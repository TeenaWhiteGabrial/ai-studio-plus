import { logger } from '../utils/logger';
import { LRUCache } from 'lru-cache';
import { config as globalConfig } from '../config';

/**
 * 嵌入模型配置
 */
export interface EmbeddingConfig {
  /** 模型名称 */
  modelName: string;
  /** 向量维度 */
  dimensions: number;
  /** 批量处理最大数量 */
  batchSize: number;
  /** 是否启用缓存 */
  enableCache: boolean;
  /** 缓存大小 */
  cacheSize: number;
  /** 本地模型配置 */
  localConfig?: {
    /** 模型本地路径 */
    modelPath?: string;
    /** 是否量化 */
    quantized?: boolean;
  };
}

/**
 * 嵌入模型接口
 */
export interface IEmbeddingModel {
  initialize(): Promise<void>;
  embed(text: string): Promise<number[]>;
  embedBatch(texts: string[]): Promise<number[][]>;
  getDimension(): number;
  dispose(): Promise<void>;
}

export type EmbeddingProgressCallback = (progress: {
  status: 'downloading' | 'loading' | 'ready' | 'error';
  message: string;
  progress?: number;
}) => void;

/**
 * 默认配置
 */
const DEFAULT_CONFIG: EmbeddingConfig = {
  modelName: 'Xenova/bge-small-zh-v1.5',
  dimensions: 384,
  batchSize: 8,
  enableCache: true,
  cacheSize: 10000,
};

/**
 * 本地嵌入模型（基于@xenova/transformers）
 */
export class LocalEmbeddingModel implements IEmbeddingModel {
  private config: EmbeddingConfig;
  private cache?: LRUCache<string, number[]>;
  private pipeline: any = null;
  private transformersModule: any = null;
  private progressCallback?: EmbeddingProgressCallback;

  constructor(config: EmbeddingConfig, progressCallback?: EmbeddingProgressCallback) {
    this.config = { ...DEFAULT_CONFIG, ...config };
    this.progressCallback = progressCallback;

    if (this.config.enableCache) {
      this.cache = new LRUCache({
        max: this.config.cacheSize,
      });
    }
  }

  async initialize(): Promise<void> {
    const modelName = this.config.modelName;
    logger.info(`Initializing local embedding model: ${modelName}`);
    logger.info(`Model cache directory: ${globalConfig.EMBEDDING_CACHE_DIR}`);

    this.progressCallback?.({ status: 'downloading', message: `Loading model: ${modelName}`, progress: 10 });

    try {
      this.transformersModule = await import('@xenova/transformers');
      const remoteHost = process.env.TRANSFORMERS_REMOTE_HOST || process.env.HF_ENDPOINT;
      if (remoteHost && this.transformersModule.env) {
        this.transformersModule.env.remoteHost = remoteHost.endsWith('/') ? remoteHost : `${remoteHost}/`;
        logger.info(`Transformers remote host: ${this.transformersModule.env.remoteHost}`);
      }
      this.progressCallback?.({ status: 'loading', message: 'Initializing model...', progress: 50 });
    } catch (error) {
      throw new Error(
        'Local embedding model requires @xenova/transformers package. ' +
        'Please install it with: npm install @xenova/transformers'
      );
    }

    const { pipeline } = this.transformersModule;
    const cacheDir = this.config.localConfig?.modelPath || globalConfig.EMBEDDING_CACHE_DIR;

    this.pipeline = await pipeline('feature-extraction', this.config.modelName, {
      quantized: this.config.localConfig?.quantized ?? globalConfig.EMBEDDING_LOCAL_QUANTIZED,
      cache_dir: cacheDir,
    });

    logger.info(`Local embedding model loaded: ${this.config.modelName}`);
    this.progressCallback?.({ status: 'ready', message: 'Model ready', progress: 100 });
  }

  async embed(text: string): Promise<number[]> {
    if (this.cache?.has(text)) {
      return this.cache.get(text)!;
    }

    const results = await this.embedBatch([text]);
    const embedding = results[0];

    if (this.cache) {
      this.cache.set(text, embedding);
    }

    return embedding;
  }

  async embedBatch(texts: string[]): Promise<number[][]> {
    if (!this.pipeline) {
      throw new Error('Model not initialized');
    }

    logger.info(`[Embedding] Starting batch embedding for ${texts.length} texts, batchSize=${this.config.batchSize}`);

    const results: number[][] = [];
    const totalBatches = Math.ceil(texts.length / this.config.batchSize);

    for (let i = 0; i < texts.length; i += this.config.batchSize) {
      const batchNum = Math.floor(i / this.config.batchSize) + 1;
      const batch = texts.slice(i, i + this.config.batchSize);

      // 每10个批次打印一次进度
      if (batchNum % 10 === 1 || batchNum === totalBatches) {
        logger.info(`[Embedding] Processing batch ${batchNum}/${totalBatches} (${Math.round(batchNum/totalBatches*100)}%)`);
      }

      const outputs = await this.pipeline(batch, {
        pooling: 'mean',
        normalize: true,
      });

      for (const output of outputs) {
        results.push(Array.from(output));
      }
    }

    logger.info(`[Embedding] Completed ${results.length} embeddings`);
    return results;
  }

  getDimension(): number {
    return this.config.dimensions;
  }

  async dispose(): Promise<void> {
    if (this.cache) {
      this.cache.clear();
    }
    this.pipeline = null;
    logger.info(`Local embedding model ${this.config.modelName} disposed`);
  }
}

/**
 * 嵌入模型工厂 - 仅支持本地模型
 */
export class EmbeddingModelFactory {
  static async create(
    config: Partial<EmbeddingConfig>,
    progressCallback?: EmbeddingProgressCallback
  ): Promise<IEmbeddingModel> {
    const fullConfig = { ...DEFAULT_CONFIG, ...config };
    const model = new LocalEmbeddingModel(fullConfig, progressCallback);
    await model.initialize();
    return model;
  }
}

export default EmbeddingModelFactory;
