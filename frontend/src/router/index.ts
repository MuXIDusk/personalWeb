import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/AboutView.vue'),
    meta: { title: '关于我' }
  },
  {
    path: '/media',
    name: 'Media',
    component: () => import('../views/MediaView.vue'),
    meta: { title: '媒体库' },
    children: [
      {
        path: '',
        name: 'MediaLibrary',
        component: () => import('../views/media/MediaLibraryView.vue')
      },
      {
        path: ':type/:id',
        name: 'MediaDetail',
        component: () => import('../views/media/MediaDetailView.vue'),
        props: true
      }
    ]
  },
  {
    path: '/political',
    name: 'Political',
    component: () => import('../views/PoliticalView.vue'),
    meta: { title: '政治观点' }
  },
  {
    path: '/blog',
    name: 'Blog',
    component: () => import('../views/BlogView.vue'),
    meta: { title: '博客' },
    children: [
      {
        path: '',
        name: 'BlogList',
        component: () => import('../views/blog/BlogListView.vue')
      },
      {
        path: 'post/:id',
        name: 'BlogPost',
        component: () => import('../views/blog/BlogPostView.vue'),
        props: true
      },
      {
        path: 'admin',
        name: 'BlogAdmin',
        component: () => import('../views/blog/BlogAdminView.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFoundView.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 个人网站`
  }
  
  // 简单的权限检查（后续可以根据实际需要扩展）
  if (to.meta.requiresAuth) {
    // 这里可以加入实际的认证逻辑
    console.log('需要认证的页面')
  }
  
  next()
})

export default router 