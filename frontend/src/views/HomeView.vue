<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '../stores/user'
import { useBlogStore } from '../stores/blog'
import { useMediaStore } from '../stores/media'
import { ArrowRight, Calendar, Eye, Star } from 'lucide-vue-next'

const userStore = useUserStore()
const blogStore = useBlogStore()
const mediaStore = useMediaStore()

// 獲取最新的博客文章（前3篇）
const latestPosts = computed(() => 
  blogStore.publishedPosts.slice(0, 3)
)

// 獲取評分最高的媒體項目（前4個）
const featuredMedia = computed(() =>
  mediaStore.items
    .filter(item => item.rating && item.rating >= 4)
    .sort((a, b) => (b.rating || 0) - (a.rating || 0))
    .slice(0, 4)
)

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 媒体类型对应的中文
const mediaTypeLabels: Record<string, string> = {
  book: '书籍',
  movie: '电影',
  tv: '电视剧',
  anime: '动画'
}
</script>

<template>
  <div class="min-h-screen">
    <!-- Hero Section -->
    <section class="bg-gradient-to-br from-blue-50 to-indigo-100 py-20">
      <div class="container mx-auto px-4">
        <div class="flex flex-col lg:flex-row items-center gap-12">
          <!-- 个人信息 -->
          <div class="lg:w-1/2 space-y-6">
            <h1 class="text-4xl md:text-5xl font-bold text-gray-800 leading-tight">
              你好，我是 
              <span class="text-blue-600">{{ userStore.profile.name }}</span>
            </h1>
            <p class="text-xl text-gray-600">{{ userStore.profile.title }}</p>
            <p class="text-lg text-gray-700 leading-relaxed">
              {{ userStore.profile.bio }}
            </p>
            
            <!-- 技能标签 -->
            <div class="flex flex-wrap gap-2">
              <span
                v-for="skill in userStore.profile.skills"
                :key="skill"
                class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium"
              >
                {{ skill }}
              </span>
            </div>

            <!-- CTA 按钮 -->
            <div class="flex flex-col sm:flex-row gap-4 pt-4">
              <router-link
                to="/about"
                class="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 font-medium"
              >
                了解更多
                <ArrowRight class="ml-2 w-4 h-4" />
              </router-link>
              <router-link
                to="/blog"
                class="inline-flex items-center px-6 py-3 border-2 border-blue-600 text-blue-600 rounded-lg hover:bg-blue-600 hover:text-white transition-all duration-200 font-medium"
              >
                阅读博客
              </router-link>
            </div>
          </div>

          <!-- 头像 -->
          <div class="lg:w-1/2 flex justify-center">
            <div class="relative">
              <img
                :src="userStore.profile.avatar"
                :alt="userStore.profile.name"
                class="w-64 h-64 rounded-full object-cover shadow-2xl border-4 border-white"
              />
              <div class="absolute inset-0 rounded-full bg-gradient-to-tr from-blue-400/20 to-purple-400/20"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新博客文章 -->
    <section class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center mb-10">
          <h2 class="text-3xl font-bold text-gray-800">最新文章</h2>
          <router-link
            to="/blog"
            class="text-blue-600 hover:text-blue-700 font-medium flex items-center"
          >
            查看全部
            <ArrowRight class="ml-1 w-4 h-4" />
          </router-link>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <article
            v-for="post in latestPosts"
            :key="post.id"
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden border border-gray-100"
          >
            <div v-if="post.coverImage" class="h-48 bg-gray-200">
              <img
                :src="post.coverImage"
                :alt="post.title"
                class="w-full h-full object-cover"
              />
            </div>
            <div class="p-6">
              <div class="flex items-center gap-2 text-sm text-gray-500 mb-3">
                <Calendar class="w-4 h-4" />
                <span>{{ formatDate(post.publishedAt || post.createdAt) }}</span>
                <span>•</span>
                <Eye class="w-4 h-4" />
                <span>{{ post.viewCount }} 次阅读</span>
              </div>
              
              <h3 class="text-xl font-semibold text-gray-800 mb-3 line-clamp-2">
                {{ post.title }}
              </h3>
              
              <p class="text-gray-600 mb-4 line-clamp-3">
                {{ post.excerpt }}
              </p>
              
              <div class="flex justify-between items-center">
                <span class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-sm">
                  {{ post.category }}
                </span>
                
                <router-link
                  :to="`/blog/post/${post.id}`"
                  class="text-blue-600 hover:text-blue-700 font-medium"
                >
                  阅读更多
                </router-link>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <!-- 精选媒体 -->
    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center mb-10">
          <h2 class="text-3xl font-bold text-gray-800">精选收藏</h2>
          <router-link
            to="/media"
            class="text-blue-600 hover:text-blue-700 font-medium flex items-center"
          >
            查看媒体库
            <ArrowRight class="ml-1 w-4 h-4" />
          </router-link>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div
            v-for="item in featuredMedia"
            :key="item.id"
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-all duration-200 overflow-hidden group cursor-pointer"
            @click="$router.push(`/media/${item.type}/${item.id}`)"
          >
            <div class="aspect-[3/4] bg-gray-200 overflow-hidden">
              <img
                :src="item.cover"
                :alt="item.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
              />
            </div>
            <div class="p-4">
              <p class="text-xs text-gray-500 mb-1">{{ mediaTypeLabels[item.type] }}</p>
              <h3 class="font-semibold text-gray-800 text-sm line-clamp-2 mb-2">
                {{ item.title }}
              </h3>
              <div class="flex items-center justify-between">
                <span class="text-xs text-gray-600">{{ item.creator }}</span>
                <div class="flex items-center">
                  <Star class="w-3 h-3 text-yellow-500 fill-current" />
                  <span class="text-xs text-gray-600 ml-1">{{ item.rating }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 限制文字行数 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 渐变背景动画 */
.bg-gradient-to-br {
  background-image: linear-gradient(to bottom right, var(--tw-gradient-stops));
}
</style> 