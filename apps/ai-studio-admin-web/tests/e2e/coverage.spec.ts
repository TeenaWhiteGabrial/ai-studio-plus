import { test } from '@playwright/test'
import { expectAdminRoute, loginAdmin } from './helpers'

const routes: Array<{ path: string; title: RegExp }> = [
  { path: '/admin/dashboard/output', title: /产出数据看板|数据看板/ },
  { path: '/admin/dashboard/portal', title: /网站数据看板/ },
  { path: '/admin/resource/skill', title: /Skill管理/ },
  { path: '/admin/resource/mcp', title: /MCP管理/ },
  { path: '/admin/resource/plugin', title: /Plugin管理/ },
  { path: '/admin/resource/tutorial', title: /教程管理/ },
  { path: '/admin/output/admin', title: /全员产出/ },
  { path: '/admin/system/user', title: /用户管理/ },
  { path: '/admin/system/department', title: /部门管理/ },
  { path: '/admin/system/role', title: /角色管理/ },
  { path: '/admin/system/menu', title: /菜单管理/ },
  { path: '/admin/system/team', title: /团队管理/ },
  { path: '/admin/system/mail-config', title: /邮件配置/ },
  { path: '/admin/audit/resource', title: /资源审核/ },
  { path: '/admin/knowledge', title: /知识库管理/ },
  { path: '/admin/site-settings', title: /网站设置/ },
  { path: '/admin/message/announcement', title: /公告管理/ },
  { path: '/admin/message/notification', title: /通知记录/ },
  { path: '/admin/message/rule', title: /通知规则/ },
  { path: '/admin/message/email-report', title: /邮件日报/ },
  { path: '/admin/community/article', title: /文章管理/ },
  { path: '/admin/community/comment', title: /评论管理/ }
]

test.describe('Admin Route Coverage', () => {
  test.beforeEach(async ({ page }) => {
    await loginAdmin(page)
  })

  for (const route of routes) {
    test(`${route.path} is reachable`, async ({ page }) => {
      await expectAdminRoute(page, route.path, route.title)
    })
  }
})
