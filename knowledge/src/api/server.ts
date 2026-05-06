import Fastify, { FastifyInstance, FastifyReply, FastifyRequest } from 'fastify';
import fastifyCors from '@fastify/cors';
import fastifyMultipart from '@fastify/multipart';
import fs from 'fs/promises';
import path from 'path';
import { logger } from '../utils/logger';
import { KnowledgeBaseManager } from '../kb/kb-manager';

export interface APIConfig {
  port: number;
  host: string;
  cors: boolean;
  apiKey?: string;
  apiPrefix: string;
}

const DEFAULT_CONFIG: APIConfig = {
  port: 3000,
  host: '0.0.0.0',
  cors: true,
  apiPrefix: '/ai-studio/v1/knowledge',
};

function normalizePrefix(prefix: string) {
  const value = prefix.trim() || DEFAULT_CONFIG.apiPrefix;
  return `/${value.replace(/^\/+|\/+$/g, '')}`;
}

function ok<T>(data: T, message?: string) {
  return { success: true, data, message };
}

function fail(reply: FastifyReply, status: number, error: string) {
  return reply.status(status).send({ success: false, error });
}

export class DocumentManagementServer {
  private app: FastifyInstance;
  private config: APIConfig;
  private kbManager?: KnowledgeBaseManager;

  constructor(config?: Partial<APIConfig>) {
    this.config = {
      ...DEFAULT_CONFIG,
      ...config,
      apiPrefix: normalizePrefix(config?.apiPrefix || process.env.API_PREFIX || DEFAULT_CONFIG.apiPrefix),
    };
    this.app = Fastify({ logger: false });
  }

  async initialize(): Promise<void> {
    if (this.config.cors) {
      await this.app.register(fastifyCors, { origin: true });
    }

    await this.app.register(fastifyMultipart, {
      limits: {
        fileSize: 50 * 1024 * 1024,
      },
    });

    this.registerRoutes();

    this.app.setErrorHandler((error: Error, _request: FastifyRequest, reply: FastifyReply) => {
      logger.error('API Error:', error);
      reply.status(500).send({
        success: false,
        error: (error as Error).message || String(error),
      });
    });
  }

  private route(pathname: string) {
    if (!pathname || pathname === '/') {
      return this.config.apiPrefix;
    }
    return `${this.config.apiPrefix}${pathname.startsWith('/') ? pathname : `/${pathname}`}`;
  }

  private registerRoutes() {
    this.app.get('/', async () => this.apiInfo());
    this.app.get(this.route('/'), async () => this.apiInfo());

    this.app.get(this.route('/health'), async () => ({
      status: 'ok',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
    }));

    this.app.post(this.route('/search'), async (request: FastifyRequest, reply: FastifyReply) => {
      const { query, topK = 10, filters } = request.body as any;

      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }
      if (!query) {
        return fail(reply, 400, 'Query is required');
      }

      const results = await this.kbManager.search(query, topK || 10, filters);
      return ok(results);
    });

    this.app.get(this.route('/stats'), async (_request: FastifyRequest, reply: FastifyReply) => {
      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }

      const stats = await this.kbManager.getStats();
      const indexQueue = await import('../kb/index-queue.js');
      const queueStatus = indexQueue.default.getQueueStatus();

      return ok({
        ...stats,
        queueStatus,
      });
    });

    this.app.post(this.route('/upload'), async (request: FastifyRequest, reply: FastifyReply) => {
      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }

      const data = await request.file();
      if (!data) {
        return fail(reply, 400, 'No file uploaded');
      }

      const uploadDir = process.env.KB_INPUT_PATH || path.join(process.cwd(), 'data', 'kb-input');
      await fs.mkdir(uploadDir, { recursive: true });

      const filename = data.filename;
      const filePath = path.join(uploadDir, filename);
      const fileId = `file-${Date.now()}`;
      const buffer = await data.toBuffer();
      await fs.writeFile(filePath, buffer);

      const kbFileRepo = (this.kbManager as any).kbFileRepository;
      const fileExt = filename.split('.').pop() || '';
      await kbFileRepo?.upsert?.({
        id: fileId,
        file_path: filePath,
        file_name: filename,
        file_type: fileExt,
        file_size: buffer.length,
        file_hash: '',
        status: 'pending',
        chunk_count: 0,
      });

      const indexQueue = (await import('../kb/index-queue.js')).default;
      indexQueue.setStatusCallback(async (fid: string, status: string, error?: string) => {
        try {
          await kbFileRepo?.updateStatus?.(fid, status, error);
        } catch (e) {
          logger.error('Failed to update status', e);
        }
      });

      indexQueue.enqueue({
        fileId,
        filePath,
        fileName: filename,
      }, async (fp: string, fid: string) => {
        await this.kbManager!.indexFile(fp, fid);
      });

      return ok({
        id: fileId,
        filename,
        path: filePath,
        size: buffer.length,
        status: 'pending',
      });
    });

    this.app.get(this.route('/documents'), async (_request: FastifyRequest, reply: FastifyReply) => {
      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }

      const kbFileRepo = (this.kbManager as any).kbFileRepository;
      if (!kbFileRepo) {
        return fail(reply, 500, 'File repository not initialized');
      }

      const files = kbFileRepo.findAll?.() || [];
      const fileList = files.map((file: any) => {
        const vectorCount = (this.kbManager as any).vectorStore?.getCountByDocumentId
          ? (this.kbManager as any).vectorStore.getCountByDocumentId(file.file_path)
          : file.chunk_count || 0;

        return {
          id: file.id,
          fileName: file.file_name,
          filePath: file.file_path,
          fileType: file.file_type,
          fileSize: file.file_size,
          status: file.status,
          chunkCount: file.chunk_count || vectorCount,
          indexedAt: file.indexed_at,
          errorMessage: file.error_message,
        };
      });

      return ok(fileList);
    });

    this.app.get(this.route('/documents/:id'), async (request: FastifyRequest, reply: FastifyReply) => {
      const id = (request.params as any).id;

      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }

      const kbFileRepo = (this.kbManager as any).kbFileRepository;
      const file = kbFileRepo?.findById?.(id);
      if (!file) {
        return fail(reply, 404, 'Document not found');
      }

      return ok({
        id: file.id,
        fileName: file.file_name,
        filePath: file.file_path,
        fileType: file.file_type,
        fileSize: file.file_size,
        status: file.status,
        chunkCount: file.chunk_count,
        indexedAt: file.indexed_at,
        errorMessage: file.error_message,
      });
    });

    this.app.delete(this.route('/documents/:id'), async (request: FastifyRequest, reply: FastifyReply) => {
      const id = (request.params as any).id;

      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }

      try {
        await this.kbManager.removeFileIndex(id);
        return ok({ message: 'Document deleted' });
      } catch (error) {
        return fail(reply, 500, `Delete failed: ${(error as Error).message}`);
      }
    });

    this.app.post(this.route('/documents/:id/reindex'), async (request: FastifyRequest, reply: FastifyReply) => {
      const id = (request.params as any).id;

      if (!this.kbManager) {
        return fail(reply, 500, 'Knowledge base not initialized');
      }

      const kbFileRepo = (this.kbManager as any).kbFileRepository;
      const file = kbFileRepo?.findById?.(id);
      if (!file) {
        return fail(reply, 404, 'Document not found');
      }

      try {
        await this.kbManager.removeFileIndex(id);
        kbFileRepo?.updateStatus?.(id, 'pending');

        const indexQueue = (await import('../kb/index-queue.js')).default;
        indexQueue.setStatusCallback(async (fid: string, status: string, error?: string) => {
          try {
            await kbFileRepo?.updateStatus?.(fid, status, error);
          } catch (e) {
            logger.error('Failed to update status during reindex', e);
          }
        });

        indexQueue.enqueue({
          fileId: id,
          filePath: file.file_path,
          fileName: file.file_name,
        }, async (fp: string, fid: string) => {
          await this.kbManager!.indexFile(fp, fid);
        });

        return ok({ message: 'Document reindexing' });
      } catch (error) {
        return fail(reply, 500, `Reindex failed: ${(error as Error).message}`);
      }
    });

    this.app.get(this.route('/config'), async () => ok({
      port: this.config.port,
      host: this.config.host,
      apiPrefix: this.config.apiPrefix,
      uploadPath: process.env.KB_INPUT_PATH || './data/kb-input',
      vectorStorePath: process.env.VECTOR_STORE_PATH || './data/vectors',
      embeddingModel: process.env.EMBEDDING_MODEL || 'Xenova/bge-small-zh-v1.5',
    }));
  }

  private apiInfo() {
    const prefix = this.config.apiPrefix;
    return ok({
      name: 'Document Knowledge Base API',
      version: '1.0.0',
      apiPrefix: prefix,
      endpoints: [
        `GET ${prefix}/health`,
        `POST ${prefix}/search`,
        `GET ${prefix}/stats`,
        `POST ${prefix}/upload`,
        `GET ${prefix}/documents`,
        `GET ${prefix}/documents/:id`,
        `DELETE ${prefix}/documents/:id`,
        `POST ${prefix}/documents/:id/reindex`,
        `GET ${prefix}/config`,
      ],
    });
  }

  setKnowledgeBase(kbManager: KnowledgeBaseManager): void {
    this.kbManager = kbManager;
  }

  async start(): Promise<any> {
    await this.app.listen({
      port: this.config.port,
      host: this.config.host,
    });
    logger.info(`Document management server started on http://${this.config.host}:${this.config.port}${this.config.apiPrefix}`);
    return this.app.server;
  }

  async stop(): Promise<void> {
    if (this.kbManager) {
      await this.kbManager.dispose();
    }
    await this.app.close();
    logger.info('Document management server stopped');
  }
}

export default DocumentManagementServer;
