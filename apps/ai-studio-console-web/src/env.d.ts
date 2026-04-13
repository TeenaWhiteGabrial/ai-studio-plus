/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_BASE_ROUTER: string
  readonly VITE_APP_TYPE: 'admin' | 'console' | 'portal'
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
