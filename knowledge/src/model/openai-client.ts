/**
 * OpenAI客户端工厂 - 为CLI提供兼容接口
 */

import { modelAdapter } from './index.js';
import OpenAI from 'openai';

export interface ClientConfig {
  id: string;
  name: string;
  type: string;
  baseURL: string;
  apiKey: string;
  defaultModel: string;
}

export interface OpenAIClient {
  chat: {
    completions: {
      create: (options: any) => Promise<any>;
    };
  };
  embeddings?: {
    create: (options: any) => Promise<any>;
  };
}

/**
 * 创建OpenAI兼容客户端
 */
export async function createOpenAIClient(config: ClientConfig): Promise<OpenAIClient> {
  // 直接创建OpenAI客户端
  const client = new OpenAI({
    apiKey: config.apiKey,
    baseURL: config.baseURL,
    maxRetries: 3,
    timeout: 120000,
  });

  return {
    chat: {
      completions: {
        create: async (options: any) => {
          return client.chat.completions.create({
            model: options.model || config.defaultModel,
            messages: options.messages,
            temperature: options.temperature,
            max_tokens: options.max_tokens,
            stream: options.stream || false,
          });
        },
      },
    },
    embeddings: {
      create: async (options: any) => {
        return client.embeddings.create({
          model: options.model || config.defaultModel,
          input: options.input,
        });
      },
    },
  };
}

/**
 * OpenAI客户端工厂
 */
export const OpenAIClientFactory = {
  create: async (config: ClientConfig) => {
    return createOpenAIClient(config);
  },
};

export default OpenAIClientFactory;