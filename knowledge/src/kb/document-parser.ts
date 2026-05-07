import path from 'path';
import fs from 'fs/promises';
import fsSync from 'fs';
import { logger } from '../utils/logger';
import mammoth from 'mammoth';
import * as xlsx from 'xlsx';
import JSZip from 'jszip';
import Tesseract from 'tesseract.js';

// 全局捕获 tesseract Worker 错误，防止进程崩溃
process.on('unhandledRejection', (reason, promise) => {
  const errorMsg = String(reason);
  if (errorMsg.includes('tesseract') || errorMsg.includes('pix') || errorMsg.includes('Image file')) {
    logger.warn('Tesseract worker error captured globally', { error: errorMsg.substring(0, 200) });
  }
});

process.on('uncaughtException', (error) => {
  const errorMsg = String(error);
  if (errorMsg.includes('tesseract') || errorMsg.includes('pix') || errorMsg.includes('Image file')) {
    logger.warn('Tesseract error captured globally', { error: errorMsg.substring(0, 200) });
    // 不退出进程，让主流程继续
    return;
  }
  // 其他未捕获的错误仍然抛出
  throw error;
});

/**
 * 文档元数据
 */
export interface DocumentMetadata {
  title?: string;
  author?: string;
  creator?: string;
  producer?: string;
  creationDate?: Date;
  modificationDate?: Date;
  pageCount?: number;
  wordCount?: number;
  fileSize: number;
  fileType: string;
  /** 是否启用了 OCR */
  ocrEnabled?: boolean;
  /** OCR 提取的文字块数量 */
  ocrTextBlockCount?: number;
  [key: string]: any;
}

/**
 * 文档解析结果
 */
export interface DocumentParseResult {
  /** 纯文本内容 */
  content: string;
  /** 元数据 */
  metadata: DocumentMetadata;
  /** 原始文件路径 */
  filePath: string;
  /** 解析器类型 */
  parserType: string;
  /** 解析耗时（毫秒） */
  parseTime: number;
  /** OCR 提取的文本块列表（用于来源追溯） */
  ocrTextBlocks?: OCRTextBlock[];
}

/**
 * OCR 提取的文本块
 */
export interface OCRTextBlock {
  /** 文本内容 */
  text: string;
  /** 图片类型：架构图/界面截图/表格 */
  imageType: 'architecture' | 'screenshot' | 'table' | 'unknown';
  /** 来源标识 */
  source: string;
  /** 原始位置（页码/段落索引） */
  position?: string;
}

/**
 * 文档解析器接口
 */
export interface IDocumentParser {
  /** 支持的文件扩展名 */
  supportedExtensions: string[];
  /**
   * 解析文档
   * @param filePath 文件路径
   * @param options 解析选项
   */
  parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult>;
}

/**
 * DOCX文档解析器
 */
export class DocxParser implements IDocumentParser {
  supportedExtensions = ['.docx'];

  async parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult> {
    const startTime = Date.now();
    const stats = await fs.stat(filePath);

    try {
      const result = await mammoth.extractRawText({ path: filePath });
      const content = result.value;

      // 提取元数据
      const metadata: DocumentMetadata = {
        fileSize: stats.size,
        fileType: 'docx',
        wordCount: content.trim().split(/\s+/).length,
      };

      return {
        content,
        metadata,
        filePath,
        parserType: 'docx',
        parseTime: Date.now() - startTime,
      };
    } catch (error) {
      logger.error(`Failed to parse DOCX file ${filePath}:`, error);
      throw new Error(`DOCX parsing failed: ${(error as Error).message}`);
    }
  }
}

/**
 * PDF文档解析器 - 使用 pdfjs-dist 流式解析
 */
export class PdfParser implements IDocumentParser {
  supportedExtensions = ['.pdf'];

  async parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult> {
    const startTime = Date.now();
    const stats = await fs.stat(filePath);

    // 使用 legacy 版本
    const pdfjsLib = await import('pdfjs-dist/legacy/build/pdf.mjs');
    const pdfPath = path.resolve(filePath);

    logger.info(`[PDF] Loading PDF with pdfjs: ${filePath}`);

    try {
      // 加载 PDF 文档
      const loadingTask = pdfjsLib.getDocument(pdfPath);
      const pdfDocument = await loadingTask.promise;
      const totalPages = pdfDocument.numPages;

      logger.info(`[PDF] Total pages: ${totalPages}, parsing in batches...`);

      // 分批解析，每批 10 页
      const BATCH_SIZE = 10;
      let fullContent = '';
      let title = '';
      let author = '';
      let creator = '';

      for (let batchStart = 1; batchStart <= totalPages; batchStart += BATCH_SIZE) {
        const batchEnd = Math.min(batchStart + BATCH_SIZE - 1, totalPages);
        logger.info(`[PDF] Parsing pages ${batchStart}-${batchEnd}/${totalPages}`);

        for (let pageNum = batchStart; pageNum <= batchEnd; pageNum++) {
          try {
            const page = await pdfDocument.getPage(pageNum);
            const textContent = await page.getTextContent();

            // 提取页面文本
            const pageText = textContent.items
              .map((item: any) => item.str)
              .join(' ')
              .trim();

            if (pageText) {
              fullContent += pageText + '\n';
            }

            // 获取元数据（只在第一页获取）
            if (pageNum === 1) {
              const metadata = await pdfDocument.getMetadata();
              const info = metadata.info as { Title?: string; Author?: string; Creator?: string } | undefined;
              if (info) {
                title = info.Title || '';
                author = info.Author || '';
                creator = info.Creator || '';
              }
            }
          } catch (pageError) {
            logger.warn(`[PDF] Failed to parse page ${pageNum}:`, pageError);
          }
        }

        // 每批处理完成后释放内存
        logger.info(`[PDF] Batch ${batchStart}-${batchEnd} done, content length: ${fullContent.length}`);
      }

      // 清理
      await pdfDocument.destroy();

      const content = fullContent;
      const wordCount = content.trim().split(/\s+/).filter(w => w.length > 0).length;

      const metadata: DocumentMetadata = {
        title,
        author,
        creator,
        producer: '',
        pageCount: totalPages,
        fileSize: stats.size,
        fileType: 'pdf',
        wordCount,
      };

      logger.info(`[PDF] Parsed ${totalPages} pages, ${wordCount} words, ${content.length} chars`);

      return {
        content,
        metadata,
        filePath,
        parserType: 'pdf',
        parseTime: Date.now() - startTime,
      };
    } catch (error) {
      logger.error(`Failed to parse PDF file ${filePath}:`, error);
      throw new Error(`PDF parsing failed: ${(error as Error).message}`);
    }
  }
}

/**
 * Excel文档解析器（xlsx/xls）
 */
export class ExcelParser implements IDocumentParser {
  supportedExtensions = ['.xlsx', '.xls', '.csv'];

  async parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult> {
    const startTime = Date.now();
    const stats = await fs.stat(filePath);

    try {
      const workbook = xlsx.readFile(filePath);
      let content = '';

      // 遍历所有工作表
      for (const sheetName of workbook.SheetNames) {
        const worksheet = workbook.Sheets[sheetName];
        // 转换为CSV格式文本
        const sheetCsv = xlsx.utils.sheet_to_csv(worksheet);
        if (sheetCsv.trim()) {
          content += `=== 工作表: ${sheetName} ===\n${sheetCsv}\n\n`;
        }
      }

      const metadata: DocumentMetadata = {
        fileSize: stats.size,
        fileType: path.extname(filePath).toLowerCase().slice(1),
        sheetCount: workbook.SheetNames.length,
        wordCount: content.trim().split(/\s+/).length,
      };

      return {
        content,
        metadata,
        filePath,
        parserType: 'excel',
        parseTime: Date.now() - startTime,
      };
    } catch (error) {
      logger.error(`Failed to parse Excel file ${filePath}:`, error);
      throw new Error(`Excel parsing failed: ${(error as Error).message}`);
    }
  }
}

/**
 * 纯文本文档解析器（txt/md）
 */
export class TextParser implements IDocumentParser {
  supportedExtensions = ['.txt', '.md', '.json', '.csv', '.xml', '.html', '.htm'];

  async parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult> {
    const startTime = Date.now();
    const stats = await fs.stat(filePath);

    try {
      const content = await fs.readFile(filePath, 'utf-8');

      const metadata: DocumentMetadata = {
        fileSize: stats.size,
        fileType: path.extname(filePath).toLowerCase().slice(1),
        wordCount: content.trim().split(/\s+/).length,
        lineCount: content.split('\n').length,
      };

      return {
        content,
        metadata,
        filePath,
        parserType: 'text',
        parseTime: Date.now() - startTime,
      };
    } catch (error) {
      // 如果UTF-8解码失败，尝试用GBK解码
      try {
        const iconv = await import('iconv-lite');
        const buffer = await fs.readFile(filePath);
        const content = iconv.decode(buffer, 'gbk');

        const metadata: DocumentMetadata = {
          fileSize: stats.size,
          fileType: path.extname(filePath).toLowerCase().slice(1),
          wordCount: content.trim().split(/\s+/).length,
          lineCount: content.split('\n').length,
          encoding: 'gbk',
        };

        return {
          content,
          metadata,
          filePath,
          parserType: 'text',
          parseTime: Date.now() - startTime,
        };
      } catch (gbkError) {
        logger.error(`Failed to parse text file ${filePath}:`, error);
        throw new Error(`Text parsing failed: ${(error as Error).message}`);
      }
    }
  }
}

/**
 * OfficeParser 统一文档解析器
 * 支持 docx, pptx, xlsx, pdf, odt, odp, ods, rtf
 * PDF 使用 OfficeParser 提取更完整（异步任务，效果优先）
 * 自动启用 OCR 提取图片中的文字（架构图、系统截图等）
 */
export class OfficeParserAdapter implements IDocumentParser {
  supportedExtensions = ['.docx', '.doc', '.pptx', '.xlsx', '.pdf', '.odt', '.odp', '.ods', '.rtf'];

  /**
   * 需要启用 OCR 的文件类型（包含图片的文档格式）
   */
  private static readonly OCR_EXTENSIONS = ['.docx', '.doc', '.pptx', '.pdf'];

  /**
   * 支持图片提取的格式
   */
  private static readonly IMAGE_EXTRACTABLE_EXTENSIONS = ['.docx', '.doc', '.pptx'];

  async parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult> {
    const startTime = Date.now();
    const stats = await fs.stat(filePath);
    const ext = path.extname(filePath).toLowerCase();

    // 自动启用 OCR 的文件类型
    const enableOCR = OfficeParserAdapter.OCR_EXTENSIONS.includes(ext);

    try {
      // 动态导入 officeparser
      logger.info(`[OfficeParser] Step 1/5: Loading officeparser module...`);
      const officeparser = await import('officeparser');
      logger.info(`[OfficeParser] Step 1/5: Module loaded`);

      // 启用 OCR 选项（文档内图片文字识别）
      // 优先使用 options 中的值，否则根据文件类型自动启用
      const parseOptions = {
        ocr: options?.ocr ?? enableOCR,
        ocrLanguage: options?.ocrLanguage ?? 'chi_sim+eng',
      };

      logger.debug('OfficeParser parse options', { filePath, parseOptions, enableOCR });

      const ocrEnabled = parseOptions.ocr;

      if (ocrEnabled) {
        logger.info(`[OfficeParser] Step 2/5: OCR enabled (language: ${parseOptions.ocrLanguage}), will be handled by officeparser internally`);
      } else {
        logger.info(`[OfficeParser] Step 2/5: OCR disabled`);
      }

      const parseStartTime = Date.now();
      logger.info(`[OfficeParser] Step 3/5: Parsing document (file: ${path.basename(filePath)}, size: ${stats.size} bytes, ocr: ${ocrEnabled})`);

      const data = await officeparser.parseOffice(filePath, parseOptions);

      const parseDuration = Date.now() - parseStartTime;
      logger.info(`[OfficeParser] Step 3/5: Document parsing completed in ${parseDuration}ms`);

      // 从结构化数据中提取纯文本（适合嵌入）
      logger.info(`[OfficeParser] Step 4/5: Extracting text...`);
      const extractStartTime = Date.now();
      const { text, ocrTextBlocks } = this.extractTextWithOCR(data, ocrEnabled);
      logger.info(`[OfficeParser] Step 4/5: Text extracted (${text.length} chars) in ${Date.now() - extractStartTime}ms`);

      // 如果启用 OCR 且是支持图片提取的格式，额外提取图片 OCR
      logger.info(`[OfficeParser] Step 5/5: Processing images (if any)...`);
      let additionalOCRBlocks: OCRTextBlock[] = [];
      if (ocrEnabled && OfficeParserAdapter.IMAGE_EXTRACTABLE_EXTENSIONS.includes(ext)) {
        try {
          const imageOCRResult = await this.extractAndOCRImages(filePath, parseOptions.ocrLanguage);
          additionalOCRBlocks = imageOCRResult;
          logger.debug('Image OCR completed', { filePath, blockCount: additionalOCRBlocks.length });
        } catch (err) {
          // OCR 失败不影响主流程
          logger.warn('Image OCR failed, continuing without image OCR', { filePath, error: (err as Error).message });
        }
      }

      // 合并 OCR 文本块
      const allOCRBlocks = [...ocrTextBlocks, ...additionalOCRBlocks];

      const metadata: DocumentMetadata = {
        fileSize: stats.size,
        fileType: ext.slice(1),
        wordCount: text.trim().split(/\s+/).length,
        ocrEnabled,
        ocrTextBlockCount: allOCRBlocks.length,
      };

      // 将 OCR 结果附加到内容后面
      const fullContent = this.combineContentWithOCR(text, allOCRBlocks);

      return {
        content: fullContent,
        metadata,
        filePath,
        parserType: 'officeparser',
        parseTime: Date.now() - startTime,
        ocrTextBlocks: allOCRBlocks,
      };
    } catch (error) {
      logger.error('OfficeParser parse error', { filePath, error });
      throw error;
    }
  }

  /**
   * 从 DOCX/PPTX 中提取图片并运行 OCR
   */
  private async extractAndOCRImages(
    filePath: string,
    ocrLanguage: string
  ): Promise<OCRTextBlock[]> {
    const ocrTextBlocks: OCRTextBlock[] = [];

    try {
      // 读取文件
      const buffer = await fs.readFile(filePath);
      const zip = await JSZip.loadAsync(buffer);

      // 查找图片文件
      const imageFolder = zip.folder('word/media') || zip.folder('ppt/media');
      if (!imageFolder) {
        return ocrTextBlocks;
      }

      const imageFiles = (Object.values(imageFolder.files) as any[]).filter(file =>
        /\.(png|jpg|jpeg|gif|emf|wmf)$/i.test(file.name)
      );

      if (imageFiles.length === 0) {
        return ocrTextBlocks;
      }

      logger.debug('Found images in document', { filePath, count: imageFiles.length });

      // 只处理常见图片格式，跳过可能导致 tesseract 崩溃的格式
      // emf/wmf 是 Windows 图元格式，tesseract 可能无法处理
      const supportedImageTypes = ['png', 'jpg', 'jpeg', 'gif', 'bmp'];
      const validImageFiles = imageFiles.filter(file => {
        const ext = path.extname(file.name).toLowerCase().slice(1);
        return supportedImageTypes.includes(ext);
      });

      if (validImageFiles.length === 0) {
        logger.debug('No supported images for OCR', { filePath });
        return ocrTextBlocks;
      }

      // 对每张图片运行 OCR - 使用独立错误处理防止崩溃
      const tempDir = path.join('/tmp', `ocr-${Date.now()}`);
      fsSync.mkdirSync(tempDir, { recursive: true });

      for (let i = 0; i < validImageFiles.length; i++) {
        const file = validImageFiles[i];
        const imageName = path.basename(file.name);
        const ext = path.extname(imageName).toLowerCase().slice(1);
        const imagePath = path.join(tempDir, `img_${i}.${ext}`);

        try {
          // 提取图片数据
          const imageData = await file.async('uint8array');
          fsSync.writeFileSync(imagePath, Buffer.from(imageData));

          // 运行 OCR - 添加超时和错误处理
          const result = await Promise.race([
            Tesseract.recognize(imagePath, ocrLanguage, {
              logger: () => {},
              tessdata: process.env.TESSDATA_PREFIX || './lib/tessdata',
            } as any),
            new Promise((_, reject) =>
              setTimeout(() => reject(new Error('OCR timeout')), 30000)
            )
          ]) as any;

          const text = result?.data?.text?.trim() || '';
          if (text && text.length > 5) {
            // 检测图片类型
            const imageType = this.detectImageType(text);
            ocrTextBlocks.push({
              text,
              imageType,
              source: `ocr-${this.getImageTypeLabel(imageType)}`,
              position: `image-${i + 1}`,
            });
          }
        } catch (err) {
          // 单个图片 OCR 失败不影响其他图片
          logger.debug('OCR failed for image, continuing', { imageName, error: (err as Error).message });
        }
      }

      // 清理临时文件
      try {
        fsSync.rmSync(tempDir, { recursive: true, force: true });
      } catch {
        // 忽略清理失败
      }

    } catch (error) {
      logger.warn('Image extraction/OCR error', { filePath, error: (error as Error).message });
    }

    return ocrTextBlocks;
  }

  /**
   * 将原始内容与 OCR 结果合并
   */
  private combineContentWithOCR(text: string, ocrBlocks: OCRTextBlock[]): string {
    if (ocrBlocks.length === 0) {
      return text;
    }

    const ocrContent = ocrBlocks
      .map(block => `[${block.source}] ${block.text}`)
      .join('\n\n');

    return `${text}\n\n--- 图片 OCR 提取内容 ---\n${ocrContent}`;
  }

  /**
   * 从 OfficeParser 结构化输出中提取纯文本（带 OCR 跟踪）
   */
  private extractTextWithOCR(data: any, ocrEnabled: boolean): { text: string; ocrTextBlocks: OCRTextBlock[] } {
    const ocrTextBlocks: OCRTextBlock[] = [];

    const text = this.extractTextRecursive(data, ocrEnabled, ocrTextBlocks, '');

    return { text, ocrTextBlocks };
  }

  /**
   * 递归提取文本（内部方法）
   */
  private extractTextRecursive(
    data: any,
    ocrEnabled: boolean,
    ocrTextBlocks: OCRTextBlock[],
    position: string
  ): string {
    // 如果是字符串直接返回
    if (typeof data === 'string') {
      return data;
    }

    // 处理对象类型 (docx, pptx, pdf 等返回的结构化数据)
    if (data && typeof data === 'object') {
      const textParts: string[] = [];

      // 检测 OCR 图片块（OfficeParser 返回的图片 OCR 结果）
      if (ocrEnabled && data.imageType === 'image' && data.text) {
        const imageType = this.detectImageType(data.text, data.altText);
        ocrTextBlocks.push({
          text: data.text,
          imageType,
          source: `ocr-${this.getImageTypeLabel(imageType)}`,
          position: position || data.pageNumber?.toString(),
        });
      }

      // 处理 PDF: data.content 是页面数组
      if (Array.isArray(data.content)) {
        for (let i = 0; i < data.content.length; i++) {
          const page = data.content[i];
          const pagePos = `page-${i + 1}`;
          const pageText = this.extractTextRecursive(page, ocrEnabled, ocrTextBlocks, pagePos);
          if (pageText) textParts.push(pageText);
        }
      }

      // 处理 PPTX: data.content 是幻灯片数组
      if (data.content && typeof data.content === 'object' && Array.isArray(data.content.children)) {
        for (let i = 0; i < data.content.children.length; i++) {
          const slide = data.content.children[i];
          const slidePos = `slide-${i + 1}`;
          const slideText = this.extractTextRecursive(slide, ocrEnabled, ocrTextBlocks, slidePos);
          if (slideText) textParts.push(slideText);
        }
      }

      // 处理 DOCX: data.content 是段落数组
      if (data.content && Array.isArray(data.content)) {
        for (let i = 0; i < data.content.length; i++) {
          const block = data.content[i];
          const blockPos = position ? `${position}/p-${i + 1}` : `p-${i + 1}`;
          const blockText = this.extractTextRecursive(block, ocrEnabled, ocrTextBlocks, blockPos);
          if (blockText) textParts.push(blockText);
        }
      }

      // 处理带 text 属性的节点
      if (data.text) {
        textParts.push(data.text);
      }

      // 处理 children (嵌套结构)
      if (data.children && Array.isArray(data.children)) {
        for (let i = 0; i < data.children.length; i++) {
          const child = data.children[i];
          const childPos = position ? `${position}/c-${i + 1}` : `c-${i + 1}`;
          const childText = this.extractTextRecursive(child, ocrEnabled, ocrTextBlocks, childPos);
          if (childText) textParts.push(childText);
        }
      }

      return textParts.join('\n');
    }

    // 清理文本：移除多余空格，修复连字问题
    let text = String(data ?? '');
    text = text.replace(/\s+/g, ' ');           // 多空格变单空格
    text = text.replace(/([a-zA-Z])\s+(?=[a-zA-Z])/g, '$1'); // 连字字母合并
    text = text.replace(/(\d)\s+(?=\d)/g, '$1'); // 连字数字合并

    return text.trim();
  }

  /**
   * 根据文本内容检测图片类型
   * @param text OCR 提取的文字
   * @param altText 图片的替代文本（如果有）
   */
  private detectImageType(text: string, altText?: string): 'architecture' | 'screenshot' | 'table' | 'unknown' {
    const lowerText = (text + ' ' + (altText || '')).toLowerCase();

    // 架构图特征：包含技术组件、流程、箭头等关键词
    const architectureKeywords = [
      '服务器', '客户端', '数据库', '接口', 'api', 'http', 'https',
      '模块', '组件', '服务', '系统', '平台', '架构', '部署',
      'nginx', 'redis', 'mysql', 'kafka', 'docker', 'kubernetes',
      '网关', '认证', '授权', '微服务', '分布式', '集群',
      '负载均衡', '缓存', '队列', '消息', '存储', '计算',
      '箭头', '流程', '->', '-->', '→',
    ];
    const architectureCount = architectureKeywords.filter(kw => lowerText.includes(kw)).length;
    if (architectureCount >= 2) {
      return 'architecture';
    }

    // 界面截图特征：包含 UI 元素关键词
    const screenshotKeywords = [
      '按钮', '菜单', '输入框', '下拉', '复选框', '单选框',
      '用户名', '密码', '登录', '提交', '取消', '确定',
      '首页', '设置', '管理', '用户', '列表', '详情',
      '查询', '搜索', '添加', '编辑', '删除', '导出',
      'toolbar', 'sidebar', 'header', 'footer', 'nav',
      'click', 'button', 'menu', 'input', 'form',
    ];
    const screenshotCount = screenshotKeywords.filter(kw => lowerText.includes(kw)).length;
    if (screenshotCount >= 2) {
      return 'screenshot';
    }

    // 表格图片特征：包含行列结构特征
    const tableKeywords = [
      '序号', '编号', '项目', '名称', '类型', '状态', '时间', '日期',
      '金额', '数量', '备注', '说明', '分类', '部门', '人员',
      '|', '：', ':', '—', '–',
    ];
    const tablePatterns = [
      /^\s*\d+[\s\t]+.+/,  // 数字开头
      /^.+\s+\d+\s*$/,     // 数字结尾
    ];
    const tableCount = tableKeywords.filter(kw => lowerText.includes(kw)).length;
    const hasTablePattern = tablePatterns.some(p => p.test(text));
    if (tableCount >= 3 || (tableCount >= 1 && hasTablePattern)) {
      return 'table';
    }

    return 'unknown';
  }

  /**
   * 获取图片类型的中文标签
   */
  private getImageTypeLabel(imageType: 'architecture' | 'screenshot' | 'table' | 'unknown'): string {
    const labels = {
      architecture: '架构图',
      screenshot: '界面截图',
      table: '表格',
      unknown: '图片',
    };
    return labels[imageType];
  }
}

/**
 * 文档解析器工厂
 */
export class DocumentParserFactory {
  private static parsers: IDocumentParser[] = [
    new OfficeParserAdapter(),  // 优先使用（效果最好）
    new PdfParser(),
    new DocxParser(),
    new ExcelParser(),
    new TextParser(),
  ];

  /**
   * 注册新的解析器
   * @param parser 解析器实例
   */
  static registerParser(parser: IDocumentParser): void {
    this.parsers.unshift(parser); // 新注册的解析器优先级更高
  }

  /**
   * 获取适合文件的解析器
   * @param filePath 文件路径
   */
  static getParserForFile(filePath: string): IDocumentParser | null {
    const ext = path.extname(filePath).toLowerCase();
    for (const parser of this.parsers) {
      if (parser.supportedExtensions.includes(ext)) {
        return parser;
      }
    }
    return null;
  }

  /**
   * 获取所有支持的文件扩展名
   */
  static getSupportedExtensions(): string[] {
    const extensions = new Set<string>();
    for (const parser of this.parsers) {
      for (const ext of parser.supportedExtensions) {
        extensions.add(ext);
      }
    }
    return Array.from(extensions);
  }

  /**
   * 解析文档，自动选择合适的解析器
   * @param filePath 文件路径
   * @param options 解析选项
   */
  static async parse(filePath: string, options?: Record<string, any>): Promise<DocumentParseResult> {
    // 检查文件是否存在
    try {
      await fs.access(filePath);
    } catch (error) {
      throw new Error(`File not found: ${filePath}`);
    }

    const parser = this.getParserForFile(filePath);
    if (!parser) {
      throw new Error(`No parser available for file: ${filePath}`);
    }

    logger.debug(`Parsing file ${filePath} with ${parser.constructor.name}`);
    return parser.parse(filePath, options);
  }
}

// 默认导出
export default DocumentParserFactory;
