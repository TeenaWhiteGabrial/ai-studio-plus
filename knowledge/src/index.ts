/**
 * Document management service entry.
 */

import 'dotenv/config';
import path from 'path';
import { logger } from './utils/logger.js';
import { initDatabase } from './db/index.js';
import { DocumentManagementServer } from './api/server.js';
import { KnowledgeBaseManager } from './kb/kb-manager.js';

if (process.env.TESSDATA_PREFIX) {
  process.env.TESSDATA_PREFIX = path.resolve(process.env.TESSDATA_PREFIX);
  logger.info(`TESSDATA_PREFIX set to: ${process.env.TESSDATA_PREFIX}`);
}

async function main() {
  logger.info('Starting Document Management System...');

  await initDatabase();

  const port = parseInt(process.env.PORT || '3000');
  const host = process.env.HOST || '0.0.0.0';
  const vectorStorePath = process.env.VECTOR_STORE_PATH || './data/vectors';

  const kbConfig = {
    kbId: 'default-kb',
    kbName: 'Document Knowledge Base',
    vectorStorePath: path.resolve(vectorStorePath),
    embeddingConfig: {
      modelName: process.env.EMBEDDING_MODEL || 'Xenova/bge-small-zh-v1.5',
      dimensions: parseInt(process.env.EMBEDDING_DIMENSION || '384'),
    },
  };

  const kbManager = new KnowledgeBaseManager(kbConfig);

  try {
    await kbManager.initialize();
    logger.info('Knowledge base initialized successfully');
  } catch (error) {
    logger.error('Failed to initialize knowledge base:', error);
    process.exit(1);
  }

  const server = new DocumentManagementServer({
    port,
    host,
    cors: true,
  });

  server.setKnowledgeBase(kbManager);

  try {
    await server.initialize();
    await server.start();
    logger.info(`Server is running at http://${host}:${port}`);

    const indexQueue = (await import('./kb/index-queue.js')).default;
    const pendingFiles = (kbManager as any).kbFileRepository?.findPendingOrProcessing?.() || [];

    if (pendingFiles.length > 0) {
      logger.info(`Found ${pendingFiles.length} pending tasks to recover`);

      for (const file of pendingFiles) {
        (kbManager as any).kbFileRepository?.updateStatus?.(file.id, 'pending');

        indexQueue.enqueue({
          fileId: file.id,
          filePath: file.file_path,
          fileName: file.file_name,
        }, async (fp: string, fid: string) => {
          await kbManager.indexFile(fp, fid);
        });

        indexQueue.setStatusCallback(async (fid: string, status: string, error?: string) => {
          try {
            await (kbManager as any).kbFileRepository?.updateStatus?.(fid, status, error);
          } catch (e) {
            logger.error('Failed to update status during recovery', e);
          }
        });

        logger.info(`Recovered task: ${file.file_name} (${file.id})`);
      }
    }
  } catch (error) {
    logger.error('Failed to start server:', error);
    process.exit(1);
  }

  const shutdown = async () => {
    logger.info('Shutting down...');
    await server.stop();
    process.exit(0);
  };

  process.on('SIGINT', shutdown);
  process.on('SIGTERM', shutdown);
}

main().catch((error) => {
  logger.error('Fatal error:', error);
  process.exit(1);
});
