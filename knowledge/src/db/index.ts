/**
 * 数据库模块 - 统一 SQLite 存储
 * 支持向量存储、图存储的共享连接和统一事务
 */
import { logger } from '../shared/logger.js';
import { SQLiteClient, sqliteClient } from './sqlite-client.js';
import { config } from '../config/index.js';

export interface RunResult {
  changes: number;
  lastInsertRowid: number | bigint;
}

// 导出统一 SQLite 客户端
export { SQLiteClient, sqliteClient };

/**
 * 初始化统一数据库
 * 调用此函数后，可通过 sqliteClient 访问数据库
 */
export async function initDatabase(): Promise<SQLiteClient> {
  const dbPath = config.DB_PATH || './data/biaoshu.db';
  await sqliteClient.initialize(dbPath);
  return sqliteClient;
}