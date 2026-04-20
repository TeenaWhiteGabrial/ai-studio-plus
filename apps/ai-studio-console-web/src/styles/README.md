# AI Studio Console 样式系统

基于 [model_plaza](https://github.com/LanceRepo/model_plaza) 项目的设计风格构建的完整主题系统。

## 设计理念

- **深浅主题切换**：支持深色和浅色两种主题，默认深色
- **浏览器缓存**：主题设置自动保存到 localStorage
- **HSL 颜色系统**：使用 HSL 色彩空间，便于调整色相、饱和度和亮度
- **8px 间距系统**：基础间距单位为 8px (0.5rem)
- **毛玻璃效果**：支持背景模糊的玻璃态设计
- **柔和发光**：微妙的发光效果，增强视觉层次

## 文件结构

```
src/
├── styles/
│   ├── theme.css          # 主题变量定义（CSS 变量）
│   ├── global.css         # 全局样式和 Element Plus 覆盖
│   └── README.md          # 本文档
├── stores/
│   └── theme.ts           # 主题状态管理（Pinia Store）
└── components/
    └── ThemeToggle.vue   # 主题切换组件
```

## 主题系统

### 主题配置

主题变量定义在 `theme.css` 中，包含完整的颜色系统：

```css
:root {
  /* 浅色主题（默认） */
  --background: 0 0% 98%;
  --foreground: 240 10% 10%;
  --card: 0 0% 100%;
  --primary: 220 70% 55%;
  --secondary: 220 10% 94%;
  --muted: 220 10% 95%;
  --border: 220 10% 88%;
  --ring: 220 70% 55%;
  --chart-1: 220 70% 55%;
  --chart-2: 160 50% 45%;
  --chart-3: 30 70% 50%;
  --chart-4: 280 55% 55%;
  --chart-5: 340 65% 50%;
}

/* 深色主题 */
.dark {
  --background: 228 12% 8%;
  --foreground: 220 10% 92%;
  --card: 228 12% 11%;
  --primary: 220 70% 55%;
  --secondary: 228 10% 16%;
  --muted: 228 10% 14%;
  --border: 228 8% 20%;
  --ring: 220 70% 55%;
  --chart-1: 220 70% 55%;
  --chart-2: 160 50% 45%;
  --chart-3: 30 70% 50%;
  --chart-4: 280 55% 55%;
  --chart-5: 340 65% 50%;
}
```

### 主题状态管理

使用 Pinia Store 管理主题状态：

```typescript
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

// 获取当前主题
console.log(themeStore.theme) // 'light' | 'dark'

// 切换主题
themeStore.toggleTheme()

// 设置特定主题
themeStore.setTheme('light')

// 初始化主题（在应用启动时调用）
themeStore.initTheme()
```

### 主题切换组件

在布局组件中使用主题切换按钮：

```vue
<template>
  <div class="header-right">
    <ThemeToggle />
    <!-- 其他内容 -->
  </div>
</template>

<script setup lang="ts">
import ThemeToggle from '@/components/ThemeToggle.vue'
</script>
```

## Element Plus 主题化

样式系统已对 Element Plus 组件进行了完整主题化处理，包括：

- **Card**：主题化背景和边框
- **Table**：主题化表格背景、边框和文字颜色
- **Dialog/Drawer**：主题化模态框
- **Menu**：主题化菜单和激活状态
- **Input/Select**：主题化输入框和焦点状态
- **Button**：主题化按钮（primary 颜色）
- **Tag**：语义化标签颜色
- **Scrollbar**：主题化滚动条
- **Message/Form/Loading/Tooltip**：完整的主题化支持

## 使用方法

### 1. 在组件中使用主题变量

```vue
<template>
  <div class="my-component">
    <h1 class="title">标题</h1>
    <p class="description">描述内容</p>
  </div>
</template>

<style scoped>
.my-component {
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: var(--radius);
  padding: var(--spacing-unit);
}

.title {
  font-size: var(--font-xl);
  color: hsl(var(--foreground));
  font-weight: 600;
}

.description {
  font-size: var(--font-sm);
  color: hsl(var(--muted-foreground));
  margin-top: var(--spacing-unit);
}
</style>
```

### 2. 使用预定义的实用类

```vue
<template>
  <div class="my-component">
    <div class="glass glass-border">
      <div class="gradient-text">主要文字</div>
      <div class="surface-elevated">悬浮卡片</div>
    </div>
  </div>
</template>

<style scoped>
/* 无需额外样式，直接使用预定义类 */
</style>
```

### 3. 使用主题切换功能

```typescript
// 在 main.ts 中初始化主题
import { useThemeStore }() from './stores/theme'

const app = createApp(App)
// ... 其他配置

const themeStore = useThemeStore()
themeStore.initTheme()

app.mount('#app')
```

## 颜色系统

### 品牌色（蓝色系）

| 变量名 | 说明 | 用途 |
|--------|------|------|
| `--primary` | 主色 | 按钮、链接、激活状态 |
| `--primary-foreground` | 主色文字 | 主色文字颜色 |
| `--secondary` | 次要色 | 次要背景、悬停状态 |
| `--secondary-` | 次要色文字 | 次要文字颜色 |

### 语义色

| 变量名 | 说明 | 用途 |
|--------|------|------|
| `--success` | 绿色 | 成功、完成状态 |
| `--warning` | 橙色 | 警告、待处理状态 |
| `--destructive` | 红色 | 错误、危险操作 |
| `--muted` | 灰色 | 禁用、占位状态 |

### 中性色

#### 背景色

| 变量名 | 浅色 | 深色 |
|--------|------|------|
| `--background` | `0 0% 98%` | `228 12% 8%` |
| `--card` | `0 0% 100%` | `228 12% 11%` |
| `--popover` | `0 0% 100%` | `228 12% 11%` |

#### 文字色

| 变量名 | 浅色 | 深色 |
|--------|------|------|
| `--foreground` | `240 10% 10%` | `220 10% 92%` |
| `--muted-foreground` | `220 8% 38%` | `220 8% 56%` |

#### 边框色

| 变量名 | 浅色 | 深色 |
|--------|------|------|
| `--border` | `220 10% 88%` | `228 8% 20%` |

## 特效

### 发光效果

```css
.my-element {
  box-shadow: var(--ai-glow-sm);    /* 小发光 */
  box-shadow: var(--ai-glow-md);    /* 中发光 */
  box-shadow: var(--ai-glow-lg);    /* 大发光 */
  box-shadow: var(--ai-glow-ring);  /* 发光环 */
}
```

### 毛玻璃效果

```vue
<template>
  <div class="glass glass-border">
    <!-- 内容 -->
  </div>
</template>
```

### 阴影效果

```css
.my-element {
  box-shadow: var(--ai-shadow-sm);
  box-shadow: var(--ai-shadow-md);
  box-shadow: var(--ai-shadow-lg);
}
```

### 渐变效果

```css
.gradient-text {
  background: linear-gradient(135deg, hsl(var(--foreground)), hsl(var(--muted-foreground)));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.gradient-accent {
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 80% 65%));
}
```

## 间距系统

基于 8px 的间距单位：

| 类名 | 值 | 说明 |
|------|-----|------|
| `.space-8` | 0.5rem | 8px |
| `.space-16` | 1rem | 16px |
| `.space-24` | 1.5rem | 24px |
| `.space-32` | 2rem | 32px |
| `.space-40` | 2.5rem | 40px |
| `.space-48` | 3rem | 48px |

## 圆角

| 类名 | 值 | 说明 |
|------|-----|------|
| `.radius-sm` | 0.375rem | 6px |
| `.radius-md` | 0.625rem | 10px |
| `.radius-lg` | 0.75rem | 12px |
| `.radius-xl` | 1rem | 16px |

## 动画

预定义的动画效果：

```css
.animate-fade-in-up {
  animation: fade-in-up 0.4s ease-out forwards;
}

.animate-fade-in {
  animation: fade-in 0.3s ease-out forwards;
}

.animate-subtle-pulse {
  animation: subtle-pulse 3s ease-in-out infinite;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-shimmer {
  background-size: 200% 100%;
  animation: shimmer 2s linear infinite;
}
```

## 最佳实践

1. **优先使用主题变量**：避免硬编码颜色值
2. **使用 CSS 变量引用**：通过 `hsl(var(--variable-name))` 引用
3. **保持间距一致**：使用预定义的间距变量
4. **合理使用特效**：发光和毛玻璃效果适度使用
5. **遵循对比度**：确保文字与背景有足够的对比度
6. **组件化复用**：使用预定义的实用类减少重复代码
7. **主题切换友好**：确保组件在深浅主题下都有良好表现

## 参考项目

- [model_plaza](https://github.com/LanceRepo/model_plaza) - 原始设计参考
- Tailwind CSS - 设计理念参考
- shadcn/ui - 组件设计参考
