<template>
  <footer class="portal-footer">
    <div class="csdn-container footer-inner">
      <div class="footer-brand">
        <strong>AI Studio</strong>
        <p>面向研发团队的 AI 技术社区与资源平台。</p>
      </div>

      <div class="footer-links">
        <NuxtLink to="/">首页</NuxtLink>
        <NuxtLink to="/community">社区</NuxtLink>
        <NuxtLink to="/resources">资源中心</NuxtLink>
        <NuxtLink to="/profile">个人中心</NuxtLink>
      </div>

      <div class="footer-copy">
        <span>Copyright © {{ year }} AI Studio</span>
        <span>All rights reserved.</span>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.portal-footer {
  margin-top: 28px;
  border-top: 1px solid var(--csdn-line);
  background: #fff;
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
  color: var(--csdn-text);
}

.footer-brand p {
  margin: 6px 0 0;
  color: var(--csdn-muted);
  font-size: 13px;
}

.footer-links {
  display: flex;
  gap: 14px;
  justify-content: center;
  font-size: 14px;
  color: var(--csdn-subtext);
}

.footer-links a:hover {
  color: var(--csdn-primary);
}

.footer-copy {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  font-size: 12px;
  color: var(--csdn-muted);
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
