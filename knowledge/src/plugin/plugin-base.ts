/**
 * 插件系统基础接口
 */

export interface Plugin {
  /** 插件唯一标识 */
  id: string;
  /** 插件名称 */
  name: string;
  /** 插件版本 */
  version: string;
  /** 插件描述 */
  description?: string;
  /** 插件作者 */
  author?: string;
  /** 插件依赖 */
  dependencies?: string[];
  /** 插件初始化 */
  initialize(): Promise<void> | void;
  /** 插件销毁 */
  destroy(): Promise<void> | void;
}

export interface PluginMetadata {
  id: string;
  name: string;
  version: string;
  description?: string;
  author?: string;
}

export interface PluginContext {
  /** 注册服务 */
  registerService(name: string, service: any): void;
  /** 获取服务 */
  getService(name: string): any;
  /** 配置文件 */
  config: Record<string, any>;
  /** 日志 */
  logger: {
    info: (msg: string, ...args: any[]) => void;
    warn: (msg: string, ...args: any[]) => void;
    error: (msg: string, ...args: any[]) => void;
    debug: (msg: string, ...args: any[]) => void;
  };
}