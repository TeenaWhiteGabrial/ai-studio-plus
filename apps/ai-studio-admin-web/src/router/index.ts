// 导入本地认证路由
import router from '@/utils/auth'
import Layout from '@/layout/index.vue'
import Login from '@/views/login/index.vue'

// 登录路由（必须放在最前面，在 layoutRoute 之前）
const loginRoute = {
  path: '/admin/login',
  name: 'Login',
  component: Login,
  meta: { title: '登录' },
}

// Admin 管理后台路由（统一挂在 /admin/ 前缀下）
const adminRoutes = [
  {
    path: '/admin/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/index.vue'),
    meta: { title: '数据看板' },
  },
  {
    path: '/admin/resource/skill',
    name: 'Skill',
    component: () => import('@/views/skill/index.vue'),
    meta: { title: 'Skill管理' },
  },
  {
    path: '/admin/resource/mcp',
    name: 'Mcp',
    component: () => import('@/views/mcp/index.vue'),
    meta: { title: 'MCP管理' },
  },
  {
    path: '/admin/resource/plugin',
    name: 'Plugin',
    component: () => import('@/views/plugin/index.vue'),
    meta: { title: 'Plugin管理' },
  },
  {
    path: '/admin/resource/tutorial',
    name: 'Tutorial',
    component: () => import('@/views/tutorial/index.vue'),
    meta: { title: '教程管理' },
  },
  {
    path: '/admin/tutorial/:id/read',
    name: 'TutorialRead',
    component: () => import('@/views/tutorial/read.vue'),
    meta: { title: '阅读教程' },
  },
  {
    path: '/admin/output/my',
    name: 'MyOutput',
    component: () => import('@/views/output/my.vue'),
    meta: { title: '我的产出' },
  },
  {
    path: '/admin/output/history',
    name: 'OutputHistory',
    component: () => import('@/views/output/history.vue'),
    meta: { title: '产出历史' },
  },
  {
    path: '/admin/output/admin',
    name: 'AdminOutput',
    component: () => import('@/views/output/admin.vue'),
    meta: { title: '全员产出' },
  },
  {
    path: '/admin/stats/department',
    name: 'DepartmentStats',
    component: () => import('@/views/stats/DepartmentStats.vue'),
    meta: { title: '部门统计' },
  },
  {
    path: '/admin/stats/project',
    name: 'ProjectStats',
    component: () => import('@/views/stats/ProjectStats.vue'),
    meta: { title: '项目统计' },
  },
  {
    path: '/admin/system/user',
    name: 'UserManage',
    component: () => import('@/views/user/index.vue'),
    meta: { title: '用户管理' },
  },
  {
    path: '/admin/system/department',
    name: 'DepartmentManage',
    component: () => import('@/views/department/index.vue'),
    meta: { title: '部门管理' },
  },
  {
    path: '/admin/system/role',
    name: 'RoleManage',
    component: () => import('@/views/role/index.vue'),
    meta: { title: '角色管理' },
  },
  {
    path: '/admin/system/team',
    name: 'TeamManage',
    component: () => import('@/views/team/index.vue'),
    meta: { title: '团队管理' },
  },
  {
    path: '/admin/audit/resource',
    name: 'ResourceAudit',
    component: () => import('@/views/audit/resource.vue'),
    meta: { title: '资源审核' },
  },
]

// 404 重定向
const notFoundRoute = {
  path: '/:pathMatch(.*)*',
  redirect: '/admin/dashboard',
}

// 添加登录路由
router.addRoute(loginRoute)

// 使用 Layout 的路由（包含子路由）
const layoutRoute = {
  path: '/admin',
  component: Layout,
  redirect: '/admin/dashboard',
  children: adminRoutes,
}

// 添加 Layout 路由
router.addRoute(layoutRoute)

// 添加 404 路由
router.addRoute(notFoundRoute)

export default router
