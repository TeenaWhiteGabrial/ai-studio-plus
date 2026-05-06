/**
 * 向量存储模块
 */

export interface VectorStore {
  add(vectors: number[][], ids: string[]): Promise<void>;
  search(query: number[], k: number): Promise<{ id: string; score: number }[]>;
  delete(ids: string[]): Promise<void>;
  save(path: string): Promise<void>;
  load(path: string): Promise<void>;
}

/**
 * 内存向量存储实现
 */
export class InMemoryVectorStore implements VectorStore {
  private vectors: Map<string, number[]> = new Map();

  async add(vectors: number[][], ids: string[]): Promise<void> {
    for (let i = 0; i < ids.length; i++) {
      this.vectors.set(ids[i], vectors[i]);
    }
  }

  async search(query: number[], k: number): Promise<{ id: string; score: number }[]> {
    const results: { id: string; score: number }[] = [];
    for (const [id, vec] of this.vectors) {
      const score = this.cosineSimilarity(query, vec);
      results.push({ id, score });
    }
    results.sort((a, b) => b.score - a.score);
    return results.slice(0, k);
  }

  async delete(ids: string[]): Promise<void> {
    for (const id of ids) {
      this.vectors.delete(id);
    }
  }

  async save(path: string): Promise<void> {
    const data = Object.fromEntries(this.vectors);
    const fs = await import('fs/promises');
    await fs.writeFile(path, JSON.stringify(data));
  }

  async load(path: string): Promise<void> {
    try {
      const fs = await import('fs/promises');
      const data = await fs.readFile(path, 'utf-8');
      const parsed = JSON.parse(data);
      this.vectors = new Map(Object.entries(parsed));
    } catch {
      // 文件不存在，忽略
    }
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

export const vectorStore = new InMemoryVectorStore();
export default vectorStore;