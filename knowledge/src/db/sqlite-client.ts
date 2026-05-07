/**
 * 统一 SQLite 客户端
 * 提供统一的数据库连接管理，支持向量存储、图存储的共享连接和事务
 */
import initSqlJs, { Database, SqlJsStatic } from 'sql.js';
import { logger } from '../shared/logger.js';
import path from 'path';
import fs from 'fs';

export interface RunResult {
  changes: number;
  lastInsertRowid: number | bigint;
}

export interface TransactionResult<T> {
  success: boolean;
  result?: T;
  error?: Error;
}

/**
 * 统一 SQLite 客户端
 * 管理单个数据库连接，供向量存储和图存储共享使用
 */
export class SQLiteClient {
  private db: Database | null = null;
  private SQL: SqlJsStatic | null = null;
  private dbPath: string = '';
  private initialized: boolean = false;

  constructor() {}

  /**
   * 初始化数据库连接
   */
  async initialize(dbPath: string): Promise<void> {
    if (this.initialized) {
      logger.warn('SQLiteClient already initialized');
      return;
    }

    try {
      this.dbPath = dbPath;

      // 确保目录存在
      const dir = path.dirname(dbPath);
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }

      // 初始化 SQL.js
      this.SQL = await initSqlJs();

      // 加载或创建数据库
      if (fs.existsSync(dbPath)) {
        const buffer = fs.readFileSync(dbPath);
        this.db = new this.SQL.Database(buffer);
        logger.info('Loaded existing SQLite database', { path: dbPath });
      } else {
        this.db = new this.SQL.Database();
        logger.info('Created new SQLite database', { path: dbPath });
      }

      // 启用外键约束
      this.db.run('PRAGMA foreign_keys = ON');

      this.initialized = true;
      logger.info('SQLiteClient initialized', { path: dbPath });
    } catch (error) {
      logger.error('Failed to initialize SQLiteClient', { error });
      throw error;
    }
  }

  /**
   * 获取数据库实例
   */
  getDb(): Database {
    if (!this.db || !this.initialized) {
      throw new Error('Database not initialized. Call initialize() first.');
    }
    return this.db;
  }

  /**
   * 执行单条 SQL 语句
   */
  run(sql: string, params: any[] = []): RunResult {
    if (!this.db) throw new Error('Database not initialized');

    this.db.run(sql, params);

    // 获取最后插入的行ID和影响的行数
    const lastId = (this.db.exec('SELECT last_insert_rowid() as id')[0]?.values[0]?.[0] || 0) as number | bigint;
    const changes = this.db.getRowsModified();

    return {
      changes,
      lastInsertRowid: lastId,
    };
  }

  /**
   * 查询单条记录
   */
  get<T = any>(sql: string, params: any[] = []): T | undefined {
    if (!this.db) throw new Error('Database not initialized');

    const stmt = this.db.prepare(sql);
    stmt.bind(params);

    if (stmt.step()) {
      const row = stmt.getAsObject();
      stmt.free();
      return row as T;
    }

    stmt.free();
    return undefined;
  }

  /**
   * 查询多条记录
   */
  all<T = any>(sql: string, params: any[] = []): T[] {
    if (!this.db) throw new Error('Database not initialized');

    const results: T[] = [];
    const stmt = this.db.prepare(sql);
    stmt.bind(params);

    while (stmt.step()) {
      results.push(stmt.getAsObject() as T);
    }

    stmt.free();
    return results;
  }

  /**
   * 执行多条 SQL（事务中）
   */
  exec(sql: string): void {
    if (!this.db) throw new Error('Database not initialized');
    this.db.exec(sql);
  }

  /**
   * 执行事务
   * @param fn 事务回调，在回调中执行数据库操作
   * @returns 事务执行结果
   */
  transaction<T>(fn: () => T): TransactionResult<T> {
    if (!this.db) {
      return { success: false, error: new Error('Database not initialized') };
    }

    try {
      this.db.run('BEGIN TRANSACTION');
      const result = fn();
      this.db.run('COMMIT');
      return { success: true, result };
    } catch (error) {
      this.db.run('ROLLBACK');
      logger.error('Transaction failed', { error });
      return { success: false, error: error as Error };
    }
  }

  /**
   * 保存数据库到文件
   */
  async save(): Promise<void> {
    if (!this.db || !this.dbPath) {
      throw new Error('Database not initialized');
    }

    const data = this.db.export();
    const buffer = Buffer.from(data);

    // 确保目录存在
    const dir = path.dirname(this.dbPath);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }

    fs.writeFileSync(this.dbPath, buffer);
    logger.info('Database saved', { path: this.dbPath });
  }

  /**
   * 关闭数据库连接
   */
  close(): void {
    if (this.db) {
      this.db.close();
      this.db = null;
      this.initialized = false;
      logger.info('Database connection closed');
    }
  }

  /**
   * 检查是否已初始化
   */
  isInitialized(): boolean {
    return this.initialized;
  }
}

// 单例实例
export const sqliteClient = new SQLiteClient();
export default sqliteClient;
