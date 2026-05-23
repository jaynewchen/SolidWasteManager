import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/layout/index'

Vue.use(Router)

export const constantRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index'), meta: { title: '仪表盘', icon: 'dashboard' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile/index'), meta: { title: '个人中心' }, hidden: true }
    ]
  },
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },
  { path: '/404', component: () => import('@/views/error/404'), hidden: true },
  { path: '/401', component: () => import('@/views/error/401'), hidden: true },
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export default router
