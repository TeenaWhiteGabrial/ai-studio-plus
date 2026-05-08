// 使用本地认证路由
import router from '@/utils/auth'
import Layout from '@/layout/index.vue'
import Login from '@/views/login/index.vue'

// 登录路由（必须放在最前面，在 layoutRoute 之前）
const loginRoute = {
  path: '/console/login',
  name: 'ConsoleLogin',
  component: Login,
  meta: { title: '登录' },
}

// Console 控制台路由（统一挂在 /console/ 前缀下）
const consoleRoutes = [
  {
    path: '/console/dashboard',
    name: 'ConsoleDashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '工作台' },
  },
  {
    path: '/console/apikey',
    name: 'ApiKey',
    component: () => import('@/views/ApiKey.vue'),
    meta: { title: 'API Key 管理' },
  },
  {
    path: '/console/task',
    name: 'DailyTask',
    component: () => import('@/views/DailyTask.vue'),
    meta: { title: '每日任务' },
  },
  {
    path: '/console/article',
    name: 'Article',
    component: () => import('@/views/Article.vue'),
    meta: { title: '文章管理' },
  },
  {
    path: '/console/project',
    name: 'ProjectManage',
    component: () => import('@/views/Project.vue'),
    meta: { title: '项目管理', roles: ['PROJECT_MANAGER', 'SUPER_ADMIN', 'OP_ADMIN', 'DEPT_ADMIN'] },
  },
  {
    path: '/console/article/:id/edit',
    name: 'ArticleEdit',
    component: () => import('@/views/ArticleEdit.vue'),
    meta: { title: '编辑文章' },
  },
  {
    path: '/console/article/:id',
    name: 'ArticleView',
    component: () => import('@/views/ArticleView.vue'),
    meta: { title: '查看文章' },
  },
  {
    path: '/console/resource',
    name: 'ResourceCenter',
    component: () => import('@/views/ResourceCenter.vue'),
    meta: { title: '资源中心' },
  },
  {
    path: '/console/stats',
    name: 'OutputStats',
    component: () => import('@/views/OutputStats.vue'),
    meta: { title: '产出统计' },
  },
  {
    path: '/console/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue'),
    meta: { title: '个人设置' },
  },
  {
    path: '/console/messages',
    name: 'MessageCenter',
    component: () => import('@/views/MessageCenter.vue'),
    meta: { title: '消息中心' },
  },
]

// 使用 Layout 的路由
const layoutRoute = {
  path: '/console',
  component: Layout,
  redirect: '/console/dashboard',
  children: consoleRoutes,
}

// 404 重定向
const notFoundRoute = {
  path: '/:pathMatch(.*)*',
  redirect: '/console/dashboard',
}

// 添加登录路由
router.addRoute(loginRoute)

// 添加 Layout 路由
router.addRoute(layoutRoute)

// 添加 404 路由
router.addRoute(notFoundRoute)

export default router
