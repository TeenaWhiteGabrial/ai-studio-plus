import { createRouter, createWebHashHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

// 静态路由
const staticRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据看板' },
      },
      {
        path: 'resource/skill',
        name: 'Skill',
        component: () => import('@/views/skill/index.vue'),
        meta: { title: 'Skill管理' },
      },
      {
        path: 'resource/mcp',
        name: 'Mcp',
        component: () => import('@/views/mcp/index.vue'),
        meta: { title: 'MCP管理' },
      },
      {
        path: 'resource/plugin',
        name: 'Plugin',
        component: () => import('@/views/plugin/index.vue'),
        meta: { title: 'Plugin管理' },
      },
      {
        path: 'resource/tutorial',
        name: 'Tutorial',
        component: () => import('@/views/tutorial/index.vue'),
        meta: { title: '教程管理' },
      },
      {
        path: 'tutorial/:id/read',
        name: 'TutorialRead',
        component: () => import('@/views/tutorial/read.vue'),
        meta: { title: '阅读教程' },
      },
      {
        path: 'output/my',
        name: 'MyOutput',
        component: () => import('@/views/output/my.vue'),
        meta: { title: '我的产出' },
      },
      {
        path: 'output/history',
        name: 'OutputHistory',
        component: () => import('@/views/output/history.vue'),
        meta: { title: '产出历史' },
      },
      {
        path: 'output/admin',
        name: 'AdminOutput',
        component: () => import('@/views/output/admin.vue'),
        meta: { title: '全员产出' },
      },
      {
        path: 'stats/department',
        name: 'DepartmentStats',
        component: () => import('@/views/stats/DepartmentStats.vue'),
        meta: { title: '部门统计' },
      },
      {
        path: 'stats/project',
        name: 'ProjectStats',
        component: () => import('@/views/stats/ProjectStats.vue'),
        meta: { title: '项目统计' },
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'system/department',
        name: 'DepartmentManage',
        component: () => import('@/views/department/index.vue'),
        meta: { title: '部门管理' },
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/role/index.vue'),
        meta: { title: '角色管理' },
      }
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes: staticRoutes,
})

// 路由守卫：校验 Token
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    return '/login'
  }
  if (to.path === '/login' && token) {
    return '/dashboard'
  }
})

export default router
