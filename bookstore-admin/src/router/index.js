import { createRouter, createWebHistory } from 'vue-router'

// 懒加载页面组件
const Login = () => import('@/views/Login.vue')
const Layout = () => import('@/views/Layout.vue')
const Dashboard = () => import('@/views/Dashboard.vue')
const BookManage = () => import('@/views/BookManage.vue')
const OrderManage = () => import('@/views/OrderManage.vue')
const UserManage = () => import('@/views/UserManage.vue')
const AdminManage = () => import('@/views/AdminManage.vue')

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    children: [
      { path: '', name: 'Dashboard', component: Dashboard, meta: { title: '仪表盘' } },
      { path: 'books', name: 'BookManage', component: BookManage, meta: { title: '图书管理' } },
      { path: 'orders', name: 'OrderManage', component: OrderManage, meta: { title: '订单管理' } },
      { path: 'users', name: 'UserManage', component: UserManage, meta: { title: '用户管理' } },
      { path: 'admins', name: 'AdminManage', component: AdminManage, meta: { title: '管理员管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫：登录状态 + 管理员角色校验
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.path === '/login') {
    token ? next('/') : next()
  } else {
    if (!token) {
      return next('/login')
    }
    // 校验管理员角色（role >= 2）
    if (userInfo && userInfo.role < 2) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      return next('/login')
    }
    next()
  }
})

export default router
