import { createRouter, createWebHistory } from 'vue-router'

// 懒加载页面
const Layout = () => import('@/views/Layout.vue')
const Home = () => import('@/views/Home.vue')
const Login = () => import('@/views/Login.vue')
const Register = () => import('@/views/Register.vue')
const BookList = () => import('@/views/BookList.vue')
const BookDetail = () => import('@/views/BookDetail.vue')
const Cart = () => import('@/views/Cart.vue')
const Orders = () => import('@/views/Orders.vue')
const Profile = () => import('@/views/Profile.vue')

// 路由配置
const routes = [
  { path: '/login', name: 'Login', component: Login, meta: { title: '登录' } },
  { path: '/register', name: 'Register', component: Register, meta: { title: '注册' } },
  {
    path: '/',
    component: Layout,
    children: [
      { path: '', name: 'Home', component: Home, meta: { title: '首页' } },
      { path: 'books', name: 'BookList', component: BookList, meta: { title: '图书列表' } },
      { path: 'book/:id', name: 'BookDetail', component: BookDetail, meta: { title: '图书详情' } },
      { path: 'cart', name: 'Cart', component: Cart, meta: { title: '购物车', requireAuth: true } },
      { path: 'orders', name: 'Orders', component: Orders, meta: { title: '我的订单', requireAuth: true } },
      { path: 'profile', name: 'Profile', component: Profile, meta: { title: '个人中心', requireAuth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫：需要登录的页面校验 token
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if ((to.path === '/login' || to.path === '/register') && token) {
    return next('/')
  }
  if (to.meta.requireAuth && !token) {
    return next('/login')
  }
  next()
})

export default router
