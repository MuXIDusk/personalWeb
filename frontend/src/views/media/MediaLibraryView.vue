<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMediaStore, type MediaType, type MediaStatus } from '../../stores/media'
import { Search, Grid, List, Filter, Star, Eye, Calendar, ChevronDown } from 'lucide-vue-next'

const mediaStore = useMediaStore()

// 响应式状态
const searchQuery = ref('')
const selectedType = ref<MediaType | ''>('')
const selectedStatus = ref<MediaStatus | ''>('')
const selectedGenre = ref('')
const viewMode = ref<'grid' | 'list'>('grid')
const isFilterOpen = ref(false)

// 媒体类型选项
const mediaTypes = [
  { value: '', label: '全部类型' },
  { value: 'book', label: '书籍' },
  { value: 'movie', label: '电影' },
  { value: 'tv', label: '电视剧' },
  { value: 'anime', label: '动画' }
]

// 状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'completed', label: '已完成' },
  { value: 'watching', label: '观看中' },
  { value: 'want-to-watch', label: '计划观看' }
]

// 计算属性
const filteredItems = computed(() => {
  mediaStore.setFilter({
    type: selectedType.value || undefined,
    status: selectedStatus.value || undefined,
    genre: selectedGenre.value || undefined,
    query: searchQuery.value || undefined
  })
  return mediaStore.filteredItems
})

// 应用搜索筛选
const applyFilters = () => {
  // 筛选逻辑在 computed 中已处理
  isFilterOpen.value = false
}

// 重置筛选
const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedStatus.value = ''
  selectedGenre.value = ''
  isFilterOpen.value = false
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 获取状态标签
const getStatusLabel = (status: MediaStatus) => {
  const labels = {
    'completed': '已完成',
    'watching': '观看中',
    'want-to-watch': '计划观看'
  }
  return labels[status]
}

// 获取状态颜色
const getStatusColor = (status: MediaStatus) => {
  const colors = {
    'completed': 'bg-green-100 text-green-800',
    'watching': 'bg-blue-100 text-blue-800',
    'want-to-watch': 'bg-yellow-100 text-yellow-800'
  }
  return colors[status]
}

onMounted(() => {
  // 页面加载时的初始化逻辑
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 页面标题和操作栏 -->
    <section class="bg-white shadow-sm">
      <div class="container mx-auto px-4 py-6">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <!-- 标题 -->
          <div>
            <h1 class="text-3xl font-bold text-gray-800">媒体库</h1>
            <p class="text-gray-600 mt-1">管理和浏览我的收藏</p>
          </div>
          
          <!-- 视图切换 -->
          <div class="flex items-center space-x-2">
            <button
              @click="viewMode = 'grid'"
              class="p-2 rounded-lg transition-colors duration-200"
              :class="viewMode === 'grid' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100'"
            >
              <Grid class="w-5 h-5" />
            </button>
            <button
              @click="viewMode = 'list'"
              class="p-2 rounded-lg transition-colors duration-200"
              :class="viewMode === 'list' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100'"
            >
              <List class="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 搜索和筛选区域 -->
    <section class="bg-white border-t">
      <div class="container mx-auto px-4 py-4">
        <div class="flex flex-col md:flex-row gap-4">
          <!-- 搜索框 -->
          <div class="flex-1 relative">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索标题、作者或导演..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <!-- 快速筛选 -->
          <div class="flex flex-wrap gap-2">
            <select
              v-model="selectedType"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option v-for="type in mediaTypes" :key="type.value" :value="type.value">
                {{ type.label }}
              </option>
            </select>
            
            <select
              v-model="selectedStatus"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option v-for="status in statusOptions" :key="status.value" :value="status.value">
                {{ status.label }}
              </option>
            </select>
            
            <!-- 高级筛选按钮 -->
            <button
              @click="isFilterOpen = !isFilterOpen"
              class="flex items-center px-3 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors duration-200"
            >
              <Filter class="w-4 h-4 mr-2" />
              高级筛选
              <ChevronDown class="w-4 h-4 ml-1 transition-transform duration-200" :class="{ 'rotate-180': isFilterOpen }" />
            </button>
            
            <!-- 重置按钮 -->
            <button
              @click="resetFilters"
              class="px-4 py-2 text-gray-600 hover:text-gray-800 hover:bg-gray-50 rounded-lg transition-colors duration-200"
            >
              重置
            </button>
          </div>
        </div>
        
        <!-- 高级筛选面板 -->
        <div v-show="isFilterOpen" class="mt-4 p-4 bg-gray-50 rounded-lg">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- 类型筛选 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">类型筛选</label>
              <select
                v-model="selectedGenre"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">全部类型</option>
                <option v-for="genre in mediaStore.allGenres" :key="genre" :value="genre">
                  {{ genre }}
                </option>
              </select>
            </div>
          </div>
          
          <div class="mt-4 flex justify-end space-x-2">
            <button
              @click="isFilterOpen = false"
              class="px-4 py-2 text-gray-600 hover:text-gray-800 rounded-lg transition-colors duration-200"
            >
              取消
            </button>
            <button
              @click="applyFilters"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200"
            >
              应用筛选
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 媒体内容区域 -->
    <section class="py-8">
      <div class="container mx-auto px-4">
        <!-- 结果统计 -->
        <div class="mb-6">
          <p class="text-gray-600">
            找到 <span class="font-semibold text-gray-800">{{ filteredItems.length }}</span> 个结果
          </p>
        </div>
        
        <!-- 网格视图 -->
        <div v-if="viewMode === 'grid'" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          <div
            v-for="item in filteredItems"
            :key="item.id"
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden group cursor-pointer"
            @click="$router.push(`/media/${item.type}/${item.id}`)"
          >
            <!-- 封面图片 -->
            <div class="aspect-[2/3] overflow-hidden">
              <img
                :src="item.cover"
                :alt="item.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                loading="lazy"
              />
            </div>
            
            <!-- 内容信息 -->
            <div class="p-4">
              <h3 class="font-semibold text-gray-800 line-clamp-2 mb-2">{{ item.title }}</h3>
              <p class="text-sm text-gray-600 mb-2">{{ item.creator }}</p>
              
              <!-- 评分和状态 -->
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <Star class="w-4 h-4 text-yellow-400 fill-current" v-if="item.rating" />
                  <span class="text-sm text-gray-600 ml-1" v-if="item.rating">{{ item.rating }}</span>
                </div>
                <span
                  class="px-2 py-1 text-xs rounded-full"
                  :class="getStatusColor(item.status)"
                >
                  {{ getStatusLabel(item.status) }}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 列表视图 -->
        <div v-else class="bg-white rounded-lg shadow overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">标题</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">类型</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">创作者</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">评分</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">添加日期</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr
                  v-for="item in filteredItems"
                  :key="item.id"
                  class="hover:bg-gray-50 cursor-pointer transition-colors duration-200"
                  @click="$router.push(`/media/${item.type}/${item.id}`)"
                >
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <img
                        :src="item.cover"
                        :alt="item.title"
                        class="w-12 h-16 object-cover rounded mr-4"
                        loading="lazy"
                      />
                      <div>
                        <div class="text-sm font-medium text-gray-900">{{ item.title }}</div>
                        <div class="text-sm text-gray-500" v-if="item.originalTitle">{{ item.originalTitle }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded">
                      {{ mediaTypes.find(t => t.value === item.type)?.label }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ item.creator }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center" v-if="item.rating">
                      <Star class="w-4 h-4 text-yellow-400 fill-current mr-1" />
                      <span class="text-sm text-gray-900">{{ item.rating }}</span>
                    </div>
                    <span v-else class="text-sm text-gray-400">未评分</span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      class="px-2 py-1 text-xs rounded-full"
                      :class="getStatusColor(item.status)"
                    >
                      {{ getStatusLabel(item.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(item.dateAdded) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="filteredItems.length === 0" class="text-center py-12">
          <div class="text-gray-400 mb-4">
            <Eye class="w-16 h-16 mx-auto" />
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">没有找到相关内容</h3>
          <p class="text-gray-500">
            尝试调整搜索条件或筛选器，或者
            <router-link to="/media" class="text-blue-600 hover:text-blue-700">浏览全部内容</router-link>
          </p>
        </div>
      </div>
    </section>
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

/* 过渡动画 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

/* 表格行悬停效果 */
tbody tr:hover {
  background-color: #f9fafb;
}

/* 媒体查询优化 */
@media (max-width: 640px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
  }
}
</style> 