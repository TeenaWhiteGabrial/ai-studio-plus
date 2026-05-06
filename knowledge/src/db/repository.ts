import { sqliteClient } from './index.js'
import { randomUUID } from 'crypto'

export abstract class BaseRepository<T> {
  protected abstract tableName: string

  protected abstract toModel(row: any): T

  protected abstract toRow(model: T): Record<string, any>

  create(model: Omit<T, 'id' | 'created_at' | 'updated_at'>): T {
    const id = randomUUID()
    const now = new Date().toISOString()
    const row = this.toRow({ ...model, id, created_at: now, updated_at: now } as any)

    const keys = Object.keys(row)
    const placeholders = keys.map(() => '?').join(', ')
    const values = Object.values(row)

    sqliteClient.run(`INSERT INTO ${this.tableName} (${keys.join(', ')}) VALUES (${placeholders})`, values)

    return this.getById(id)!
  }

  getById(id: string): T | undefined {
    const row = sqliteClient.get(`SELECT * FROM ${this.tableName} WHERE id = ?`, [id])
    return row ? this.toModel(row) : undefined
  }

  update(id: string, updates: Partial<T>): T | undefined {
    const now = new Date().toISOString()
    const updateRow = this.toRow({ ...updates, updated_at: now } as any)

    const setClauses = Object.keys(updateRow)
      .filter((key) => key !== 'id' && key !== 'created_at')
      .map((key) => `${key} = ?`)
      .join(', ')

    const values = [...Object.values(updateRow).filter((_, index) => Object.keys(updateRow)[index] !== 'id' && Object.keys(updateRow)[index] !== 'created_at'), id]

    if (setClauses.length === 0) {
      return this.getById(id)
    }

    sqliteClient.run(`UPDATE ${this.tableName} SET ${setClauses} WHERE id = ?`, values)

    return this.getById(id)
  }

  delete(id: string): boolean {
    const result = sqliteClient.run(`DELETE FROM ${this.tableName} WHERE id = ?`, [id])
    return result.changes > 0
  }

  list(limit = 100, offset = 0): T[] {
    const rows = sqliteClient.all(`SELECT * FROM ${this.tableName} ORDER BY created_at DESC LIMIT ? OFFSET ?`, [limit, offset])
    return rows.map((row) => this.toModel(row))
  }

  count(): number {
    const result = sqliteClient.get<{ count: number }>(`SELECT COUNT(*) as count FROM ${this.tableName}`)
    return result?.count || 0
  }
}

// 知识库文件Repository
export interface KbFile {
  id: string
  // 兼容字段（代码中使用）
  path?: string
  name?: string
  type?: string
  size?: number
  status?: 'pending' | 'processing' | 'completed' | 'failed' | 'indexed'
  error_message?: string
  // 数据库字段
  file_path: string
  file_name: string
  file_type: string
  file_size: number
  file_hash: string
  // 索引相关字段
  vector_ids?: string
  chunk_count?: number
  last_modified?: Date
  last_indexed?: Date
  // 时间戳
  created_at: string
  updated_at: string
}

export class KbFileRepository extends BaseRepository<KbFile> {
  protected tableName = 'kb_files'

  protected toModel(row: any): KbFile {
    return {
      id: row.id,
      file_path: row.file_path,
      file_name: row.file_name,
      file_type: row.file_type,
      file_size: row.file_size,
      file_hash: row.file_hash,
      status: row.status,
      error_message: row.error_message,
      created_at: row.created_at,
      updated_at: row.updated_at,
    }
  }

  protected toRow(model: KbFile): Record<string, any> {
    return {
      id: model.id,
      file_path: model.file_path,
      file_name: model.file_name,
      file_type: model.file_type,
      file_size: model.file_size,
      file_hash: model.file_hash,
      status: model.status,
      error_message: model.error_message,
      created_at: model.created_at,
      updated_at: model.updated_at,
    }
  }

  getByFilePath(filePath: string): KbFile | undefined {
    const row = sqliteClient.get(`SELECT * FROM ${this.tableName} WHERE file_path = ?`, [filePath])
    return row ? this.toModel(row) : undefined
  }

  getByHash(fileHash: string): KbFile | undefined {
    const row = sqliteClient.get(`SELECT * FROM ${this.tableName} WHERE file_hash = ?`, [fileHash])
    return row ? this.toModel(row) : undefined
  }

  listByStatus(status: KbFile['status'], limit = 100): KbFile[] {
    const rows = sqliteClient.all(`SELECT * FROM ${this.tableName} WHERE status = ? ORDER BY created_at DESC LIMIT ?`, [status, limit])
    return rows.map((row) => this.toModel(row))
  }
}

export const kbFileRepository = new KbFileRepository()
