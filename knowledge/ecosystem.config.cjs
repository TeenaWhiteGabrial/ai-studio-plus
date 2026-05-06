module.exports = {
  apps: [
    {
      name: 'ai-studio-knowledge',
      script: 'npm',
      args: 'run start',
      cwd: '/data/ai-studio/knowledge',
      interpreter: 'none',
      instances: 1,
      autorestart: true,
      watch: false,
      max_memory_restart: '1G',
      env: {
        NODE_ENV: 'production',
        HOST: '0.0.0.0',
        PORT: 3000,
        API_PREFIX: '/ai-studio/v1/knowledge',
        EMBEDDING_MODE: 'local',
        EMBEDDING_CACHE_DIR: './data/models',
        TESSDATA_PREFIX: '/data/ai-studio/knowledge/lib/tessdata',
      },
      out_file: '/data/ai-studio/knowledge/logs/out.log',
      error_file: '/data/ai-studio/knowledge/logs/error.log',
      log_date_format: 'YYYY-MM-DD HH:mm:ss',
      merge_logs: true,
    },
  ],
};
