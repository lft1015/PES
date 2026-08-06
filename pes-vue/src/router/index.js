import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/layout/AppLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: '/system/user',
        name: 'UserList',
        component: () => import('@/views/system/user/UserList.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/system/role',
        name: 'RoleList',
        component: () => import('@/views/system/role/RoleList.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: '/system/menu',
        name: 'MenuManage',
        component: () => import('@/views/system/menu/MenuManage.vue'),
        meta: { title: '菜单管理' }
      },
      {
        path: '/log/operation',
        name: 'OperationLog',
        component: () => import('@/views/log/OperationLog.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: '/log/login',
        name: 'LoginLog',
        component: () => import('@/views/log/LoginLog.vue'),
        meta: { title: '登录日志' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
