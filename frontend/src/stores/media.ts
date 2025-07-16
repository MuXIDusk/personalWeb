import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export type MediaType = 'book' | 'movie' | 'tv' | 'anime'
export type MediaStatus = 'completed' | 'watching' | 'want-to-watch'

export interface MediaItem {
  id: string
  title: string
  originalTitle?: string
  cover: string
  type: MediaType
  creator: string // 作者/导演/制作方
  genre: string[]
  rating?: number
  status: MediaStatus
  dateAdded: string
  dateWatched?: string
  review?: string
  tags: string[]
  externalLinks?: {
    douban?: string
    imdb?: string
    mal?: string
  }
}

export const useMediaStore = defineStore('media', () => {
  const items = ref<MediaItem[]>([])
  const currentFilter = ref<{
    type?: MediaType
    status?: MediaStatus
    genre?: string
    query?: string
  }>({})
  const currentSort = ref<'dateAdded' | 'dateWatched' | 'rating' | 'title'>('dateAdded')
  const sortOrder = ref<'asc' | 'desc'>('desc')

  // 示例数据
  const sampleData: MediaItem[] = [
    {
      id: '1',
      title: '示例书籍',
      cover: 'https://via.placeholder.com/300x400',
      type: 'book',
      creator: '作者姓名',
      genre: ['文学', '小说'],
      rating: 4.5,
      status: 'completed',
      dateAdded: '2024-01-01',
      dateWatched: '2024-01-15',
      review: '这是一本很棒的书...',
      tags: ['推荐', '经典']
    },
    {
      id: '2',
      title: '示例电影',
      cover: 'https://via.placeholder.com/300x400',
      type: 'movie',
      creator: '导演姓名',
      genre: ['剧情', '科幻'],
      rating: 4.0,
      status: 'completed',
      dateAdded: '2024-01-02',
      dateWatched: '2024-01-20',
      review: '视觉效果惊人...',
      tags: ['科幻', 'IMAX']
    }
  ]

  // 初始化示例数据
  if (items.value.length === 0) {
    items.value = sampleData
  }

  const filteredItems = computed(() => {
    let result = items.value

    if (currentFilter.value.type) {
      result = result.filter(item => item.type === currentFilter.value.type)
    }

    if (currentFilter.value.status) {
      result = result.filter(item => item.status === currentFilter.value.status)
    }

    if (currentFilter.value.genre) {
      result = result.filter(item => item.genre.includes(currentFilter.value.genre!))
    }

    if (currentFilter.value.query) {
      const query = currentFilter.value.query.toLowerCase()
      result = result.filter(item => 
        item.title.toLowerCase().includes(query) ||
        item.creator.toLowerCase().includes(query) ||
        item.genre.some(g => g.toLowerCase().includes(query))
      )
    }

    // 排序
    result.sort((a, b) => {
      const getValue = (item: MediaItem) => {
        switch (currentSort.value) {
          case 'dateAdded':
            return new Date(item.dateAdded).getTime()
          case 'dateWatched':
            return item.dateWatched ? new Date(item.dateWatched).getTime() : 0
          case 'rating':
            return item.rating || 0
          case 'title':
            return item.title
          default:
            return item.dateAdded
        }
      }

      const aValue = getValue(a)
      const bValue = getValue(b)

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return sortOrder.value === 'asc' 
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue)
      }

      return sortOrder.value === 'asc' 
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    })

    return result
  })

  const mediaTypes = computed(() => {
    const types = new Set(items.value.map(item => item.type))
    return Array.from(types)
  })

  const allGenres = computed(() => {
    const genres = new Set<string>()
    items.value.forEach(item => {
      item.genre.forEach(g => genres.add(g))
    })
    return Array.from(genres).sort()
  })

  const addMediaItem = (item: Omit<MediaItem, 'id' | 'dateAdded'>) => {
    const newItem: MediaItem = {
      ...item,
      id: Date.now().toString(),
      dateAdded: new Date().toISOString().split('T')[0]
    }
    items.value.push(newItem)
  }

  const updateMediaItem = (id: string, updates: Partial<MediaItem>) => {
    const index = items.value.findIndex(item => item.id === id)
    if (index !== -1) {
      items.value[index] = { ...items.value[index], ...updates }
    }
  }

  const deleteMediaItem = (id: string) => {
    const index = items.value.findIndex(item => item.id === id)
    if (index !== -1) {
      items.value.splice(index, 1)
    }
  }

  const getMediaItem = (id: string) => {
    return items.value.find(item => item.id === id)
  }

  const setFilter = (filter: typeof currentFilter.value) => {
    currentFilter.value = filter
  }

  const setSort = (sort: typeof currentSort.value, order: typeof sortOrder.value) => {
    currentSort.value = sort
    sortOrder.value = order
  }

  return {
    items,
    filteredItems,
    currentFilter,
    currentSort,
    sortOrder,
    mediaTypes,
    allGenres,
    addMediaItem,
    updateMediaItem,
    deleteMediaItem,
    getMediaItem,
    setFilter,
    setSort
  }
}) 