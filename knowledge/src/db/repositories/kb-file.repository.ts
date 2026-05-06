/**
 * 知识库文件仓库
 */
import { sqliteClient } from '../index.js';

const db = sqliteClient;

export interface KbFile {
  id: string;
  file_path: string;
  file_name: string;
  file_type: string;
  file_size: number;
  file_hash: string;
  status: string;
  error_message?: string;
  chunk_count?: number;
  created_at: Date;
  updated_at: Date;
}

export class KbFileRepository {
  private initialized = false;

  private ensureTable(): void {
    if (this.initialized) return;
    // 初始化表
    sqliteClient.run(`
      CREATE TABLE IF NOT EXISTS kb_files (
        id TEXT PRIMARY KEY,
        file_path TEXT NOT NULL,
        file_name TEXT NOT NULL,
        file_type TEXT NOT NULL,
        file_size INTEGER NOT NULL,
        file_hash TEXT,
        status TEXT NOT NULL DEFAULT 'pending',
        error_message TEXT,
        chunk_count INTEGER DEFAULT 0,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
      )
    `);
    // 保存数据库到文件
    sqliteClient.save();
    this.initialized = true;
  }

  upsert(file: Omit<KbFile, 'created_at' | 'updated_at'>): number {
    this.ensureTable();
    // 优先通过路径查找，其次通过 hash 查找
    let existing = this.findByPath(file.file_path);
    if (!existing && file.file_hash) {
      existing = this.findByHash(file.file_hash);
    }
    const errorMessage = file.error_message || null;
    const chunkCount = file.chunk_count || 0;
    if (existing) {
      sqliteClient.run(
        `UPDATE kb_files SET file_name = ?, file_type = ?, file_size = ?, file_hash = ?, status = ?, error_message = ?, chunk_count = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
        [file.file_name, file.file_type, file.file_size, file.file_hash || null, file.status, errorMessage, chunkCount, existing.id]
      );
      sqliteClient.save();
      return existing.id as unknown as number;
    } else {
      const result = this.create(file);
      sqliteClient.save();
      return result;
    }
  }

  create(file: Omit<KbFile, 'created_at' | 'updated_at'>): number {
    const errorMessage = file.error_message || null;
    const fileHash = file.file_hash || null;
    const chunkCount = file.chunk_count || 0;
    return sqliteClient.run(
      `INSERT INTO kb_files (id, file_path, file_name, file_type, file_size, file_hash, status, error_message, chunk_count)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [file.id, file.file_path, file.file_name, file.file_type, file.file_size, fileHash, file.status, errorMessage, chunkCount]
    ).lastInsertRowid as number;
  }

  findById(id: string): KbFile | undefined {
    return db.get<KbFile>('SELECT * FROM kb_files WHERE id = ?', [id]);
  }

  findByPath(path: string): KbFile | undefined {
    return db.get<KbFile>('SELECT * FROM kb_files WHERE file_path = ?', [path]);
  }

  findByHash(hash: string): KbFile | undefined {
    return db.get<KbFile>('SELECT * FROM kb_files WHERE file_hash = ?', [hash]);
  }

  findAll(): KbFile[] {
    return db.all<KbFile>('SELECT * FROM kb_files ORDER BY created_at DESC');
  }

  findByStatus(status: string): KbFile[] {
    return db.all<KbFile>('SELECT * FROM kb_files WHERE status = ? ORDER BY created_at DESC', [status]);
  }

  findPendingOrProcessing(): KbFile[] {
    return db.all<KbFile>("SELECT * FROM kb_files WHERE status IN ('pending', 'processing') ORDER BY created_at ASC");
  }

  updateStatus(id: string, status: string, errorMessage?: string): void {
    db.run(
      'UPDATE kb_files SET status = ?, error_message = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?',
      [status, errorMessage || null, id]
    );
    sqliteClient.save();
  }

  delete(id: string): void {
    db.run('DELETE FROM kb_files WHERE id = ?', [id]);
  }

  getStats() {
    const total = db.all<{ count: number }>('SELECT COUNT(*) as count FROM kb_files');
    const completed = db.all<{ count: number }>("SELECT COUNT(*) as count FROM kb_files WHERE status = 'completed'");
    const failed = db.all<{ count: number }>("SELECT COUNT(*) as count FROM kb_files WHERE status = 'failed'");
    const totalChunks = db.all<{ total: number }>('SELECT SUM(chunk_count) as total FROM kb_files WHERE status = ?', ['completed']);
    return {
      total: total[0]?.count || 0,
      completed: completed[0]?.count || 0,
      failed: failed[0]?.count || 0,
      totalChunks: totalChunks[0]?.total || 0,
    };
  }
}

export const kbFileRepository = new KbFileRepository();