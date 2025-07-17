<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useBlogStore, type BlogPost } from '../../stores/blog'
import { Search, Filter, Calendar, Eye, Tag, User, ChevronLeft, ChevronRight, Loader2 } from 'lucide-vue-next'

const blogStore = useBlogStore()

// 響應式狀態
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedTag = ref('')
const sortBy = ref<'date' | 'views' | 'title'>('date')
const searchResults = ref<BlogPost[]>([])
const isSearching = ref(false)

// 分頁狀態
const currentPage = ref(1)
const postsPerPage = ref(6)

// 計算屬性
const filteredPosts = computed(() => {
  let posts = searchQuery.value ? searchResults.value : blogStore.publishedPosts

  // 分類篩選
  if (selectedCategory.value) {
    posts = posts.filter(post => post.category === selectedCategory.value)
  }

  // 標籤篩選
  if (selectedTag.value) {
    posts = posts.filter(post => post.tags.includes(selectedTag.value))
  }

  // 排序
  posts.sort((a, b) => {
    switch (sortBy.value) {
      case 'date':
        return new Date(b.publishedAt || b.createdAt).getTime() - new Date(a.publishedAt || a.createdAt).getTime()
      case 'views':
        return b.viewCount - a.viewCount
      case 'title':
        return a.title.localeCompare(b.title)
      default:
        return 0
    }
  })

  return posts
})

const totalPages = computed(() => Math.ceil(filteredPosts.value.length / postsPerPage.value))

const paginatedPosts = computed(() => {
  const start = (currentPage.value - 1) * postsPerPage.value
  const end = start + postsPerPage.value
  return filteredPosts.value.slice(start, end)
})

// 熱門標籤
const popularTags = computed(() => {
  const tagCounts = new Map<string, number>()
  blogStore.publishedPosts.forEach(post => {
    post.tags.forEach(tag => {
      tagCounts.set(tag, (tagCounts.get(tag) || 0) + 1)
    })
  })
  return Array.from(tagCounts.entries())
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
    .map(([tag]) => tag)
})

// 按分類計算文章數量
const getCategoryPostCount = (category: string) => {
  return blogStore.publishedPosts.filter(post => post.category === category).length
}

// 方法
const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedTag.value = ''
  currentPage.value = 1
  searchResults.value = []
}

const performSearch = async () => {
  if (!searchQuery.value.trim()) {
    searchResults.value = []
    return
  }

  try {
    isSearching.value = true
    searchResults.value = await blogStore.searchPosts(searchQuery.value)
    currentPage.value = 1
  } catch (error) {
    console.error('搜索失敗:', error)
    searchResults.value = []
  } finally {
    isSearching.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getReadingTime = (content: string) => {
  const wordsPerMinute = 200
  const wordCount = content.replace(/<[^>]*>/g, '').split(/\s+/).length
  return Math.ceil(wordCount / wordsPerMinute)
}

const goToPage = (page: number) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 監聽搜索查詢變化
let searchTimeout: number
watch(searchQuery, () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(performSearch, 500) // 防抖搜索
})

onMounted(async () => {
  // 初始化數據
  await blogStore.initialize()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 頁面標題 -->
    <section class="bg-white py-16">
      <div class="container mx-auto px-4">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold text-gray-800 mb-4">我的博客</h1>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto">
            分享技术见解、生活感悟和思考随笔
          </p>
        </div>
      </div>
    </section>

    <div class="container mx-auto px-4 py-8">
      <!-- 加載狀態 -->
      <div v-if="blogStore.loading" class="flex justify-center items-center py-12">
        <Loader2 class="w-8 h-8 animate-spin text-blue-600" />
        <span class="ml-2 text-gray-600">加載中...</span>
      </div>

      <!-- 錯誤狀態 -->
      <div v-else-if="blogStore.error" class="text-center py-12">
        <div class="text-red-500 mb-4">
          <p class="text-lg font-medium">加載失敗</p>
          <p class="text-sm">{{ blogStore.error }}</p>
        </div>
        <button 
          @click="blogStore.initialize()"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          重試
        </button>
      </div>

      <div v-else class="flex flex-col lg:flex-row gap-8">
        <!-- 主內容區域 -->
        <main class="lg:w-2/3">
          <!-- 搜索和篩選 -->
          <div class="bg-white rounded-lg shadow-md p-6 mb-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
              <!-- 搜索框 -->
              <div class="md:col-span-2 relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="搜索文章标题或内容..."
                  class="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <Loader2 
                  v-if="isSearching" 
                  class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4 animate-spin" 
                />
              </div>

              <!-- 分類篩選 -->
              <select
                v-model="selectedCategory"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">全部分类</option>
                <option v-for="category in blogStore.categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>

              <!-- 排序選項 -->
              <select
                v-model="sortBy"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="date">按日期排序</option>
                <option value="views">按阅读量排序</option>
                <option value="title">按标题排序</option>
              </select>
            </div>

            <!-- 篩選統計和重置 -->
            <div class="flex justify-between items-center mt-4 pt-4 border-t border-gray-200">
              <p class="text-gray-600">
                找到 <span class="font-semibold text-gray-800">{{ filteredPosts.length }}</span> 篇文章
                <span v-if="searchQuery" class="text-blue-600">
                  关于 "{{ searchQuery }}"
                </span>
              </p>
              <button
                @click="resetFilters"
                class="text-blue-600 hover:text-blue-700 font-medium"
              >
                重置篩選
              </button>
            </div>
          </div>

          <!-- 文章列表 -->
          <div class="space-y-6">
            <div
              v-for="post in paginatedPosts"
              :key="post.id"
              class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden cursor-pointer"
              @click="$router.push(`/blog/post/${post.id}`)"
            >
              <div class="p-6">
                <!-- 文章元信息 -->
                <div class="flex items-center text-sm text-gray-500 mb-3">
                  <Calendar class="w-4 h-4 mr-1" />
                  <span class="mr-4">{{ formatDate(post.publishedAt || post.createdAt) }}</span>
                  
                  <Eye class="w-4 h-4 mr-1" />
                  <span class="mr-4">{{ post.viewCount }} 次阅读</span>
                  
                  <User class="w-4 h-4 mr-1" />
                  <span>{{ post.author }}</span>
                  
                  <span class="ml-auto">{{ getReadingTime(post.content) }} 分钟阅读</span>
                </div>

                <!-- 標題和摘要 -->
                <h2 class="text-xl font-semibold text-gray-800 mb-3 hover:text-blue-600 transition-colors">
                  {{ post.title }}
                </h2>
                
                <p class="text-gray-600 leading-relaxed mb-4 line-clamp-3">
                  {{ post.excerpt }}
                </p>

                <!-- 分類和標籤 -->
                <div class="flex flex-wrap items-center gap-2">
                  <span class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                    {{ post.category }}
                  </span>
                  
                  <div class="flex flex-wrap gap-1">
                    <span
                      v-for="tag in post.tags.slice(0, 3)"
                      :key="tag"
                      class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs cursor-pointer hover:bg-gray-200"
                      @click.stop="selectedTag = tag; currentPage = 1"
                    >
                      #{{ tag }}
                    </span>
                    <span v-if="post.tags.length > 3" class="text-gray-400 text-xs">
                      +{{ post.tags.length - 3 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空狀態 -->
          <div v-if="paginatedPosts.length === 0 && !blogStore.loading" class="text-center py-12">
            <div class="text-gray-400 mb-4">
              <Search class="w-16 h-16 mx-auto" />
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">没有找到相关文章</h3>
            <p class="text-gray-500">
              尝试调整搜索条件或筛选器，或者
              <button @click="resetFilters" class="text-blue-600 hover:text-blue-700">重置筛选</button>
            </p>
          </div>

          <!-- 分頁組件 -->
          <div v-if="totalPages > 1" class="flex justify-center mt-8">
            <nav class="flex items-center space-x-2">
              <button
                @click="goToPage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="flex items-center px-3 py-2 text-gray-500 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <ChevronLeft class="w-4 h-4 mr-1" />
                上一页
              </button>
              
              <div class="flex space-x-1">
                <button
                  v-for="page in totalPages"
                  :key="page"
                  @click="goToPage(page)"
                  class="px-3 py-2 rounded-lg transition-colors"
                  :class="[
                    page === currentPage
                      ? 'bg-blue-600 text-white'
                      : 'text-gray-700 hover:bg-gray-100'
                  ]"
                >
                  {{ page }}
                </button>
              </div>
              
              <button
                @click="goToPage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="flex items-center px-3 py-2 text-gray-500 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                下一页
                <ChevronRight class="w-4 h-4 ml-1" />
              </button>
            </nav>
          </div>
        </main>

        <!-- 側邊欄 -->
        <aside class="lg:w-1/3">
          <!-- 分類導航 -->
          <div class="bg-white rounded-lg shadow-md p-6 mb-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">文章分类</h3>
            <div class="space-y-2">
              <button
                @click="selectedCategory = ''; currentPage = 1"
                class="block w-full text-left px-3 py-2 rounded-lg transition-colors"
                :class="selectedCategory === '' ? 'bg-blue-100 text-blue-800' : 'text-gray-700 hover:bg-gray-100'"
              >
                全部分类 ({{ blogStore.publishedPosts.length }})
              </button>
              <button
                v-for="category in blogStore.categories"
                :key="category"
                @click="selectedCategory = category; currentPage = 1"
                class="block w-full text-left px-3 py-2 rounded-lg transition-colors"
                :class="selectedCategory === category ? 'bg-blue-100 text-blue-800' : 'text-gray-700 hover:bg-gray-100'"
              >
                {{ category }} ({{ getCategoryPostCount(category) }})
              </button>
            </div>
          </div>

          <!-- 熱門標籤 -->
          <div class="bg-white rounded-lg shadow-md p-6 mb-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">热门标签</h3>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="tag in popularTags"
                :key="tag"
                @click="selectedTag = selectedTag === tag ? '' : tag; currentPage = 1"
                class="px-3 py-1 rounded-full text-sm transition-colors"
                :class="selectedTag === tag 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
              >
                <Tag class="w-3 h-3 inline mr-1" />
                {{ tag }}
              </button>
            </div>
          </div>

          <!-- 最新文章 -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">最新文章</h3>
            <div class="space-y-4">
              <div
                v-for="post in blogStore.publishedPosts.slice(0, 5)"
                :key="post.id"
                class="cursor-pointer group"
                @click="$router.push(`/blog/post/${post.id}`)"
              >
                <h4 class="font-medium text-gray-800 group-hover:text-blue-600 transition-colors mb-1 line-clamp-2">
                  {{ post.title }}
                </h4>
                <div class="flex items-center text-xs text-gray-500">
                  <Calendar class="w-3 h-3 mr-1" />
                  <span>{{ formatDate(post.publishedAt || post.createdAt) }}</span>
                  <span class="mx-2">•</span>
                  <Eye class="w-3 h-3 mr-1" />
                  <span>{{ post.viewCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 文字行數限制 */
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

/* 過渡動畫 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

/* 分頁按鈕樣式 */
nav button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 響應式優化 */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .grid-cols-1.md\:grid-cols-4 {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
}

/* 加載動畫 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style> 