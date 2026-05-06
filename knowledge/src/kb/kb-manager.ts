import fs from 'fs/promises';
import path from 'path';
import { logger } from '../utils/logger';
import { KbFileRepository } from '../db/repositories/kb-file.repository.js';
import { SQLiteVectorStore, VectorStore } from '../vector/index.js';
import DocumentParserFactory, { DocumentParseResult } from './document-parser';
import DocumentProcessor from './document-processor';
import EmbeddingModelFactory, { EmbeddingProgressCallback, IEmbeddingModel } from './embedding';

export interface KnowledgeBaseConfig {
  kbId: string;
  kbName: string;
  vectorStorePath: string;
  embeddingConfig: any;
  processingConfig?: any;
  embeddingProgressCallback?: EmbeddingProgressCallback;
}

export enum KnowledgeBaseStatus {
  INITIALIZING = 'initializing',
  READY = 'ready',
  INDEXING = 'indexing',
  ERROR = 'error',
}

export class KnowledgeBaseManager {
  private config: KnowledgeBaseConfig;
  private status: KnowledgeBaseStatus = KnowledgeBaseStatus.INITIALIZING;
  private documentProcessor: DocumentProcessor;
  private embeddingModel?: IEmbeddingModel;
  private vectorStore: VectorStore;
  private kbFileRepository?: KbFileRepository;

  constructor(config: KnowledgeBaseConfig) {
    this.config = config;
    this.documentProcessor = new DocumentProcessor(config.processingConfig);

    const dimension = config.embeddingConfig?.dimensions || 384;
    const useSQLite = process.env.VECTOR_STORE !== 'memory';
    this.vectorStore = useSQLite
      ? (new SQLiteVectorStore(dimension) as unknown as VectorStore)
      : new VectorStore();
  }

  async initialize(): Promise<void> {
    try {
      logger.info(`Initializing knowledge base: ${this.config.kbName} (${this.config.kbId})`);

      this.kbFileRepository = new KbFileRepository();
      (this.kbFileRepository as any).ensureTable();

      await this.vectorStore.load(this.config.vectorStorePath || 'data/vectors');

      this.embeddingModel = await EmbeddingModelFactory.create(
        this.config.embeddingConfig,
        this.config.embeddingProgressCallback,
      );

      this.status = KnowledgeBaseStatus.READY;
      logger.info(`Knowledge base ${this.config.kbName} initialized successfully`);
    } catch (error) {
      this.status = KnowledgeBaseStatus.ERROR;
      logger.error('Failed to initialize knowledge base:', error);
      throw error;
    }
  }

  async indexFile(filePath: string, externalFileId?: string): Promise<void> {
    logger.info(`Indexing file: ${filePath}`);
    const startTime = Date.now();
    const normalizedPath = filePath;
    let fileHash = '';
    let fileId = externalFileId;
    let parseResult: DocumentParseResult | undefined;

    try {
      fileHash = await this.calculateFileHash(normalizedPath);

      if (!externalFileId) {
        const existingFile = (this.kbFileRepository as any).findByHash?.(fileHash);
        if (existingFile) {
          logger.info(`[${normalizedPath}] File already exists with id: ${existingFile.id}, skipping`);
          return;
        }
      }

      logger.info(`[${normalizedPath}] Step 1/4: Parsing document`);
      parseResult = await DocumentParserFactory.parse(normalizedPath);
      if (!parseResult) {
        throw new Error('Document parser returned empty result');
      }

      const contentPreview = parseResult.content?.substring(0, 100) || '(empty)';
      logger.info(`[${normalizedPath}] Parsed, ${contentPreview}...`);

      if (!fileId) {
        const fileRecord = (this.kbFileRepository as any).findByPath?.(normalizedPath);
        fileId = fileRecord?.id || this.generateFileId(normalizedPath);
      }
      const currentFileId = fileId;

      logger.info(`[${normalizedPath}] Step 2/4: Chunking document`);
      const chunks = await this.documentProcessor.processDocument(currentFileId, parseResult);
      logger.info(`[${normalizedPath}] Chunked into ${chunks.length} chunks`);

      logger.info(`[${normalizedPath}] Step 3/4: Generating embeddings for ${chunks.length} chunks`);
      const chunkContents = chunks.map(chunk => chunk.content);
      const embeddings = await this.embeddingModel!.embedBatch(chunkContents);
      logger.info(`[${normalizedPath}] Generated ${embeddings.length} embeddings`);

      logger.info(`[${normalizedPath}] Step 4/4: Storing vectors`);
      for (let i = 0; i < chunks.length; i++) {
        const chunk = chunks[i];
        const embedding = embeddings[i];
        if (!chunk || !embedding) continue;

        const cleanMetadata: Record<string, any> = {
          documentId: currentFileId,
          filePath: normalizedPath,
          fileName: path.basename(normalizedPath),
          chunkIndex: chunk.index,
          content: chunk.content,
        };

        if (chunk.metadata) {
          for (const [key, value] of Object.entries(chunk.metadata)) {
            if (value !== undefined) {
              cleanMetadata[key] = value;
            }
          }
        }

        await this.vectorStore.add([embedding], [chunk.id], [cleanMetadata]);
      }

      await (this.kbFileRepository as any).upsert({
        id: currentFileId,
        file_path: normalizedPath,
        file_name: path.basename(normalizedPath),
        file_type: parseResult.metadata.fileType,
        file_size: parseResult.metadata.fileSize,
        file_hash: fileHash,
        status: 'completed',
        chunk_count: chunks.length,
      });

      await (this.vectorStore as any).save(this.config.vectorStorePath || 'data/vectors');
      logger.info(`Successfully indexed file ${normalizedPath} in ${Date.now() - startTime}ms`);
    } catch (error) {
      const stat = await this.safeStat(normalizedPath);
      await (this.kbFileRepository as any)?.upsert?.({
        id: fileId || this.generateFileId(normalizedPath),
        file_path: normalizedPath,
        file_name: path.basename(normalizedPath),
        file_type: path.extname(normalizedPath).slice(1),
        file_size: parseResult?.metadata?.fileSize || stat?.size || 0,
        file_hash: fileHash,
        status: 'failed',
        error_message: (error as Error).message,
      });

      throw error;
    }
  }

  async removeFileIndex(fileId: string): Promise<void> {
    logger.info(`Removing index for file id: ${fileId}`);

    const fileRecord = (this.kbFileRepository as any)?.findById?.(fileId);
    if (!fileRecord) {
      logger.warn(`File record not found: ${fileId}`);
      return;
    }

    await this.vectorStore.delete([`${fileId}-chunk-%`]);
    (this.kbFileRepository as any)?.delete?.(fileId);
    await (this.vectorStore as any).save(this.config.vectorStorePath || 'data/vectors');

    logger.info(`Successfully removed index for file id: ${fileId}`);
  }

  async search(query: string, topK = 10, filters?: Record<string, any>): Promise<any[]> {
    if (!this.embeddingModel) {
      throw new Error('Embedding model not initialized');
    }

    const queryEmbedding = await this.embeddingModel.embed(query);
    const results = await (this.vectorStore as any).search(queryEmbedding, topK * 2, filters);
    const filtered = results.filter((item: any) => (item.score || 0) >= 0.4);
    return filtered.slice(0, topK);
  }

  async getStats(): Promise<{
    kbId: string;
    kbName: string;
    status: KnowledgeBaseStatus;
    fileCount: number;
    chunkCount: number;
    vectorCount: number;
    vectorDimension: number;
  }> {
    const fileStats = await (this.kbFileRepository as any)?.getStats?.();
    const vectorCount = await this.vectorStore.getCount();

    return {
      kbId: this.config.kbId,
      kbName: this.config.kbName,
      status: this.status,
      fileCount: fileStats?.total || 0,
      chunkCount: vectorCount,
      vectorCount,
      vectorDimension: (this.vectorStore as any).getDimension?.() || 0,
    };
  }

  getStatus(): KnowledgeBaseStatus {
    return this.status;
  }

  async dispose(): Promise<void> {
    logger.info(`Disposing knowledge base ${this.config.kbName}`);

    if (this.embeddingModel) {
      await this.embeddingModel.dispose();
    }

    await (this.vectorStore as any).save(this.config.vectorStorePath || 'data/vectors');
    this.status = KnowledgeBaseStatus.ERROR;
  }

  private generateFileId(filePath: string): string {
    const name = path.basename(filePath).replace(/[^a-zA-Z0-9_-]/g, '-').slice(0, 40);
    return `file-${Date.now()}-${name || Math.random().toString(36).substring(2, 8)}`;
  }

  private async calculateFileHash(filePath: string): Promise<string> {
    const crypto = await import('crypto');
    const fsSync = await import('fs');
    const hash = crypto.createHash('md5');
    hash.update(fsSync.readFileSync(filePath));
    return hash.digest('hex');
  }

  private async safeStat(filePath: string) {
    try {
      return await fs.stat(filePath);
    } catch {
      return undefined;
    }
  }
}

export default KnowledgeBaseManager;
