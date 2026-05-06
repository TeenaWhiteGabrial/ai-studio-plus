/**
 * SQLite 向量存储实现
 * 基于 sql.js 实现跨平台的向量存储和检索
 * 支持使用统一的 SQLite 客户端
 */

import initSqlJs, { Database } from 'sql.js';
import { logger } from '../shared/logger.js';
import path from 'path';
import fs from 'fs';
import { SQLiteClient } from '../db/sqlite-client.js';

export interface SearchResult {
  id: string;
  score: number;
  metadata: Record<string, any>;
}

/**
 * SQLite 向量存储类
 * 提供向量存储、相似度搜索、持久化功能
 */
export class SQLiteVectorStore {
  private db: Database | null = null;
  private dbPath: string = '';
  private dimension: number = 384; // 默认向量维度
  private initialized: boolean = false;
  private sharedClient: SQLiteClient | null = null;
  private useSharedClient: boolean = false;

  constructor(dimension: number = 384) {
    this.dimension = dimension;
  }

  /**
   * 使用共享的 SQLite 客户端
   * 这样向量存储和图存储可以使用同一个数据库连接
   */
  useClient(client: SQLiteClient): void {
    this.sharedClient = client;
    this.useSharedClient = true;
    this.db = client.getDb();
    this.initialized = true;
    logger.info('SQLiteVectorStore using shared SQLiteClient');
  }

  /**
   * 初始化数据库
   */
  async initialize(dbPath: string): Promise<void> {
    try {
      this.dbPath = dbPath;

      // 确保目录存在
      const dir = path.dirname(dbPath);
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }

      // 初始化 SQL.js
      const SQL = await initSqlJs();

      // 加载或创建数据库
      if (fs.existsSync(dbPath)) {
        const buffer = fs.readFileSync(dbPath);
        this.db = new SQL.Database(buffer);
        logger.info('Loaded existing SQLite database', { path: dbPath });
      } else {
        this.db = new SQL.Database();
        logger.info('Created new SQLite database', { path: dbPath });
      }

      // 创建表
      this.createTables();

      this.initialized = true;
      logger.info('SQLiteVectorStore initialized', { dimension: this.dimension });
    } catch (error) {
      logger.error('Failed to initialize SQLiteVectorStore', { error });
      throw error;
    }
  }

  /**
   * 创建数据库表
   */
  private createTables(): void {
    if (!this.db) return;

    // 向量表
    this.db.run(`
      CREATE TABLE IF NOT EXISTS vectors (
        id TEXT PRIMARY KEY,
        vector BLOB NOT NULL,
        metadata TEXT,
        created_at INTEGER DEFAULT (strftime('%s', 'now')),
        updated_at INTEGER DEFAULT (strftime('%s', 'now'))
      )
    `);

    // 创建索引（用于快速查找）
    this.db.run('CREATE INDEX IF NOT EXISTS idx_vectors_id ON vectors(id)');

    logger.debug('Created vector tables');
  }

  /**
   * 添加向量
   */
  async add(
    vectors: number[][],
    ids: string[],
    metadatas?: Record<string, any>[]
  ): Promise<void> {
    if (!this.db || !this.initialized) {
      throw new Error('Database not initialized');
    }

    logger.debug('SQLiteVectorStore.add called', {
      vectorsCount: vectors?.length,
      idsCount: ids?.length,
      metadatasCount: metadatas?.length,
      firstId: ids?.[0],
      firstVectorLen: vectors?.[0]?.length
    });

    for (let i = 0; i < ids.length; i++) {
      const vector = vectors[i];
      const id = ids[i];
      const metadata = metadatas?.[i];

      logger.debug('Processing vector', { i, id, vectorLen: vector?.length, metadata: !!metadata });

      // 验证数据
      if (!vector || vector.length === 0) {
        logger.warn('Skipping empty vector', { id });
        continue;
      }
      if (!id) {
        logger.warn('Skipping empty id');
        continue;
      }

      try {
        // 将向量转换为二进制存储
        const vectorBuffer = this.arrayToBuffer(vector);
        const metadataJson = metadata ? JSON.stringify(metadata) : null;

        logger.debug('About to run insert', { id, vectorBufferLen: vectorBuffer?.length, metadataJsonLen: metadataJson?.length });

        // 直接使用 db.run 而不是 prepare
        this.db.run(
          'INSERT OR REPLACE INTO vectors (id, vector, metadata, updated_at) VALUES (?, ?, ?, strftime(\'%s\', \'now\'))',
          [id, vectorBuffer, metadataJson]
        );
      } catch (err) {
        logger.error('Error adding vector', { i, id, error: err });
        throw err;
      }
    }

    this.save(); // 立即保存到磁盘
    logger.debug(`Added ${ids.length} vectors to SQLite`);
  }

  /**
   * 向量相似度搜索
   * 使用余弦相似度计算
   */
  async search(query: number[], k: number): Promise<SearchResult[]> {
    if (!this.db || !this.initialized) {
      throw new Error('Database not initialized');
    }

    const results: SearchResult[] = [];

    // 获取所有向量
    const stmt = this.db.prepare('SELECT id, vector, metadata FROM vectors');
    while (stmt.step()) {
      const row = stmt.getAsObject();
      const vector = this.bufferToArray(row.vector as Uint8Array);
      const metadata = row.metadata ? JSON.parse(row.metadata as string) : {};

      // 计算余弦相似度
      const score = this.cosineSimilarity(query, vector);

      results.push({
        id: row.id as string,
        score,
        metadata,
      });
    }
    stmt.free();

    // 排序并返回 top-k
    results.sort((a, b) => b.score - a.score);
    return results.slice(0, k);
  }

  /**
   * 批量搜索（优化版本）
   */
  async searchBatch(queries: number[][], k: number): Promise<SearchResult[][]> {
    // 简化为多次搜索
    const results: SearchResult[][] = [];
    for (const query of queries) {
      const result = await this.search(query, k);
      results.push(result);
    }
    return results;
  }

  /**
   * 删除向量
   */
  async delete(ids: string[]): Promise<void> {
    if (!this.db || !this.initialized) {
      throw new Error('Database not initialized');
    }

    for (const id of ids) {
      if (id.includes('%') || id.includes('*')) {
        // 通配符模式 - 直接使用 LIKE 删除
        const pattern = id.replace(/\*/g, '%');
        this.db.run('DELETE FROM vectors WHERE id LIKE ?', [pattern]);
      } else if (id.startsWith('file:') && !id.includes('-chunk-')) {
        // 文件前缀模式 - 转换为 LIKE 模式
        const pattern = `${id}-chunk-%`;
        this.db.run('DELETE FROM vectors WHERE id LIKE ?', [pattern]);
      } else {
        // 精确 ID 删除
        this.db.run('DELETE FROM vectors WHERE id = ?', [id]);
      }
    }

    this.save();
    logger.debug(`Deleted vectors for IDs: ${ids.join(', ')}`);
  }

  /**
   * 更新向量
   */
  async update(
    id: string,
    vector?: number[],
    metadata?: Record<string, any>
  ): Promise<void> {
    if (!this.db || !this.initialized) {
      throw new Error('Database not initialized');
    }

    if (vector) {
      const vectorBuffer = this.arrayToBuffer(vector);
      this.db.run(
        'UPDATE vectors SET vector = ?, updated_at = strftime(\'%s\', \'now\') WHERE id = ?',
        [vectorBuffer, id]
      );
    }

    if (metadata) {
      const metadataJson = JSON.stringify(metadata);
      this.db.run(
        'UPDATE vectors SET metadata = ?, updated_at = strftime(\'%s\', \'now\') WHERE id = ?',
        [metadataJson, id]
      );
    }

    this.save();
  }

  /**
   * 获取向量数量
   */
  getCount(): number {
    if (!this.db || !this.initialized) return 0;

    const result = this.db.exec('SELECT COUNT(*) as count FROM vectors');
    if (result.length > 0 && result[0].values.length > 0) {
      return result[0].values[0][0] as number;
    }
    return 0;
  }

  /**
   * 获取指定文档的向量数量
   */
  getCountByDocumentId(documentId: string): number {
    if (!this.db || !this.initialized) return 0;

    // 从 documentId 提取原始路径（去掉 "file:" 前缀和 base64 编码）
    // documentId 可能是: file:base64路径 或 file-时间戳
    let searchPath = documentId;
    if (documentId.startsWith('file:')) {
      try {
        searchPath = Buffer.from(documentId.slice(5), 'base64url').toString('utf-8');
      } catch (e) {
        // 如果不是有效的 base64url，直接使用原值
      }
    }

    // 在 metadata 的 filePath 中搜索
    const stmt = this.db.prepare(`
      SELECT COUNT(*) as count FROM vectors
      WHERE JSON_EXTRACT(metadata, "$.filePath") LIKE ?`);
    stmt.bind([`%${searchPath}%`]);
    let count = 0;
    if (stmt.step()) {
      count = stmt.get()[0] as number;
    }
    stmt.free();
    return count;
  }

  /**
   * 获取向量维度
   */
  getDimension(): number {
    return this.dimension;
  }

  /**
   * 获取存储统计信息
   */
  getStats() {
    return {
      count: this.getCount(),
      dimension: this.dimension,
      path: this.dbPath,
    };
  }

  /**
   * 保存数据库到磁盘
   */
  async save(dir?: string): Promise<void> {
    if (!this.db) return;

    const savePath = dir ? path.join(dir, 'vectors.db') : this.dbPath;
    const data = this.db.export();
    const buffer = Buffer.from(data);

    // 确保目录存在
    const saveDir = path.dirname(savePath);
    if (!fs.existsSync(saveDir)) {
      fs.mkdirSync(saveDir, { recursive: true });
    }

    fs.writeFileSync(savePath, buffer);
    logger.debug('Saved SQLite database', { path: savePath });
  }

  /**
   * 加载数据库
   */
  async load(dir: string): Promise<void> {
    const dbPath = path.join(dir, 'vectors.db');
    await this.initialize(dbPath);
  }

  /**
   * 清空向量存储
   */
  clear(): void {
    if (!this.db || !this.initialized) return;

    this.db.run('DELETE FROM vectors');
    this.save();
    logger.info('Cleared SQLite vector store');
  }

  /**
   * 关闭数据库连接
   */
  async close(): Promise<void> {
    if (this.db) {
      this.save();
      this.db.close();
      this.db = null;
      this.initialized = false;
      logger.info('Closed SQLite database');
    }
  }

  /**
   * 将数组转换为二进制缓冲区
   */
  private arrayToBuffer(arr: number[]): Uint8Array {
    const buffer = new Float32Array(arr);
    return new Uint8Array(buffer.buffer);
  }

  /**
   * 将二进制缓冲区转换为数组
   */
  private bufferToArray(buffer: Uint8Array): number[] {
    const float32Array = new Float32Array(buffer.buffer);
    return Array.from(float32Array);
  }

  /**
   * 计算余弦相似度
   */
  private cosineSimilarity(a: number[], b: number[]): number {
    if (a.length !== b.length) return 0;

    let dot = 0;
    let normA = 0;
    let normB = 0;

    for (let i = 0; i < a.length; i++) {
      dot += a[i] * b[i];
      normA += a[i] * a[i];
      normB += b[i] * b[i];
    }

    const denominator = Math.sqrt(normA) * Math.sqrt(normB);
    return denominator === 0 ? 0 : dot / denominator;
  }

  /**
   * 检查是否已初始化
   */
  isInitialized(): boolean {
    return this.initialized;
  }
}

// 导出默认实例
export const sqliteVectorStore = new SQLiteVectorStore();
export default SQLiteVectorStore;