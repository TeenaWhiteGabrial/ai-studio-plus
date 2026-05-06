/**
 * 插件加载器
 * 支持动态加载、卸载、热重载
 */

import { Plugin, PluginContext, PluginMetadata } from './plugin-base.js';
import { logger } from '../utils/logger.js';
import path from 'path';
import fs from 'fs';

export interface PluginLoaderOptions {
  /** 插件目录 */
  pluginDir: string;
  /** 是否启用热重载 */
  hotReload?: boolean;
  /** 热重载间隔（毫秒） */
  reloadInterval?: number;
}

interface LoadedPlugin {
  metadata: PluginMetadata;
  instance: Plugin;
  lastModified: number;
}

/**
 * 插件加载器
 */
export class PluginLoader {
  private plugins: Map<string, LoadedPlugin> = new Map();
  private options: Required<PluginLoaderOptions>;
  private reloadTimer?: NodeJS.Timeout;
  private context: PluginContext;

  constructor(options: PluginLoaderOptions) {
    this.options = {
      pluginDir: options.pluginDir || './plugins',
      hotReload: options.hotReload ?? false,
      reloadInterval: options.reloadInterval ?? 5000,
    };

    this.context = this.createContext();
  }

  /**
   * 创建插件上下文
   */
  private createContext(): PluginContext {
    return {
      registerService: (name: string, service: any) => {
        (this.context as any)[name] = service;
      },
      getService: (name: string) => {
        return (this.context as any)[name];
      },
      config: {},
      logger: {
        info: (msg: string, ...args: any[]) => logger.info(`[Plugin] ${msg}`, ...args),
        warn: (msg: string, ...args: any[]) => logger.warn(`[Plugin] ${msg}`, ...args),
        error: (msg: string, ...args: any[]) => logger.error(`[Plugin] ${msg}`, ...args),
        debug: (msg: string, ...args: any[]) => logger.debug(`[Plugin] ${msg}`, ...args),
      },
    };
  }

  /**
   * 加载所有插件
   */
  async loadAll(): Promise<void> {
    const pluginDir = this.options.pluginDir;

    if (!fs.existsSync(pluginDir)) {
      logger.info('Plugin directory not found, creating...', { pluginDir });
      fs.mkdirSync(pluginDir, { recursive: true });
      return;
    }

    const entries = fs.readdirSync(pluginDir, { withFileTypes: true });

    for (const entry of entries) {
      if (entry.isDirectory()) {
        const pluginPath = path.join(pluginDir, entry.name);
        await this.loadPlugin(pluginPath, entry.name);
      }
    }

    logger.info('All plugins loaded', { count: this.plugins.size });

    // 启动热重载
    if (this.options.hotReload) {
      this.startHotReload();
    }
  }

  /**
   * 加载单个插件
   */
  async loadPlugin(pluginPath: string, pluginId: string): Promise<void> {
    try {
      // 动态导入插件入口
      const pluginModule = await import(pluginPath);

      // 获取默认导出或命名导出
      const PluginClass = pluginModule.default || pluginModule.Plugin;

      if (!PluginClass) {
        throw new Error('Plugin module must export default or named "Plugin" class');
      }

      const instance = new PluginClass(this.context);

      // 初始化插件
      await instance.initialize();

      // 记录已加载插件
      const stats = fs.statSync(pluginPath);
      this.plugins.set(pluginId, {
        metadata: {
          id: pluginId,
          name: instance.name,
          version: instance.version,
          description: instance.description,
          author: instance.author,
        },
        instance,
        lastModified: stats.mtimeMs,
      });

      logger.info('Plugin loaded', { id: pluginId, name: instance.name });
    } catch (error) {
      logger.error('Failed to load plugin', { id: pluginId, error });
    }
  }

  /**
   * 卸载插件
   */
  async unloadPlugin(pluginId: string): Promise<void> {
    const plugin = this.plugins.get(pluginId);
    if (!plugin) {
      logger.warn('Plugin not found', { pluginId });
      return;
    }

    try {
      await plugin.instance.destroy();
      this.plugins.delete(pluginId);
      logger.info('Plugin unloaded', { pluginId });
    } catch (error) {
      logger.error('Failed to unload plugin', { pluginId, error });
    }
  }

  /**
   * 热重载单个插件
   */
  async reloadPlugin(pluginId: string): Promise<void> {
    const plugin = this.plugins.get(pluginId);
    if (!plugin) {
      logger.warn('Plugin not found for reload', { pluginId });
      return;
    }

    const pluginPath = path.join(this.options.pluginDir, pluginId);

    await this.unloadPlugin(pluginId);
    await this.loadPlugin(pluginPath, pluginId);

    logger.info('Plugin reloaded', { pluginId });
  }

  /**
   * 启动热重载
   */
  private startHotReload(): void {
    this.reloadTimer = setInterval(async () => {
      for (const [pluginId, plugin] of this.plugins.entries()) {
        const pluginPath = path.join(this.options.pluginDir, pluginId);
        const stats = fs.statSync(pluginPath);

        if (stats.mtimeMs > plugin.lastModified) {
          logger.info('Plugin file changed, reloading...', { pluginId });
          await this.reloadPlugin(pluginId);
        }
      }
    }, this.options.reloadInterval);

    logger.info('Hot reload started', { interval: this.options.reloadInterval });
  }

  /**
   * 停止热重载
   */
  stopHotReload(): void {
    if (this.reloadTimer) {
      clearInterval(this.reloadTimer);
      this.reloadTimer = undefined;
      logger.info('Hot reload stopped');
    }
  }

  /**
   * 获取插件
   */
  getPlugin(pluginId: string): Plugin | undefined {
    return this.plugins.get(pluginId)?.instance;
  }

  /**
   * 获取所有插件
   */
  getAllPlugins(): Plugin[] {
    return Array.from(this.plugins.values()).map(p => p.instance);
  }

  /**
   * 获取插件元数据列表
   */
  getPluginList(): PluginMetadata[] {
    return Array.from(this.plugins.values()).map(p => p.metadata);
  }

  /**
   * 销毁加载器
   */
  async destroy(): Promise<void> {
    this.stopHotReload();

    for (const [pluginId] of this.plugins) {
      await this.unloadPlugin(pluginId);
    }

    logger.info('Plugin loader destroyed');
  }
}