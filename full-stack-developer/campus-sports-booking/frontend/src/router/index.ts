import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/layout/DashboardView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'venue',
        name: 'VenueList',
        component: () => import('@/views/venue/VenueListView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'venue/:id',
        name: 'VenueDetail',
        component: () => import('@/views/venue/VenueDetailView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'reservation/my',
        name: 'MyReservations',
        component: () => import('@/views/reservation/MyReservationView.vue'),
        meta: { requiresAuth: true }
      },
      // 管理员路由
      {
        path: 'admin/category',
        name: 'AdminCategory',
        component: () => import('@/views/system/AdminCategoryView.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/venue',
        name: 'AdminVenue',
        component: () => import('@/views/system/AdminVenueView.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/timeslot',
        name: 'AdminTimeSlot',
        component: () => import('@/views/system/AdminTimeSlotView.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/reservation',
        name: 'AdminReservation',
        component: () => import('@/views/system/AdminReservationView.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth !== false
  const requiresAdmin = to.meta.requiresAdmin === true

  if (requiresAuth && !authStore.isLoggedIn()) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if (requiresAdmin && !authStore.isAdmin()) {
    ElMessage.error('没有访问权限')
    next({ name: 'Dashboard' })
    return
  }

  if (to.name === 'Login' && authStore.isLoggedIn()) {
    next({ name: 'Dashboard' })
    return
  }

  next()
})

export default router

// 需要在 router 之后导入 Element Plus 的 ElMessage
import { ElMessage } from 'element-plus'
