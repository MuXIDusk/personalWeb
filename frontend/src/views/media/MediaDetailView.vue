<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMediaStore } from '../../stores/media'
import { ArrowLeft, Star, Calendar, User, Tag, Link, Edit, Trash2 } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const mediaStore = useMediaStore()

const itemId = route.params.id as string
const itemType = route.params.type as string

const item = computed(() => mediaStore.getMediaItem(itemId))

// 相关推荐
const relatedItems = computed(() => {
  if (!item.value) return []
  
  return mediaStore.items
    .filter(i => 
      i.id !== item.value!.id && 
      (i.type === item.value!.type || 
       i.genre.some(g => item.value!.genre.includes(g)))
    )
    .slice(0, 6)
})

// 媒体类型标签
const mediaTypeLabels: Record<string, string> = {
  book: '书籍',
  movie: '电影',
  tv: '电视剧',
  anime: '动画'
}

// 状态标签
const statusLabels: Record<string, string> = {
  completed: '已完成',
  watching: '观看中',
  'want-to-watch': '计划观看'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusColor = (status: string) => {
  const colors = {
    'completed': 'bg-green-100 text-green-800',
    'watching': 'bg-blue-100 text-blue-800',
    'want-to-watch': 'bg-yellow-100 text-yellow-800'
  }
  return colors[status as keyof typeof colors] || 'bg-gray-100 text-gray-800'
}

const getRatingStars = (rating: number) => {
  const fullStars = Math.floor(rating)
  const hasHalfStar = rating % 1 >= 0.5
  const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0)
  
  return {
    full: fullStars,
    half: hasHalfStar,
    empty: emptyStars
  }
}

onMounted(() => {
  if (!item.value) {
    router.push('/media')
  }
})
</script>

<template>
  <div v-if="item" class="min-h-screen bg-gray-50">
    <!-- 返回按钮 -->
    <div class="bg-white border-b">
      <div class="container mx-auto px-4 py-4">
        <button
          @click="$router.go(-1)"
          class="flex items-center text-blue-600 hover:text-blue-700 font-medium transition-colors"
        >
          <ArrowLeft class="w-4 h-4 mr-2" />
          返回媒体库
        </button>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="container mx-auto px-4 py-8">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- 左侧：封面和基本信息 -->
        <div class="lg:w-1/3">
          <div class="bg-white rounded-lg shadow-md overflow-hidden sticky top-8">
            <!-- 封面图片 -->
            <div class="aspect-[2/3] bg-gray-200">
              <img
                :src="item.cover"
                :alt="item.title"
                class="w-full h-full object-cover"
                loading="lazy"
              />
            </div>

            <!-- 基本信息 -->
            <div class="p-6">
              <!-- 媒体类型 -->
              <div class="flex items-center justify-between mb-4">
                <span class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                  {{ mediaTypeLabels[item.type] }}
                </span>
                <span
                  class="px-3 py-1 rounded-full text-sm font-medium"
                  :class="getStatusColor(item.status)"
                >
                  {{ statusLabels[item.status] }}
                </span>
              </div>

              <!-- 评分 -->
              <div v-if="item.rating" class="mb-4">
                <div class="flex items-center mb-2">
                  <Star class="w-5 h-5 text-yellow-400 fill-current mr-2" />
                  <span class="text-lg font-semibold text-gray-900">{{ item.rating }}</span>
                  <span class="text-gray-500 ml-1">/5</span>
                </div>
                <div class="flex items-center">
                  <div
                    v-for="i in getRatingStars(item.rating).full"
                    :key="`full-${i}`"
                    class="text-yellow-400"
                  >
                    <Star class="w-4 h-4 fill-current" />
                  </div>
                  <div
                    v-if="getRatingStars(item.rating).half"
                    class="text-yellow-400"
                  >
                    <Star class="w-4 h-4 fill-current" style="clip-path: inset(0 50% 0 0)" />
                  </div>
                  <div
                    v-for="i in getRatingStars(item.rating).empty"
                    :key="`empty-${i}`"
                    class="text-gray-300"
                  >
                    <Star class="w-4 h-4" />
                  </div>
                </div>
              </div>

              <!-- 详细信息 -->
              <div class="space-y-3">
                <div class="flex items-center text-gray-600">
                  <User class="w-4 h-4 mr-3 flex-shrink-0" />
                  <span class="text-sm">{{ item.creator }}</span>
                </div>
                
                <div class="flex items-center text-gray-600">
                  <Calendar class="w-4 h-4 mr-3 flex-shrink-0" />
                  <span class="text-sm">添加于 {{ formatDate(item.dateAdded) }}</span>
                </div>
                
                <div v-if="item.dateWatched" class="flex items-center text-gray-600">
                  <Calendar class="w-4 h-4 mr-3 flex-shrink-0" />
                  <span class="text-sm">完成于 {{ formatDate(item.dateWatched) }}</span>
                </div>
              </div>

              <!-- 外部链接 -->
              <div v-if="item.externalLinks" class="mt-6 pt-6 border-t border-gray-200">
                <h4 class="text-sm font-medium text-gray-900 mb-3">外部链接</h4>
                <div class="space-y-2">
                  <a
                    v-if="item.externalLinks.douban"
                    :href="item.externalLinks.douban"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="flex items-center text-sm text-blue-600 hover:text-blue-700"
                  >
                    <Link class="w-4 h-4 mr-2" />
                    豆瓣页面
                  </a>
                  <a
                    v-if="item.externalLinks.imdb"
                    :href="item.externalLinks.imdb"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="flex items-center text-sm text-blue-600 hover:text-blue-700"
                  >
                    <Link class="w-4 h-4 mr-2" />
                    IMDB 页面
                  </a>
                  <a
                    v-if="item.externalLinks.mal"
                    :href="item.externalLinks.mal"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="flex items-center text-sm text-blue-600 hover:text-blue-700"
                  >
                    <Link class="w-4 h-4 mr-2" />
                    MAL 页面
                  </a>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="mt-6 pt-6 border-t border-gray-200">
                <div class="flex space-x-2">
                  <button class="flex-1 flex items-center justify-center px-3 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                    <Edit class="w-4 h-4 mr-2" />
                    编辑
                  </button>
                  <button class="flex items-center justify-center px-3 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors">
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：详细内容 -->
        <div class="lg:w-2/3 space-y-6">
          <!-- 标题和摘要 -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ item.title }}</h1>
            <p v-if="item.originalTitle" class="text-lg text-gray-600 mb-4">{{ item.originalTitle }}</p>
            
            <!-- 类型标签 -->
            <div class="flex flex-wrap gap-2 mb-6">
              <span
                v-for="genre in item.genre"
                :key="genre"
                class="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm"
              >
                {{ genre }}
              </span>
            </div>

            <!-- 标签 -->
            <div v-if="item.tags.length > 0" class="mb-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-3">标签</h3>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="tag in item.tags"
                  :key="tag"
                  class="inline-flex items-center px-2 py-1 bg-blue-100 text-blue-800 rounded text-sm"
                >
                  <Tag class="w-3 h-3 mr-1" />
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>

          <!-- 个人评价 -->
          <div v-if="item.review" class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">我的评价</h3>
            <div class="prose prose-gray max-w-none">
              <p class="text-gray-700 leading-relaxed whitespace-pre-wrap">{{ item.review }}</p>
            </div>
          </div>

          <!-- 相关推荐 -->
          <div v-if="relatedItems.length > 0" class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-6">相关推荐</h3>
            <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
              <div
                v-for="relatedItem in relatedItems"
                :key="relatedItem.id"
                class="group cursor-pointer"
                @click="$router.push(`/media/${relatedItem.type}/${relatedItem.id}`)"
              >
                <div class="aspect-[2/3] bg-gray-200 rounded-lg overflow-hidden mb-2">
                  <img
                    :src="relatedItem.cover"
                    :alt="relatedItem.title"
                    class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                    loading="lazy"
                  />
                </div>
                <h4 class="text-sm font-medium text-gray-900 line-clamp-2 group-hover:text-blue-600 transition-colors">
                  {{ relatedItem.title }}
                </h4>
                <div class="flex items-center mt-1">
                  <Star v-if="relatedItem.rating" class="w-3 h-3 text-yellow-400 fill-current mr-1" />
                  <span v-if="relatedItem.rating" class="text-xs text-gray-600">{{ relatedItem.rating }}</span>
                  <span class="text-xs text-gray-500 ml-auto">{{ mediaTypeLabels[relatedItem.type] }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 项目不存在的情况 -->
  <div v-else class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="text-center">
      <h2 class="text-2xl font-bold text-gray-900 mb-4">项目未找到</h2>
      <p class="text-gray-600 mb-6">抱歉，您访问的媒体项目不存在或已被删除。</p>
      <button
        @click="$router.push('/media')"
        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        返回媒体库
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 文字行数限制 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 半星评分效果 */
.star-half {
  position: relative;
  overflow: hidden;
}

.star-half::after {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  right: 0;
  bottom: 0;
  background: white;
}

/* 悬停效果 */
.group:hover .group-hover\\:scale-105 {
  transform: scale(1.05);
}

.group:hover .group-hover\\:text-blue-600 {
  color: #2563eb;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .lg\\:w-1\\/3,
  .lg\\:w-2\\/3 {
    width: 100%;
  }
  
  .sticky {
    position: relative;
  }
}

/* 过渡动画 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}
</style> 