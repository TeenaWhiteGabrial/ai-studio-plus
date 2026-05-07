import axios from 'axios'

export interface SiteConfig {
  siteName: string
  logoUrl: string
}

function mapSiteConfig(item: any): SiteConfig {
  return {
    siteName: item.site_name ?? item.siteName ?? 'AI Studio',
    logoUrl: item.logo_url ?? item.logoUrl ?? '/ai-studio-logo.svg',
  }
}

export async function getSiteConfig() {
  const response = await axios.get('/ai-studio/v1/portal/site/config')
  const payload = response.data
  if (payload.code !== 200) {
    throw new Error(payload.message || '获取网站设置失败')
  }
  return mapSiteConfig(payload.data)
}
