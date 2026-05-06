import path from 'path';
import { logger } from '../utils/logger';
import { DocumentParseResult } from './document-parser';

/**
 * 文档分片
 */
export interface DocumentChunk {
  /** 分片ID */
  id: string;
  /** 原始文档ID */
  documentId: string;
  /** 分片内容 */
  content: string;
  /** 分片在原文档中的起始位置 */
  startIndex: number;
  /** 分片在原文档中的结束位置 */
  endIndex: number;
  /** 分片序号 */
  index: number;
  /** 元数据 */
  metadata: Record<string, any>;
  /** 抽取的实体 */
  entities: Entity[];
  /** 分片的向量表示（后续填充） */
  embedding?: number[];
}

/**
 * 实体类型
 */
export enum EntityType {
  PERSON = 'person',
  ORGANIZATION = 'organization',
  LOCATION = 'location',
  DATE = 'date',
  NUMBER = 'number',
  EMAIL = 'email',
  PHONE = 'phone',
  URL = 'url',
  PRODUCT = 'product',
  TECHNOLOGY = 'technology',
  PROJECT = 'project',
  KEYWORD = 'keyword',
  CUSTOM = 'custom',
}

/**
 * 实体
 */
export interface Entity {
  /** 实体文本 */
  text: string;
  /** 实体类型 */
  type: EntityType | string;
  /** 置信度 */
  confidence: number;
  /** 在文本中的起始位置 */
  startIndex: number;
  /** 在文本中的结束位置 */
  endIndex: number;
  /** 元数据 */
  metadata?: Record<string, any>;
}

/**
 * 分片配置
 */
export interface ChunkingConfig {
  /** 最大分片长度（字符数） */
  maxChunkSize: number;
  /** 重叠长度（字符数） */
  chunkOverlap: number;
  /** 优先分隔符 */
  separators: string[];
  /** 是否保留分隔符 */
  keepSeparators: boolean;
}

/**
 * 文档处理配置
 */
export interface DocumentProcessingConfig {
  chunking: ChunkingConfig;
  /** 是否启用实体抽取 */
  enableEntityExtraction: boolean;
  /** 是否启用元数据提取 */
  enableMetadataExtraction: boolean;
  /** 自定义实体抽取规则 */
  customEntityRules?: EntityExtractionRule[];
}

/**
 * 实体抽取规则
 */
export interface EntityExtractionRule {
  /** 规则名称 */
  name: string;
  /** 实体类型 */
  type: EntityType | string;
  /** 匹配正则表达式 */
  pattern: RegExp;
  /** 置信度 */
  confidence: number;
}

/**
 * 默认分片配置
 */
const DEFAULT_CHUNKING_CONFIG: ChunkingConfig = {
  maxChunkSize: 1000,
  chunkOverlap: 100,
  separators: ['\n\n', '\n', '。', '！', '？', '.', '!', '?', ' ', ''],
  keepSeparators: true,
};

/**
 * 默认处理配置
 */
const DEFAULT_PROCESSING_CONFIG: DocumentProcessingConfig = {
  chunking: DEFAULT_CHUNKING_CONFIG,
  enableEntityExtraction: true,
  enableMetadataExtraction: true,
  customEntityRules: [],
};

/**
 * 内置实体抽取规则
 */
const BUILTIN_ENTITY_RULES: EntityExtractionRule[] = [
  // 邮箱
  {
    name: 'email',
    type: EntityType.EMAIL,
    pattern: /\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b/g,
    confidence: 0.95,
  },
  // 手机号
  {
    name: 'phone',
    type: EntityType.PHONE,
    pattern: /\b1[3-9]\d{9}\b/g,
    confidence: 0.9,
  },
  // URL
  {
    name: 'url',
    type: EntityType.URL,
    pattern: /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/g,
    confidence: 0.95,
  },
  // 日期
  {
    name: 'date',
    type: EntityType.DATE,
    pattern: /\d{4}[-/年]\d{1,2}[-/月]\d{1,2}日?/g,
    confidence: 0.85,
  },
];

/**
 * 文档处理器
 * 实现文档处理管道：分片 → 元数据提取 → 实体抽取
 */
export class DocumentProcessor {
  private config: DocumentProcessingConfig;
  private entityRules: EntityExtractionRule[];

  constructor(config?: Partial<DocumentProcessingConfig>) {
    this.config = { ...DEFAULT_PROCESSING_CONFIG, ...config };
    this.entityRules = [...BUILTIN_ENTITY_RULES, ...(this.config.customEntityRules || [])];
  }

  /**
   * 处理文档，完整管道
   * @param documentId 文档ID
   * @param parseResult 文档解析结果
   */
  async processDocument(
    documentId: string,
    parseResult: DocumentParseResult
  ): Promise<DocumentChunk[]> {
    logger.debug(`Processing document: ${documentId}`);
    const startTime = Date.now();

    try {
      // 1. 文档分片
      const chunks = this.splitIntoChunks(documentId, parseResult.content);

      // 2. 处理每个分片
      const processedChunks: DocumentChunk[] = [];
      for (const chunk of chunks) {
        let processedChunk = { ...chunk };

        // 提取元数据
        if (this.config.enableMetadataExtraction) {
          processedChunk.metadata = {
            ...processedChunk.metadata,
            ...this.extractMetadata(chunk.content, parseResult),
          };
        }

        // 抽取实体
        if (this.config.enableEntityExtraction) {
          processedChunk.entities = this.extractEntities(chunk.content);
        }

        processedChunks.push(processedChunk);
      }

      logger.debug(
        `Document processing complete: ${documentId}, generated ${processedChunks.length} chunks in ${Date.now() - startTime}ms`
      );

      return processedChunks;
    } catch (error) {
      logger.error(`Error processing document ${documentId}:`, error);
      throw new Error(`Document processing failed: ${(error as Error).message}`);
    }
  }

  /**
   * 将文本拆分为分片
   * 参考LangChain的递归字符拆分逻辑，优先保留语义完整性
   */
  splitIntoChunks(documentId: string, text: string): DocumentChunk[] {
    const { maxChunkSize, chunkOverlap, separators, keepSeparators } = this.config.chunking;
    const chunks: DocumentChunk[] = [];

    // 递归拆分函数
    const recursiveSplit = (
      inputText: string,
      separators: string[],
      startOffset: number = 0
    ): string[] => {
      if (!inputText) return [];

      let separator = separators[separators.length - 1];
      let nextSeparators = separators;

      // 找到第一个匹配的分隔符
      for (let i = 0; i < separators.length; i++) {
        const s = separators[i];
        if (s === '') {
          separator = s;
          break;
        }
        if (inputText.includes(s)) {
          separator = s;
          nextSeparators = separators.slice(i + 1);
          break;
        }
      }

      // 使用分隔符拆分
      let splits: string[] = [];
      if (separator) {
        splits = inputText.split(separator);
      } else {
        // 空分隔符按字符拆分
        splits = inputText.split('');
      }

      const goodSplits: string[] = [];
      const currentChunk: string[] = [];

      for (const s of splits) {
        const combined = currentChunk.join(separator) + (keepSeparators && separator ? separator : '') + s;

        if (combined.length > maxChunkSize) {
          if (currentChunk.length > 0) {
            const chunkText = currentChunk.join(separator) + (keepSeparators && separator ? separator : '');
            goodSplits.push(chunkText);

            // 处理重叠：保留最后几个字符作为下一个分片的开头
            if (chunkOverlap > 0) {
              const overlapText = chunkText.slice(-chunkOverlap);
              currentChunk.length = 0;
              currentChunk.push(overlapText);
            } else {
              currentChunk.length = 0;
            }
          }

          // 如果单个拆分已经超过最大长度，递归用更细的分隔符拆分
          if (s.length > maxChunkSize) {
            const subSplits = recursiveSplit(s, nextSeparators, startOffset + combined.length - s.length);
            goodSplits.push(...subSplits);
          } else {
            currentChunk.push(s);
          }
        } else {
          currentChunk.push(s);
        }
      }

      // 添加最后一个分片
      if (currentChunk.length > 0) {
        goodSplits.push(currentChunk.join(separator) + (keepSeparators && separator ? separator : ''));
      }

      return goodSplits;
    };

    // 执行拆分
    const splitTexts = recursiveSplit(text, separators);

    // 构建分片对象
    let currentIndex = 0;
    for (let i = 0; i < splitTexts.length; i++) {
      const chunkText = splitTexts[i].trim();
      if (!chunkText) continue;

      const startIndex = text.indexOf(chunkText, currentIndex);
      const endIndex = startIndex + chunkText.length;

      chunks.push({
        id: `${documentId}-chunk-${i}`,
        documentId,
        content: chunkText,
        startIndex,
        endIndex,
        index: i,
        metadata: {},
        entities: [],
      });

      currentIndex = endIndex - chunkOverlap;
    }

    return chunks;
  }

  /**
   * 从文本中提取元数据
   */
  extractMetadata(text: string, parseResult: DocumentParseResult): Record<string, any> {
    const fileName = path.basename(parseResult.filePath);
    const metadata: Record<string, any> = {
      sourceFile: parseResult.filePath,
      fileName: fileName,
      fileType: parseResult.metadata.fileType,
      wordCount: text.split(/\s+/).length,
      charCount: text.length,
    };

    // 尝试提取标题（第一行）
    const firstLine = text.split('\n')[0]?.trim();
    if (firstLine && firstLine.length < 200) {
      metadata.suggestedTitle = firstLine;
    }

    // 提取常用关键字
    const keywords = this.extractKeywords(text, 10);
    if (keywords.length > 0) {
      metadata.keywords = keywords;
    }

    return metadata;
  }

  /**
   * 提取关键词（简单实现，基于词频）
   */
  private extractKeywords(text: string, maxCount: number = 10): string[] {
    // 简单实现：去除标点，统计词频
    const cleanText = text.replace(/[^\w\u4e00-\u9fa5\s]/g, '');
    const words = cleanText.split(/\s+/).filter(w => w.length > 1);

    const freq: Record<string, number> = {};
    for (const word of words) {
      freq[word] = (freq[word] || 0) + 1;
    }

    return Object.entries(freq)
      .sort((a, b) => b[1] - a[1])
      .slice(0, maxCount)
      .map(([word]) => word);
  }

  /**
   * 抽取实体
   */
  extractEntities(text: string): Entity[] {
    const entities: Entity[] = [];
    const usedIndices = new Set<string>(); // 避免重复匹配

    for (const rule of this.entityRules) {
      const matches = text.matchAll(rule.pattern);
      for (const match of matches) {
        if (match.index === undefined) continue;

        const text = match[0];
        const startIndex = match.index;
        const endIndex = startIndex + text.length;
        const key = `${startIndex}-${endIndex}`;

        // 跳过已经匹配过的位置
        if (usedIndices.has(key)) continue;
        usedIndices.add(key);

        entities.push({
          text,
          type: rule.type,
          confidence: rule.confidence,
          startIndex,
          endIndex,
          metadata: {
            rule: rule.name,
          },
        });
      }
    }

    // 按位置排序
    return entities.sort((a, b) => a.startIndex - b.startIndex);
  }

  /**
   * 注册自定义实体抽取规则
   */
  addEntityRule(rule: EntityExtractionRule): void {
    this.entityRules.push(rule);
  }

  /**
   * 移除实体抽取规则
   */
  removeEntityRule(ruleName: string): void {
    this.entityRules = this.entityRules.filter(r => r.name !== ruleName);
  }
}

// 默认导出
export default DocumentProcessor;
