<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useBlogStore } from '../../stores/blog'
import { Search, Filter, Calendar, Eye, Tag, User, ChevronLeft, ChevronRight } from 'lucide-vue-next'

const blogStore = useBlogStore()

// 响应式状态
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedTag = ref('')
const sortBy = ref<'date' | 'views' | 'title'>('date')

// 分页状态
const currentPage = ref(1)
const postsPerPage = ref(6)

// 计算属性
const filteredPosts = computed(() => {
  let posts = blogStore.publishedPosts

  // 搜索筛选
  if (searchQuery.value) {
    posts = blogStore.searchPosts(searchQuery.value)
  }

  // 分类筛选
  if (selectedCategory.value) {
    posts = posts.filter(post => post.category === selectedCategory.value)
  }

  // 标签筛选
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

// 热门标签
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

// 方法
const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedTag.value = ''
  currentPage.value = 1
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

onMounted(() => {
  // 初始化逻辑
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 页面标题 -->
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
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- 主内容区域 -->
        <main class="lg:w-2/3">
          <!-- 搜索和筛选 -->
          <div class="bg-white rounded-lg shadow-md p-6 mb-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
              <!-- 搜索框 -->
              <div class="md:col-span-2 relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="搜索文章标题或内容..."
                  class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>

              <!-- 分类筛选 -->
              <select
                v-model="selectedCategory"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">全部分类</option>
                <option v-for="category in blogStore.categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>

              <!-- 排序选项 -->
              <select
                v-model="sortBy"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="date">按日期排序</option>
                <option value="views">按阅读量排序</option>
                <option value="title">按标题排序</option>
              </select>
            </div>

            <!-- 筛选统计和重置 -->
            <div class="flex justify-between items-center mt-4 pt-4 border-t border-gray-200">
              <p class="text-gray-600">
                找到 <span class="font-semibold text-gray-800">{{ filteredPosts.length }}</span> 篇文章
              </p>
              <button
                @click="resetFilters"
                class="text-blue-600 hover:text-blue-700 font-medium"
              >
                重置筛选
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

                <!-- 标题和摘要 -->
                <h2 class="text-xl font-semibold text-gray-800 mb-3 hover:text-blue-600 transition-colors">
                  {{ post.title }}
                </h2>
                
                <p class="text-gray-600 leading-relaxed mb-4 line-clamp-3">
                  {{ post.excerpt }}
                </p>

                <!-- 分类和标签 -->
                <div class="flex flex-wrap items-center gap-2">
                  <span class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                    {{ post.category }}
                  </span>
                  
                  <div class="flex flex-wrap gap-1">
                    <span
                      v-for="tag in post.tags.slice(0, 3)"
                      :key="tag"
                      class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs"
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

          <!-- 空状态 -->
          <div v-if="paginatedPosts.length === 0" class="text-center py-12">
            <div class="text-gray-400 mb-4">
              <Search class="w-16 h-16 mx-auto" />
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">没有找到相关文章</h3>
            <p class="text-gray-500">
              尝试调整搜索条件或筛选器，或者
              <button @click="resetFilters" class="text-blue-600 hover:text-blue-700">重置筛选</button>
            </p>
          </div>

          <!-- 分页组件 -->
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

        <!-- 侧边栏 -->
        <aside class="lg:w-1/3">
          <!-- 分类导航 -->
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
                {{ category }} ({{ blogStore.getPostsByCategory(category).length }})
              </button>
            </div>
          </div>

          <!-- 热门标签 -->
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
/* 文字行数限制 */
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

/* 过渡动画 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

/* 分页按钮样式 */
nav button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .grid-cols-1.md\\:grid-cols-4 {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
}
</style> 