import OpenAI from 'openai'
import { config } from '../config/index.js'
import { logger } from '../shared/logger.js'
import { sqliteClient } from '../db/index.js'

export interface ChatMessage {
  role: 'system' | 'user' | 'assistant'
  content: string
}

export interface ChatOptions {
  model?: string
  temperature?: number
  max_tokens?: number
  top_p?: number
  stream?: boolean
}

export interface EmbeddingOptions {
  model?: string
}

export interface ModelConfig {
  id?: number
  name: string
  base_url: string
  api_key?: string
  model_name: string
  type: 'chat' | 'embedding'
  is_default?: boolean
}

class ModelAdapter {
  private clients: Map<string, OpenAI> = new Map()
  private defaultChatModel: string | null = null
  private defaultEmbeddingModel: string | null = null

  constructor() {
    this.loadConfigs()
    // 初始化默认模型
    if (config.OPENAI_API_KEY) {
      const defaultClient = new OpenAI({
        apiKey: config.OPENAI_API_KEY,
        baseURL: config.OPENAI_BASE_URL,
        maxRetries: 3,
        timeout: 60000,
      })
      this.clients.set('default', defaultClient)
      this.defaultChatModel = config.MODEL_NAME
      this.defaultEmbeddingModel = config.EMBEDDING_MODEL_NAME
    }
  }

  private loadConfigs() {
    try {
      const configs = sqliteClient.all<ModelConfig>('SELECT * FROM model_configs')
      for (const cfg of configs) {
        const client = new OpenAI({
          apiKey: cfg.api_key,
          baseURL: cfg.base_url,
          maxRetries: 3,
          timeout: 60000,
        })
        this.clients.set(cfg.name, client)

        if (cfg.is_default) {
          if (cfg.type === 'chat') {
            this.defaultChatModel = cfg.name
          } else if (cfg.type === 'embedding') {
            this.defaultEmbeddingModel = cfg.name
          }
        }
      }
      logger.info('模型配置加载成功', { count: configs.length })
    } catch (error) {
      logger.warn('模型配置加载失败，将使用环境变量配置', { error })
    }
  }

  async chat(messages: ChatMessage[], options: ChatOptions = {}): Promise<string> {
    const modelName = options.model || this.defaultChatModel
    if (!modelName) {
      throw new Error('未配置聊天模型')
    }

    const client = this.clients.get(modelName) || this.clients.get('default')
    if (!client) {
      throw new Error(`模型 ${modelName} 未配置`)
    }

    try {
      const response: any = await client.chat.completions.create({
        model: options.model || config.MODEL_NAME,
        messages,
        temperature: options.temperature ?? 0.7,
        max_tokens: options.max_tokens ?? 4096,
        top_p: options.top_p ?? 1,
        stream: options.stream ?? false,
      })

      const content = response.choices?.[0]?.message?.content
      if (!content) {
        throw new Error('模型返回内容为空')
      }

      logger.debug('聊天请求成功', {
        model: options.model || config.MODEL_NAME,
        inputTokens: response.usage?.prompt_tokens,
        outputTokens: response.usage?.completion_tokens,
        totalTokens: response.usage?.total_tokens,
      })

      return content
    } catch (error) {
      logger.error('聊天请求失败', { error, model: modelName })
      throw error
    }
  }

  async *chatStream(messages: ChatMessage[], options: ChatOptions = {}): AsyncGenerator<string> {
    const modelName = options.model || this.defaultChatModel
    if (!modelName) {
      throw new Error('未配置聊天模型')
    }

    const client = this.clients.get(modelName) || this.clients.get('default')
    if (!client) {
      throw new Error(`模型 ${modelName} 未配置`)
    }

    try {
      const stream = await client.chat.completions.create({
        model: options.model || config.MODEL_NAME,
        messages,
        temperature: options.temperature ?? 0.7,
        max_tokens: options.max_tokens ?? 4096,
        top_p: options.top_p ?? 1,
        stream: true,
      })

      for await (const chunk of stream) {
        const content = chunk.choices[0]?.delta?.content
        if (content) {
          yield content
        }
      }
    } catch (error) {
      logger.error('流式聊天请求失败', { error, model: modelName })
      throw error
    }
  }

  async embed(text: string | string[], options: EmbeddingOptions = {}): Promise<number[][]> {
    const modelName = options.model || this.defaultEmbeddingModel
    if (!modelName) {
      throw new Error('未配置嵌入模型')
    }

    const client = this.clients.get(modelName) || this.clients.get('default')
    if (!client) {
      throw new Error(`模型 ${modelName} 未配置`)
    }

    try {
      const texts = Array.isArray(text) ? text : [text]
      const response = await client.embeddings.create({
        model: options.model || config.EMBEDDING_MODEL_NAME,
        input: texts,
        encoding_format: 'float',
      })

      const embeddings = response.data.map((item: { embedding: number[] }) => item.embedding)
      logger.debug('嵌入请求成功', {
        model: options.model || config.EMBEDDING_MODEL_NAME,
        count: texts.length,
        tokens: response.usage?.total_tokens,
      })

      return embeddings
    } catch (error) {
      logger.error('嵌入请求失败', { error, model: modelName })
      throw error
    }
  }

  // 模型配置管理
  addModelConfig(config: Omit<ModelConfig, 'id'>): number {
    const result = sqliteClient.run(
      `INSERT INTO model_configs (name, base_url, api_key, model_name, type, is_default)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [config.name, config.base_url, config.api_key, config.model_name, config.type, config.is_default ? 1 : 0]
    )

    // 如果设为默认，需要取消其他同类型模型的默认状态
    if (config.is_default) {
      sqliteClient.run(`UPDATE model_configs SET is_default = 0 WHERE type = ? AND id != ?`, [config.type, result.lastInsertRowid])
    }

    this.loadConfigs()
    return result.lastInsertRowid as number
  }

  updateModelConfig(id: number, updates: Partial<ModelConfig>): void {
    const setClauses: string[] = []
    const values: any[] = []

    if (updates.name !== undefined) {
      setClauses.push('name = ?')
      values.push(updates.name)
    }
    if (updates.base_url !== undefined) {
      setClauses.push('base_url = ?')
      values.push(updates.base_url)
    }
    if (updates.api_key !== undefined) {
      setClauses.push('api_key = ?')
      values.push(updates.api_key)
    }
    if (updates.model_name !== undefined) {
      setClauses.push('model_name = ?')
      values.push(updates.model_name)
    }
    if (updates.type !== undefined) {
      setClauses.push('type = ?')
      values.push(updates.type)
    }
    if (updates.is_default !== undefined) {
      setClauses.push('is_default = ?')
      values.push(updates.is_default ? 1 : 0)
    }

    if (setClauses.length === 0) return

    values.push(id)

    sqliteClient.run(`UPDATE model_configs SET ${setClauses.join(', ')} WHERE id = ?`, values)

    // 如果设为默认，需要取消其他同类型模型的默认状态
    if (updates.is_default) {
      const cfg = sqliteClient.get<ModelConfig>('SELECT type FROM model_configs WHERE id = ?', [id])
      if (cfg) {
        sqliteClient.run(`UPDATE model_configs SET is_default = 0 WHERE type = ? AND id != ?`, [cfg.type, id])
      }
    }

    this.loadConfigs()
  }

  deleteModelConfig(id: number): void {
    sqliteClient.run('DELETE FROM model_configs WHERE id = ?', [id])
    this.loadConfigs()
  }

  listModelConfigs(): ModelConfig[] {
    return sqliteClient.all<ModelConfig>('SELECT * FROM model_configs ORDER BY id')
  }
}

// 单例实例
export const modelAdapter = new ModelAdapter()
