import dotenv from 'dotenv';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { z } from 'zod';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

dotenv.config({ path: path.join(__dirname, '../../.env') });

let configJson: Record<string, any> = {};
const configJsonPath = path.join(__dirname, '../../config.json');

if (fs.existsSync(configJsonPath)) {
  try {
    configJson = JSON.parse(fs.readFileSync(configJsonPath, 'utf-8'));
  } catch (error) {
    console.warn('Failed to load config.json:', error);
  }
}

const configSchema = z.object({
  PORT: z.coerce.number().default(3000),
  HOST: z.string().default('0.0.0.0'),
  API_PREFIX: z.string().default('/ai-studio/v1/knowledge'),

  OPENAI_API_KEY: z.string().optional(),
  OPENAI_BASE_URL: z.string().default('https://api.openai.com/v1'),
  MODEL_NAME: z.string().default('MiniMax-M2.5'),

  EMBEDDING_MODE: z.enum(['local', 'remote']).default('local'),
  EMBEDDING_MODEL_NAME: z.string().default('bge-small-zh-v1.5'),
  EMBEDDING_LOCAL_MODEL: z.string().default('Xenova/bge-small-zh-v1.5'),
  EMBEDDING_LOCAL_QUANTIZED: z.boolean().default(true),
  EMBEDDING_CACHE_DIR: z.string().default('./data/models'),
  EMBEDDING_DIMENSION: z.coerce.number().default(384),

  VECTOR_STORE: z.enum(['sqlite', 'memory']).default('sqlite'),
  VECTOR_STORE_PATH: z.string().default('./data/vectors'),
  VECTOR_INDEX_PATH: z.string().default('./data/vector'),
  VECTOR_DIMENSION: z.coerce.number().default(384),

  KNOWLEDGE_BASE_PATH: z.string().default('./data/knowledge'),
  KB_INPUT_PATH: z.string().default('./data/kb-input'),
  UPLOAD_PATH: z.string().default('./data/uploads'),
  EXPORT_PATH: z.string().default('./data/exports'),
  DB_PATH: z.string().default('./data/biaoshu.db'),

  LOG_LEVEL: z.enum(['error', 'warn', 'info', 'debug']).default('info'),
  TOP_K: z.coerce.number().default(5),
  INDEX_QUEUE_CONCURRENCY: z.coerce.number().default(1),
});

export type Config = z.infer<typeof configSchema>;

function getConfigValue(key: string, defaultValue: any): any {
  if (process.env[key] !== undefined) {
    return process.env[key];
  }

  const keys = key.split('_');
  let value: any = configJson;

  for (const item of keys) {
    if (value && typeof value === 'object' && item in value) {
      value = value[item];
    } else {
      value = undefined;
      break;
    }
  }

  return value ?? defaultValue;
}

const envConfig = {
  PORT: getConfigValue('PORT', 3000),
  HOST: getConfigValue('HOST', '0.0.0.0'),
  API_PREFIX: getConfigValue('API_PREFIX', '/ai-studio/v1/knowledge'),

  OPENAI_API_KEY: getConfigValue('OPENAI_API_KEY', undefined),
  OPENAI_BASE_URL: getConfigValue('OPENAI_BASE_URL', 'https://api.openai.com/v1'),
  MODEL_NAME: getConfigValue('MODEL_NAME', 'MiniMax-M2.5'),

  EMBEDDING_MODE: getConfigValue('EMBEDDING_MODE', 'local'),
  EMBEDDING_MODEL_NAME: getConfigValue('EMBEDDING_MODEL_NAME', 'bge-small-zh-v1.5'),
  EMBEDDING_LOCAL_MODEL: getConfigValue('EMBEDDING_LOCAL_MODEL', 'Xenova/bge-small-zh-v1.5'),
  EMBEDDING_LOCAL_QUANTIZED: getConfigValue('EMBEDDING_LOCAL_QUANTIZED', 'true') === 'true',
  EMBEDDING_CACHE_DIR: getConfigValue('EMBEDDING_CACHE_DIR', './data/models'),
  EMBEDDING_DIMENSION: getConfigValue('EMBEDDING_DIMENSION', 384),

  VECTOR_STORE: getConfigValue('VECTOR_STORE', 'sqlite'),
  VECTOR_STORE_PATH: getConfigValue('VECTOR_STORE_PATH', './data/vectors'),
  VECTOR_INDEX_PATH: getConfigValue('VECTOR_INDEX_PATH', './data/vector'),
  VECTOR_DIMENSION: getConfigValue('VECTOR_DIMENSION', 384),

  KNOWLEDGE_BASE_PATH: getConfigValue('KNOWLEDGE_BASE_PATH', './data/knowledge'),
  KB_INPUT_PATH: getConfigValue('KB_INPUT_PATH', './data/kb-input'),
  UPLOAD_PATH: getConfigValue('UPLOAD_PATH', './data/uploads'),
  EXPORT_PATH: getConfigValue('EXPORT_PATH', './data/exports'),
  DB_PATH: getConfigValue('DB_PATH', './data/biaoshu.db'),

  LOG_LEVEL: getConfigValue('LOG_LEVEL', 'info'),
  TOP_K: getConfigValue('TOP_K', 5),
  INDEX_QUEUE_CONCURRENCY: getConfigValue('INDEX_QUEUE_CONCURRENCY', 1),
};

export const config = configSchema.parse(envConfig);

const dirs = [
  config.KNOWLEDGE_BASE_PATH,
  config.KB_INPUT_PATH,
  config.UPLOAD_PATH,
  config.EXPORT_PATH,
  config.VECTOR_INDEX_PATH,
  config.VECTOR_STORE_PATH,
  config.EMBEDDING_CACHE_DIR,
  path.dirname(config.DB_PATH),
];

for (const dir of dirs) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

export function validateConfig(): { valid: boolean; errors: string[]; warnings: string[] } {
  const errors: string[] = [];
  const warnings: string[] = [];

  if (!config.OPENAI_API_KEY && config.EMBEDDING_MODE === 'remote') {
    warnings.push('OPENAI_API_KEY is not configured, remote embedding is unavailable.');
  }

  if (config.PORT < 1 || config.PORT > 65535) {
    errors.push('PORT must be between 1 and 65535.');
  }

  if (config.VECTOR_DIMENSION < 1 || config.VECTOR_DIMENSION > 4096) {
    errors.push('VECTOR_DIMENSION must be between 1 and 4096.');
  }

  return {
    valid: errors.length === 0,
    errors,
    warnings,
  };
}

export async function checkNetworkStatus(): Promise<{ online: boolean; latency?: number }> {
  const testUrls = ['https://api.openai.com', 'https://huggingface.co'];

  for (const url of testUrls) {
    try {
      const start = Date.now();
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000);

      const response = await fetch(url, {
        method: 'HEAD',
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      if (response.ok) {
        return { online: true, latency: Date.now() - start };
      }
    } catch {
      // Try next URL.
    }
  }

  return { online: false };
}
