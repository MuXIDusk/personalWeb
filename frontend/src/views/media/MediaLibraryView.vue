<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMediaStore } from '../../stores/media'
import { Search, Filter, Grid, List, Star, Calendar, Eye, Book, Film, Tv, Palette, Loader2 } from 'lucide-vue-next'

const mediaStore = useMediaStore()

// 响应式状态
const searchQuery = ref('')
const selectedType = ref('')
const selectedStatus = ref('')
const selectedGenre = ref('')
const viewMode = ref<'grid' | 'list'>('grid')

// 图片加载失败状态
const failedImages = ref<Set<number>>(new Set())

// 计算属性
const filteredItems = computed(() => {
  let items = mediaStore.items

  // 类型筛选
  if (selectedType.value) {
    items = items.filter(item => item.type === selectedType.value)
  }

  // 状态筛选
  if (selectedStatus.value) {
    items = items.filter(item => item.status === selectedStatus.value)
  }

  // 分类筛选
  if (selectedGenre.value) {
    items = items.filter(item => item.genre.includes(selectedGenre.value))
  }

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    items = items.filter(item => 
      item.title.toLowerCase().includes(query) ||
      item.creator.toLowerCase().includes(query) ||
      item.genre.some(g => g.toLowerCase().includes(query))
    )
  }

  return items
})

const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedStatus.value = ''
  selectedGenre.value = ''
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const getTypeIcon = (type: string) => {
  switch (type) {
    case 'BOOK': return Book
    case 'MOVIE': return Film
    case 'TV': return Tv
    case 'ANIME': return Palette
    default: return Film
  }
}

const getTypeName = (type: string) => {
  switch (type) {
    case 'BOOK': return '书籍'
    case 'MOVIE': return '电影'
    case 'TV': return '电视剧'
    case 'ANIME': return '动漫'
    default: return type
  }
}

const getStatusName = (status: string) => {
  switch (status) {
    case 'COMPLETED': return '已完成'
    case 'WATCHING': return '观看中'
    case 'WANT_TO_WATCH': return '想看'
    default: return status
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'COMPLETED': return 'bg-green-100 text-green-800'
    case 'WATCHING': return 'bg-blue-100 text-blue-800'
    case 'WANT_TO_WATCH': return 'bg-gray-100 text-gray-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

onMounted(async () => {
  await mediaStore.initialize()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 页面标题 -->
    <section class="bg-white py-16">
      <div class="container mx-auto px-4">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold text-gray-800 mb-4">媒体库</h1>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto">
            记录我看过的书籍、电影、电视剧和动漫
          </p>
        </div>
      </div>
    </section>

    <div class="container mx-auto px-4 py-8">
      <!-- 加载状态 -->
      <div v-if="mediaStore.loading" class="flex justify-center items-center py-12">
        <Loader2 class="w-8 h-8 animate-spin text-blue-600" />
        <span class="ml-2 text-gray-600">加载中...</span>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="mediaStore.error" class="text-center py-12">
        <div class="text-red-500 mb-4">
          <p class="text-lg font-medium">加载失败</p>
          <p class="text-sm">{{ mediaStore.error }}</p>
        </div>
        <button 
          @click="mediaStore.initialize()"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          重试
        </button>
      </div>

      <div v-else>
        <!-- 筛选和搜索 -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-8">
          <div class="grid grid-cols-1 md:grid-cols-5 gap-4 mb-4">
            <!-- 搜索框 -->
            <div class="md:col-span-2 relative">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索标题、创作者或分类..."
                class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <!-- 类型筛选 -->
            <select
              v-model="selectedType"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">全部类型</option>
              <option value="BOOK">书籍</option>
              <option value="MOVIE">电影</option>
              <option value="TV">电视剧</option>
              <option value="ANIME">动漫</option>
            </select>

            <!-- 状态筛选 -->
            <select
              v-model="selectedStatus"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">全部状态</option>
              <option value="COMPLETED">已完成</option>
              <option value="WATCHING">观看中</option>
              <option value="WANT_TO_WATCH">想看</option>
            </select>

            <!-- 分类筛选 -->
            <select
              v-model="selectedGenre"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">全部分类</option>
              <option v-for="genre in mediaStore.allGenres" :key="genre" :value="genre">
                {{ genre }}
              </option>
            </select>
          </div>

          <!-- 统计和操作 -->
          <div class="flex justify-between items-center">
            <div class="flex items-center space-x-4">
              <p class="text-gray-600">
                找到 <span class="font-semibold text-gray-800">{{ filteredItems.length }}</span> 个项目
              </p>
              <button
                @click="resetFilters"
                class="text-blue-600 hover:text-blue-700 font-medium"
              >
                重置筛选
              </button>
            </div>
            
            <div class="flex items-center space-x-2">
              <button
                @click="viewMode = 'grid'"
                :class="[
                  'p-2 rounded-lg',
                  viewMode === 'grid' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100'
                ]"
              >
                <Grid class="w-4 h-4" />
              </button>
              <button
                @click="viewMode = 'list'"
                :class="[
                  'p-2 rounded-lg',
                  viewMode === 'list' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100'
                ]"
              >
                <List class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        <!-- 网格视图 -->
        <div v-if="viewMode === 'grid'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          <div
            v-for="item in filteredItems"
            :key="item.id"
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden cursor-pointer"
            @click="$router.push(`/media/${item.type.toLowerCase()}/${item.id}`)"
          >
            <!-- 封面 -->
            <div class="aspect-[3/4] bg-gray-200 relative">
              <img
                v-if="item.cover && !failedImages.has(item.id)"
                :src="item.cover"
                :alt="item.title"
                class="w-full h-full object-cover"
                @error="(e) => { 
                  const target = e.target as HTMLImageElement; 
                  if (target) {
                    target.style.display = 'none';
                    failedImages.add(item.id);
                  }
                }"
              />
              <div v-if="!item.cover || failedImages.has(item.id)" class="w-full h-full flex items-center justify-center">
                <component :is="getTypeIcon(item.type)" class="w-16 h-16 text-gray-400" />
              </div>
              
              <!-- 类型标签 -->
              <div class="absolute top-2 left-2">
                <span class="px-2 py-1 bg-black bg-opacity-70 text-white text-xs rounded-full flex items-center">
                  <component :is="getTypeIcon(item.type)" class="w-3 h-3 mr-1" />
                  {{ getTypeName(item.type) }}
                </span>
              </div>

              <!-- 状态标签 -->
              <div class="absolute top-2 right-2">
                <span :class="[
                  'px-2 py-1 text-xs rounded-full',
                  getStatusColor(item.status)
                ]">
                  {{ getStatusName(item.status) }}
                </span>
              </div>

              <!-- 评分 -->
              <div v-if="item.rating" class="absolute bottom-2 left-2">
                <div class="flex items-center bg-black bg-opacity-70 text-white px-2 py-1 rounded-full text-xs">
                  <Star class="w-3 h-3 text-yellow-400 mr-1" />
                  {{ item.rating }}
                </div>
              </div>
            </div>

            <!-- 信息 -->
            <div class="p-4">
              <h3 class="font-semibold text-gray-900 mb-1 line-clamp-2">{{ item.title }}</h3>
              <p class="text-sm text-gray-600 mb-2 line-clamp-1">{{ item.creator }}</p>
              
              <!-- 分类标签 -->
              <div class="flex flex-wrap gap-1 mb-2">
                <span
                  v-for="genre in item.genre.slice(0, 2)"
                  :key="genre"
                  class="px-2 py-1 bg-gray-100 text-gray-700 text-xs rounded"
                >
                  {{ genre }}
                </span>
                <span v-if="item.genre.length > 2" class="text-gray-400 text-xs">
                  +{{ item.genre.length - 2 }}
                </span>
              </div>

              <div class="flex items-center justify-between text-xs text-gray-500">
                <div class="flex items-center">
                  <Calendar class="w-3 h-3 mr-1" />
                  {{ formatDate(item.dateAdded) }}
                </div>
                <div v-if="item.dateWatched" class="flex items-center">
                  <Eye class="w-3 h-3 mr-1" />
                  {{ formatDate(item.dateWatched) }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else class="bg-white rounded-lg shadow-md overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">标题</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">类型</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">创作者</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">评分</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">添加日期</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr
                  v-for="item in filteredItems"
                  :key="item.id"
                  class="hover:bg-gray-50 cursor-pointer"
                  @click="$router.push(`/media/${item.type.toLowerCase()}/${item.id}`)"
                >
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <img
                        v-if="item.cover && !failedImages.has(item.id)"
                        :src="item.cover"
                        :alt="item.title"
                        class="w-12 h-16 object-cover rounded mr-4"
                        @error="(e) => { 
                          const target = e.target as HTMLImageElement; 
                          if (target) {
                            target.style.display = 'none';
                            failedImages.add(item.id);
                          }
                        }"
                      />
                      <div v-if="!item.cover || failedImages.has(item.id)" class="w-12 h-16 bg-gray-200 rounded mr-4 flex items-center justify-center">
                        <component :is="getTypeIcon(item.type)" class="w-6 h-6 text-gray-400" />
                      </div>
                      <div>
                        <div class="text-sm font-medium text-gray-900">{{ item.title }}</div>
                        <div v-if="item.originalTitle" class="text-sm text-gray-500">{{ item.originalTitle }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <component :is="getTypeIcon(item.type)" class="w-4 h-4 text-gray-600 mr-2" />
                      <span class="text-sm text-gray-900">{{ getTypeName(item.type) }}</span>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ item.creator }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'px-2 py-1 text-xs font-semibold rounded-full',
                      getStatusColor(item.status)
                    ]">
                      {{ getStatusName(item.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <div v-if="item.rating" class="flex items-center">
                      <Star class="w-4 h-4 text-yellow-400 mr-1" />
                      {{ item.rating }}/5
                    </div>
                    <span v-else class="text-gray-400">未评分</span>
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
        <div v-if="filteredItems.length === 0 && !mediaStore.loading" class="text-center py-12">
          <div class="text-gray-400 mb-4">
            <Search class="w-16 h-16 mx-auto" />
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">没有找到相关项目</h3>
          <p class="text-gray-500">
            尝试调整搜索条件或筛选器，或者
            <button @click="resetFilters" class="text-blue-600 hover:text-blue-700">重置筛选</button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style> 