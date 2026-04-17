import 'nuxt/app'

declare module 'nuxt/app' {
    interface PublicRuntimeConfig {
        theme: {
            defaultPrimaryColor: string
            enablePersistence: boolean
        }
    }
}

export {}
