/**
 * 向量存储模块 - 支持内存和 SQLite 存储
 */

import { logger } from '../shared/logger.js';
import path from 'path';
import fs from 'fs';
import { SQLiteVectorStore } from './sqlite-vector-store.js';

export interface VectorRecord {
  id: string;
  vector: number[];
  metadata: Record<string, any>;
}

export interface SearchResult {
  id: string;
  score: number;
  metadata: Record<string, any>;
}

/**
 * 向量存储抽象类
 */
export class VectorStore {
  private vectors: Map<string, number[]> = new Map();
  private metadata: Map<string, Record<string, any>> = new Map();

  async add(vectors: number[][], ids: string[], metadatas?: Record<string, any>[]): Promise<void> {
    for (let i = 0; i < ids.length; i++) {
      this.vectors.set(ids[i], vectors[i]);
      if (metadatas && metadatas[i]) {
        this.metadata.set(ids[i], metadatas[i]);
      }
    }
    logger.debug(`Added ${ids.length} vectors to store`);
  }

  async search(query: number[], k: number): Promise<SearchResult[]> {
    const results: SearchResult[] = [];
    for (const [id, vec] of this.vectors) {
      const score = this.cosineSimilarity(query, vec);
      results.push({
        id,
        score,
        metadata: this.metadata.get(id) || {}
      });
    }
    results.sort((a, b) => b.score - a.score);
    return results.slice(0, k);
  }

  async delete(ids: string[]): Promise<void> {
    for (const id of ids) {
      this.vectors.delete(id);
      this.metadata.delete(id);
    }
  }

  async save(dir: string): Promise<void> {
    const data = {
      vectors: Object.fromEntries(this.vectors),
      metadata: Object.fromEntries(this.metadata),
    };
    const filePath = path.join(dir, 'vector-store.json');
    await fs.promises.writeFile(filePath, JSON.stringify(data, null, 2));
    logger.info('Vector store saved', { path: filePath });
  }

  async load(dir: string): Promise<void> {
    const filePath = path.join(dir, 'vector-store.json');
    try {
      const data = await fs.promises.readFile(filePath, 'utf-8');
      const parsed = JSON.parse(data);
      this.vectors = new Map(Object.entries(parsed.vectors || {}));
      this.metadata = new Map(Object.entries(parsed.metadata || {}));
      logger.info('Vector store loaded', { count: this.vectors.size });
    } catch (e) {
      logger.warn('Failed to load vector store', { error: e });
    }
  }

  getCount(): number {
    return this.vectors.size;
  }

  getStats() {
    return { count: this.vectors.size };
  }

  clear(): void {
    this.vectors.clear();
    this.metadata.clear();
  }

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
    return dot / (Math.sqrt(normA) * Math.sqrt(normB) || 1);
  }
}

// 导出 SQLiteVectorStore
export { SQLiteVectorStore };

// 默认导出：根据环境选择存储方式
// 默认使用内存存储（为了向后兼容）
// 如需使用 SQLite，在初始化时创建 SQLiteVectorStore 实例
export const vectorStore = new VectorStore();
export default vectorStore;