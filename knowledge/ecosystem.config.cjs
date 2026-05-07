const path = require('path');
const cwd = __dirname;

module.exports = {
  apps: [
    {
      name: 'ai-studio-knowledge',
      script: 'pnpm',
      args: 'start',
      cwd,
      interpreter: 'none',
      instances: 1,
      autorestart: true,
      watch: false,
      max_memory_restart: '1G',
      env: {
        NODE_ENV: 'production',
        HOST: '0.0.0.0',
        PORT: 3002,
        API_PREFIX: '/ai-studio/v1/knowledge',
        EMBEDDING_MODE: 'local',
        EMBEDDING_LOCAL_MODEL: 'Xenova/bge-small-zh-v1.5',
        EMBEDDING_MODEL: 'Xenova/bge-small-zh-v1.5',
        EMBEDDING_CACHE_DIR: './data/models',
        EMBEDDING_DIMENSION: 384,
        VECTOR_STORE: 'sqlite',
        VECTOR_STORE_PATH: './data/vectors',
        DB_PATH: './data/biaoshu.db',
        KB_INPUT_PATH: './data/kb-input',
        TESSDATA_PREFIX: path.join(cwd, 'lib/tessdata'),
        HF_ENDPOINT: 'https://hf-mirror.com',
      },
      out_file: path.join(cwd, 'logs/out.log'),
      error_file: path.join(cwd, 'logs/error.log'),
      log_date_format: 'YYYY-MM-DD HH:mm:ss',
      merge_logs: true,
    },
  ],
};
