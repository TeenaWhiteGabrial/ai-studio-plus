<template>
  <footer class="portal-footer" :class="{ transparent }">
    <div class="csdn-container footer-inner">
      <div class="footer-brand">
        <strong>{{ siteConfig.name }}</strong>
        <p>{{ siteConfig.footerText || siteConfig.description }}</p>
      </div>

      <div class="footer-links">
        <NuxtLink v-for="link in footerLinks" :key="link.url" :to="link.url">{{ link.name }}</NuxtLink>
      </div>

      <div class="footer-copy">
        <span>{{ siteConfig.footerCopyright || `Copyright © ${year} ${siteConfig.name}` }}</span>
        <span v-if="siteConfig.footerRecord">{{ siteConfig.footerRecord }}</span>
        <span v-else>All rights reserved.</span>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
defineProps<{
  transparent?: boolean
}>()

const year = new Date().getFullYear()
const { siteConfig } = useSite()
const footerLinks = computed(() => siteConfig.footerLinks?.length ? siteConfig.footerLinks : [
  { name: '首页', url: '/' },
  { name: '社区', url: '/community' },
  { name: '资源中心', url: '/resources' },
  { name: '个人中心', url: '/profile' },
])
</script>

<style scoped>
.portal-footer {
  margin-top: 28px;
  border-top: 1px solid var(--portal-line);
  background: var(--portal-footer-bg);
}

.portal-footer.transparent {
  position: relative;
  z-index: 2;
  margin-top: 0;
  border-top-color: rgba(125, 211, 252, 0.18);
  background: transparent;
}

.portal-footer.transparent .footer-brand strong {
  color: #f8fafc;
}

.portal-footer.transparent .footer-brand p,
.portal-footer.transparent .footer-links,
.portal-footer.transparent .footer-copy {
  color: rgba(226, 232, 240, 0.72);
}

.portal-footer.transparent .footer-links a:hover {
  color: #7dd3fc;
}

.footer-inner {
  min-height: 96px;
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: 20px;
  align-items: center;
  padding: 18px 0;
}

.footer-brand strong {
  font-size: 18px;
  color: var(--portal-text);
}

.footer-brand p {
  margin: 6px 0 0;
  color: var(--portal-muted);
  font-size: 13px;
}

.footer-links {
  display: flex;
  gap: 14px;
  justify-content: center;
  font-size: 14px;
  color: var(--portal-subtext);
}

.footer-links a:hover {
  color: var(--color-primary);
}

.footer-copy {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  font-size: 12px;
  color: var(--portal-muted);
}

@media (max-width: 900px) {
  .footer-inner {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .footer-links {
    justify-content: center;
  }

  .footer-copy {
    align-items: center;
  }
}
</style>
