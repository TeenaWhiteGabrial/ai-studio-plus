// Default key pair for legacy helper calls.
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

type JSEncryptConstructor = new () => {
  setPublicKey: (key: string) => void
  setPrivateKey: (key: string) => void
  encrypt: (message: string) => string | false
  decrypt: (message: string) => string | false
}

const toPemKey = (key: string, type: 'PUBLIC KEY' | 'RSA PRIVATE KEY') => {
  const trimmedKey = key.trim()
  if (trimmedKey.includes(`BEGIN ${type}`)) {
    return trimmedKey
  }

  const normalizedBody = trimmedKey
    .replace(/\s+/g, '')
    .match(/.{1,64}/g)
    ?.join('\n') || trimmedKey

  return `-----BEGIN ${type}-----\n${normalizedBody}\n-----END ${type}-----`
}

export const normalizePublicKey = (key: string) => toPemKey(key, 'PUBLIC KEY')

const normalizePrivateKey = (key: string) => toPemKey(key, 'RSA PRIVATE KEY')

const resolveJSEncryptConstructor = (module: unknown): JSEncryptConstructor => {
  const moduleRecord = module as Record<string, any>
  const defaultExport = moduleRecord?.default
  const candidates = [
    defaultExport,
    moduleRecord?.JSEncrypt,
    defaultExport?.JSEncrypt,
    defaultExport?.default,
    defaultExport?.default?.JSEncrypt,
    moduleRecord,
  ]

  const constructor = candidates.find(candidate => typeof candidate === 'function')
  if (!constructor) {
    throw new Error('JSEncrypt constructor not found')
  }
  return constructor as JSEncryptConstructor
}

export const createJSEncrypt = async () => {
  const module = await import('jsencrypt')
  const JSEncrypt = resolveJSEncryptConstructor(module)
  return new JSEncrypt()
}

export const RSAEncrypt = async (publicKey: string, message: string): Promise<{ success: boolean; encryptedPassword?: string }> => {
  if (!import.meta.client) {
    console.warn('JSEncrypt can only run on the client')
    return { success: false }
  }

  try {
    const jsencrypt = await createJSEncrypt()
    jsencrypt.setPublicKey(normalizePublicKey(publicKey))
    const encryptedPassword = jsencrypt.encrypt(message)
    if (encryptedPassword) {
      return { success: true, encryptedPassword }
    }
    return { success: false }
  } catch (error) {
    console.error('Encrypt failed:', error)
    return { success: false }
  }
}

export const RSADecrypt = async (message: string): Promise<string> => {
  if (!import.meta.client) {
    console.warn('JSEncrypt can only run on the client')
    return message
  }

  try {
    const jsencrypt = await createJSEncrypt()
    jsencrypt.setPrivateKey(normalizePrivateKey(privateKey))
    return jsencrypt.decrypt(message) || message
  } catch (error) {
    console.error('Decrypt failed:', error)
    return message
  }
}

export async function encrypt(txt: string) {
  return await RSAEncrypt(publicKey, txt)
}

export async function decrypt(txt: string) {
  return await RSADecrypt(txt)
}
