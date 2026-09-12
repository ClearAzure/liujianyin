import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '../stores/userStore'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/search', name: 'Search', component: () => import('../views/Search.vue') },
  { path: '/favorite', name: 'Favorite', component: () => import('../views/Favorite.vue'), meta: { requiresAuth: true } },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } },
  { path: '/history', name: 'History', component: () => import('../views/History.vue'), meta: { requiresAuth: true } },
  { path: '/playlist/:id', name: 'PlaylistDetail', component: () => import('../views/PlaylistDetail.vue'), meta: { requiresAuth: true } },
  { path: '/artists', name: 'Artists', component: () => import('../views/Artists.vue') },
  { path: '/albums', name: 'Albums', component: () => import('../views/Albums.vue') },
  { path: '/artist/:id', name: 'ArtistDetail', component: () => import('../views/ArtistDetail.vue') },
  { path: '/album/:id', name: 'AlbumDetail', component: () => import('../views/AlbumDetail.vue') },
  { path: '/upload', name: 'Upload', component: () => import('../views/Upload.vue'), meta: { requiresAuth: true } },
  { path: '/desktop-lyric', name: 'DesktopLyric', component: () => import('../views/DesktopLyric.vue') },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 需要登录的页面：未登录时拦截导航并弹出登录框，而不是跳回首页
router.beforeEach((to) => {
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    if (!userStore.isLogin) {
      userStore.openLogin()
      return false // 取消本次导航，留在当前页
    }
  }
})

export default router
