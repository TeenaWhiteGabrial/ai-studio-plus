<template>
  <el-dropdown trigger="click" popper-class="portal-theme-dropdown" @command="handleThemeCommand">
    <button class="theme-trigger" type="button" :title="activeTheme.name">
      <Icon name="material-symbols:palette-outline" size="19" />
      <span>{{ activeTheme.name }}</span>
      <Icon name="material-symbols:keyboard-arrow-down" size="18" />
    </button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="theme in portalThemePresets"
          :key="theme.id"
          :command="theme.id"
          :class="{ active: currentPortalTheme === theme.id }"
        >
          <div class="theme-option">
            <span class="theme-dot" :style="{ background: theme.primaryColor }"></span>
            <span>
              <strong>{{ theme.name }}</strong>
              <small>{{ theme.description }}</small>
            </span>
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
const { currentPortalTheme, portalThemePresets, setPortalTheme, getPortalTheme } = useTheme()
const activeTheme = computed(() => getPortalTheme(currentPortalTheme.value))

function handleThemeCommand(command: string) {
  setPortalTheme(command)
}
</script>

<style scoped>
.theme-trigger {
  height: 40px;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  color: var(--portal-subtext);
  background: var(--portal-surface-glass);
  cursor: pointer;
  font-weight: 800;
  backdrop-filter: var(--portal-backdrop-filter);
}

.theme-trigger:hover {
  color: var(--portal-text);
  border-color: color-mix(in srgb, var(--color-primary) 48%, var(--portal-line));
}

.theme-option {
  min-width: 190px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.theme-option strong,
.theme-option small {
  display: block;
}

.theme-option small {
  margin-top: 2px;
  color: var(--portal-muted);
  font-size: 12px;
}

.theme-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 0 4px rgba(125, 211, 252, 0.1);
}

:global(.portal-theme-dropdown) {
  border: 1px solid var(--portal-line) !important;
  background: var(--portal-surface-strong) !important;
  backdrop-filter: var(--portal-backdrop-filter);
}

:global(.portal-theme-dropdown .el-dropdown-menu) {
  background: transparent;
}

:global(.portal-theme-dropdown .el-dropdown-menu__item) {
  color: var(--portal-subtext);
}

:global(.portal-theme-dropdown .el-dropdown-menu__item.active),
:global(.portal-theme-dropdown .el-dropdown-menu__item:hover) {
  color: var(--portal-text);
  background: var(--portal-surface-soft);
}

@media (max-width: 760px) {
  .theme-trigger span {
    display: none;
  }

  .theme-trigger {
    width: 40px;
    justify-content: center;
    padding: 0;
  }
}
</style>
