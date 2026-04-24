// 密钥对生成 http://web.chacuo.net/netrsakeypair
const publicKey = 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdH\n' +
    'nzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ=='

const privateKey = 'MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY\n' +
    '7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKN\n' +
    'PuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gA\n' +
    'kM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWow\n' +
    'cSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99Ecv\n' +
    'DQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthh\n' +
    'YhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3\n' +
    'UP8iWi1Qw0Y='

/**
 * RSA加密函数 - 使用指定公钥
 * @param publicKey 加密使用的公钥
 * @param message 需要加密的消息
 * @returns 加密结果
 */
export const RSAEncrypt = async (publicKey: string, message: string): Promise<{ success: boolean; encryptedPassword?: string }> => {
  // 确保在客户端环境下运行
  if (!import.meta.client) {
    console.warn('JSEncrypt只能在客户端环境使用');
    return { success: false };
  }

  try {
    // 动态导入JSEncrypt
    // @ts-ignore
    const JSEncrypt = (await import('jsencrypt/bin/jsencrypt.min')).default;
    const jsencrypt = new JSEncrypt();
    // 设置公钥
    jsencrypt.setPublicKey(publicKey);
    // 执行加密
    const encryptedPassword = jsencrypt.encrypt(message);
    if (encryptedPassword) {
      return { success: true, encryptedPassword };
    }
    return { success: false };
  } catch (error) {
    console.error('加密失败:', error);
    return { success: false };
  }
};

/**
 * RSA解密函数 - 异步版本
 * @param message 需要解密的消息
 * @returns 解密后的字符串
 */
export const RSADecrypt = async (message: string): Promise<string> => {
  // 确保在客户端环境下运行
  if (!import.meta.client) {
    console.warn('JSEncrypt只能在客户端环境使用');
    return message; // 服务端返回原始文本
  }

  try {
    // 动态导入JSEncrypt
    // @ts-ignore
    const JSEncrypt = (await import('jsencrypt/bin/jsencrypt.min')).default;
    const jsencrypt = new JSEncrypt();
    // 设置私钥
    jsencrypt.setPrivateKey(privateKey);
    // 执行解密，失败时返回原消息
    return jsencrypt.decrypt(message) || message;
  } catch (error) {
    console.error('解密失败:', error);
    return message;
  }
};

// 保留原有的同步函数，但改为异步实现的包装器，保持向后兼容
export async function encrypt(txt: string) {
  return await RSAEncrypt(publicKey, txt);
}

export async function decrypt(txt: string) {
  return await RSADecrypt(txt);
}

