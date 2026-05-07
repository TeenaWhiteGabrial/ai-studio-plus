declare module 'sql.js' {
  export type SqlValue = string | number | bigint | Uint8Array | null

  export interface QueryExecResult {
    columns: string[]
    values: SqlValue[][]
  }

  export interface Statement {
    bind(values?: SqlValue[]): boolean
    step(): boolean
    get(): SqlValue[]
    getAsObject(): Record<string, unknown>
    free(): void
  }

  export class Database {
    constructor(data?: Uint8Array)
    run(sql: string, params?: unknown[]): Database
    exec(sql: string): QueryExecResult[]
    prepare(sql: string, params?: unknown[]): Statement
    getRowsModified(): number
    export(): Uint8Array
    close(): void
  }

  export interface SqlJsStatic {
    Database: typeof Database
  }

  export default function initSqlJs(config?: Record<string, unknown>): Promise<SqlJsStatic>
}
