import { config } from '../config/index.js';
import { logger } from '../utils/logger';

interface IndexTask {
  fileId: string;
  filePath: string;
  fileName: string;
}

class IndexQueue {
  private queue: IndexTask[] = [];
  private running = 0;
  private concurrency: number;
  private onStatusChange?: (fileId: string, status: string, error?: string) => void;
  private processing = false;
  private currentIndexFn?: (filePath: string, fileId: string) => Promise<void>;

  constructor() {
    this.concurrency = config.INDEX_QUEUE_CONCURRENCY || 1;
  }

  setStatusCallback(callback: (fileId: string, status: string, error?: string) => void) {
    this.onStatusChange = callback;
  }

  async enqueue(task: IndexTask, indexFn: (filePath: string, fileId: string) => Promise<void>) {
    if (!this.currentIndexFn) {
      this.currentIndexFn = indexFn;
    }

    this.queue.push(task);
    logger.info(`Index queue: added ${task.fileName}, queue size: ${this.queue.length}`);

    if (!this.processing) {
      void this.processQueue();
    }
  }

  private async processQueue() {
    if (this.processing) return;

    this.processing = true;

    try {
      while (this.queue.length > 0 && this.running < this.concurrency) {
        const task = this.queue.shift();
        if (!task) break;

        this.running++;
        this.onStatusChange?.(task.fileId, 'processing');

        try {
          await this.currentIndexFn!(task.filePath, task.fileId);
          this.onStatusChange?.(task.fileId, 'completed');
          logger.info(`Index completed: ${task.fileName}`);
        } catch (error) {
          const errorMessage = (error as Error).message;
          this.onStatusChange?.(task.fileId, 'failed', errorMessage);
          logger.error(`Index failed: ${task.fileName}`, error);
        } finally {
          this.running--;
        }
      }
    } finally {
      this.processing = false;
      if (this.queue.length > 0) {
        void this.processQueue();
      }
    }
  }

  getQueueStatus() {
    return {
      queueSize: this.queue.length,
      running: this.running,
      concurrency: this.concurrency,
    };
  }
}

export default new IndexQueue();
