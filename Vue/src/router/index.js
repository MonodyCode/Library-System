import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import ReaderLayout from '../layouts/ReaderLayout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, userType: 2 },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'books',
        name: 'BookManagement',
        component: () => import('../views/admin/BookManagement.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue')
      },
      {
        path: 'borrows',
        name: 'BorrowManagement',
        component: () => import('../views/admin/BorrowManagement.vue')
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('../views/admin/Statistics.vue')
      }
    ]
  },
  {
    path: '/reader',
    component: ReaderLayout,
    meta: { requiresAuth: true, userType: 1 },
    children: [
      {
        path: '',
        redirect: '/reader/books'
      },
      {
        path: 'books',
        name: 'BookList',
        component: () => import('../views/reader/BookList.vue')
      },
      {
        path: 'my-borrows',
        name: 'MyBorrows',
        component: () => import('../views/reader/MyBorrows.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/reader/Profile.vue')
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!user) {
      next('/login')
    } else if (to.meta.userType && user.userType !== to.meta.userType) {
      // 用户类型不匹配，重定向到对应的首页
      if (user.userType === 1) {
        next('/reader')
      } else if (user.userType === 2) {
        next('/admin')
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router