# 支持动态主题配置系统

项目支持动态主题配置系统，只需要一个主题色，系统会自动衍生出所有相关的辅助颜色。

## 🎨 系统特性

- **单一主题色**: 只需配置一个主题色（HEX 格式），系统自动衍生其他颜色
- **智能色彩衍生**: 自动生成悬浮色、激活色、浅色版本、深色版本等
- **动态主题加载**: 从 API 接口动态获取主题配置
- **CSS 变量支持**: 使用 CSS 变量实现实时主题切换
- **UnoCSS 集成**: 与 UnoCSS 原子化 CSS 框架完美集成
- **本地缓存**: 支持本地存储缓存，离线时使用缓存配置
- **TypeScript 支持**: 完整的类型定义，确保类型安全

## 📁 文件结构

```
├── app/types/
│   └── theme.ts                    # 简化的主题配置类型定义
├── app/composables/
│   └── useTheme.ts                 # 主题管理 Composable（含颜色衍生算法）
├── server/api/theme/
│   ├── config.get.ts               # 获取主题配置 API
│   └── config.post.ts              # 更新主题配置 API
├── app/plugins/
│   └── theme.client.ts             # 客户端主题初始化插件
├── app/components/Header/
│   └── ApolloNavbar.vue            # 使用主题色的导航栏组件
└── unocss.config.ts                # UnoCSS 配置（简化的主题色支持）
```

## 🚀 使用方法

### 1. 在组件中使用主题

```vue
<template>
  <div>
    <!-- 使用预定义的主题色类名 -->
    <button class="btn-primary">主要按钮</button>
    <button class="btn-primary-light">浅色按钮</button>
    <button class="btn-primary-outline">轮廓按钮</button>
    
    <!-- 使用主题色变量 -->
    <div class="text-primary border-primary">主题色文本和边框</div>
    <div class="bg-primary-light">浅色背景</div>
  </div>
</template>

<script setup lang="ts">
// 获取主题管理功能
const { themeConfig, derivedColors, updateTheme, fetchThemeConfig } = useTheme()

// 获取当前主题色
const currentColor = themeConfig.value.primaryColor

// 更新主题色
updateTheme({ primaryColor: '#10b981' }) // 切换为绿色

// 从 API 刷新主题配置
await fetchThemeConfig()
</script>
```

### 2. 可用的主题色类名

#### 按钮组件
- `btn-primary` - 主要按钮（主题色背景 + 悬浮效果）
- `btn-primary-light` - 浅色按钮（浅色背景 + 悬浮为主题色）
- `btn-primary-outline` - 轮廓按钮（主题色边框 + 悬浮填充）

#### 颜色类名
- `text-primary` - 主题色文本
- `text-primary-dark` - 悬浮色文本
- `bg-primary` - 主题色背景
- `bg-primary-dark` - 悬浮色背景
- `bg-primary-light` - 浅色背景
- `bg-primary-lighter` - 更浅色背景
- `bg-primary-faint` - 最浅色背景
- `border-primary` - 主题色边框
- `border-primary-dark` - 悬浮色边框

### 3. 使用 CSS 变量

```css
.custom-element {
  /* 使用主题色变量 */
  background-color: var(--color-primary);
  border-color: var(--color-primary-dark);
  color: var(--color-primary-text);
}

/* 可用的 CSS 变量 */
:root {
  --color-primary: #3b82f6;           /* 主题色 */
  --color-primary-dark: #2563eb;     /* 悬浮色（稍深） */
  --color-primary-darker: #1d4ed8;    /* 激活色（更深） */
  --color-primary-light: #dbeafe;     /* 浅色版本 */
  --color-primary-lighter: rgb(225, 233, 243);      /* 更浅色版本 */
  --color-primary-faint: rgb(236, 241, 247); /** 最浅色版本 */
  --color-primary-text: #ffffff;      /* 主题色上的文字颜色 */
}
```

## 🎯 主题配置结构

简化的主题配置只包含基本信息和一个主题色：

```typescript
interface ThemeConfig {
  name: string          // 主题名称
  description: string   // 主题描述
  version: string      // 版本号
  primaryColor: string // 主题色（HEX 格式，如 #3b82f6）
}
```

系统会自动衍生以下颜色：

```typescript
interface DerivedColors {
  primary: string          // 主题色
  primaryDark: string     // 悬浮色（稍深）
  primaryDarker: string    // 激活色（更深）
  primaryLight: string     // 浅色版本
  primaryText: string      // 主题色上的文字颜色
}
```

## 🔧 API 接口

### 获取主题配置
```http
GET /api/theme/config

Response:
{
  "success": true,
  "data": {
    "name": "政府标准主题",
    "version": "1.0.0",
    "description": "政府门户网站标准主题色配置",
    "primaryColor": "#3b82f6"
  }
}
```

### 更新主题配置
```http
POST /api/theme/config

Request Body:
{
  "name": "自定义主题",
  "version": "1.1.0",
  "description": "自定义主题配置",
  "primaryColor": "#10b981"
}
```

## 🎨 颜色衍生算法

系统使用 HSL 色彩空间来衍生颜色：

1. **悬浮色**: 亮度降低 10%
2. **激活色**: 亮度降低 15%
3. **浅色版本**: 饱和度降低 20%，亮度增加 30%
4. **深色版本**: 饱和度增加 10%，亮度降低 25%
5. **文字颜色**: 根据主题色亮度自动选择白色或黑色

## 🎪 演示页面

访问 `/theme-demo` 页面可以查看：
- 当前主题配置信息
- 主题色和衍生颜色展示
- 组件样式演示
- 快速切换预设主题色
- 主题操作功能（刷新、重置、导出）

## 🔄 主题切换流程

1. **初始化**: 应用启动时加载默认主题，然后从 API 获取配置
2. **颜色衍生**: 根据主题色自动计算所有衍生颜色
3. **CSS 应用**: 将衍生的颜色设置为 CSS 变量
4. **实时更新**: 页面样式立即更新，无需重新加载
5. **本地缓存**: 主题配置缓存到 localStorage

## 🛠️ 自定义扩展

### 添加新的按钮样式

在 `unocss.config.ts` 的 `shortcuts` 中添加：

```typescript
shortcuts: [
  ['btn-custom', 'bg-primary-dark hover:bg-primary-dark text-primary-text px-6 py-3 rounded-lg'],
  // 更多自定义样式...
]
```

### 修改颜色衍生算法

在 `app/composables/useTheme.ts` 的 `deriveColors` 函数中调整：

```typescript
function deriveColors(primaryColor: string): DerivedColors {
  const [h, s, l] = hexToHsl(primaryColor)
  
  return {
    primary: primaryColor,
    primaryDark: hslToHex(h, s, Math.max(10, l - 15)),  // 调整悬浮色深度
    // ... 其他颜色衍生逻辑
  }
}
```

## 🎨 预设主题色

演示页面提供了 8 种预设主题色：

- **蓝色**: `#3b82f6` （默认）
- **绿色**: `#10b981`
- **紫色**: `#8b5cf6`
- **红色**: `#ef4444`
- **橙色**: `#f59e0b`
- **青色**: `#06b6d4`
- **粉色**: `#ec4899`
- **靛蓝**: `#6366f1`

## 🎯 最佳实践

1. **优先使用预定义类名**: 使用 `btn-primary` 而不是直接使用 `bg-primary`
2. **保持一致性**: 在整个应用中保持颜色使用的一致性
3. **考虑可访问性**: 系统会自动选择合适的文字颜色，确保对比度
4. **测试不同主题色**: 确保所有主题色下组件都能正常显示
5. **合理使用衍生色**: 悬浮色用于交互反馈，浅色用于背景，深色用于强调

## 🔍 故障排除

### 主题没有生效
1. 检查 `app/plugins/theme.client.ts` 是否正确加载
2. 确认 API 接口返回正确的数据格式
3. 查看浏览器控制台是否有错误信息

### 颜色显示不正确
1. 检查 CSS 变量是否正确设置（开发者工具 → Elements → :root）
2. 确认 UnoCSS 配置是否包含相应的颜色定义
3. 验证主题色格式是否为有效的 HEX 格式

### 颜色衍生异常
1. 检查主题色是否为有效的 6 位 HEX 格式
2. 查看控制台是否有颜色转换错误
3. 验证 HSL 转换算法是否正常工作

---

通过这个简化的主题系统，你只需要提供一个主题色，就能获得完整的、协调的色彩方案，非常适合政府门户网站的快速定制需求。
