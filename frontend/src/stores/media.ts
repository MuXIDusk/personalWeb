import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { mediaApi, type MediaItem as ApiMediaItem, type MediaItemRequest } from '@/services/api'

export type MediaType = 'BOOK' | 'MOVIE' | 'TV' | 'ANIME'
export type MediaStatus = 'COMPLETED' | 'WATCHING' | 'WANT_TO_WATCH'

export interface MediaItem {
  id: number
  title: string
  originalTitle?: string
  cover: string
  type: MediaType
  creator: string
  genre: string[]
  rating?: number
  status: MediaStatus
  dateAdded: string
  dateWatched?: string
  review?: string
  tags: string[]
  doubanUrl?: string
  imdbUrl?: string
  malUrl?: string
  createdAt: string
  updatedAt: string
}

export const useMediaStore = defineStore('media', () => {
  const items = ref<MediaItem[]>([])
  const genres = ref<string[]>([])
  const tags = ref<string[]>([])
  const types = ref<string[]>([])
  const statuses = ref<string[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const currentFilter = ref<{
    type?: MediaType
    status?: MediaStatus
    genre?: string
    query?: string
  }>({})
  
  const currentSort = ref<'dateAdded' | 'dateWatched' | 'rating' | 'title'>('dateAdded')
  const sortOrder = ref<'asc' | 'desc'>('desc')

  // 計算屬性
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
    return types.value.map(type => type.toLowerCase() as MediaType)
  })

  const allGenres = computed(() => {
    return genres.value.sort()
  })

  const allTags = computed(() => {
    return tags.value.sort()
  })

  // API方法
  const fetchMediaItems = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await mediaApi.getMediaItems()
      items.value = response.data.data.map(convertApiItemToFrontend)
    } catch (err) {
      error.value = '獲取媒體項目失敗'
      console.error('Error fetching media items:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchGenres = async () => {
    try {
      const response = await mediaApi.getGenres()
      genres.value = response.data.data
    } catch (err) {
      console.error('Error fetching genres:', err)
    }
  }

  const fetchTags = async () => {
    try {
      const response = await mediaApi.getTags()
      tags.value = response.data.data
    } catch (err) {
      console.error('Error fetching tags:', err)
    }
  }

  const fetchTypes = async () => {
    try {
      const response = await mediaApi.getTypes()
      types.value = response.data.data
    } catch (err) {
      console.error('Error fetching types:', err)
    }
  }

  const fetchStatuses = async () => {
    try {
      const response = await mediaApi.getStatuses()
      statuses.value = response.data.data
    } catch (err) {
      console.error('Error fetching statuses:', err)
    }
  }

  const getMediaItem = async (id: number): Promise<MediaItem | null> => {
    try {
      const response = await mediaApi.getMediaItem(id)
      return convertApiItemToFrontend(response.data.data)
    } catch (err) {
      console.error('Error fetching media item:', err)
      return null
    }
  }

  const addMediaItem = async (item: Omit<MediaItem, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      const request: MediaItemRequest = {
        title: item.title,
        originalTitle: item.originalTitle,
        cover: item.cover,
        type: item.type,
        creator: item.creator,
        genre: item.genre,
        rating: item.rating,
        status: item.status,
        dateAdded: item.dateAdded,
        dateWatched: item.dateWatched,
        review: item.review,
        tags: item.tags,
        doubanUrl: item.doubanUrl,
        imdbUrl: item.imdbUrl,
        malUrl: item.malUrl
      }
      
      const response = await mediaApi.createMediaItem(request)
      const newItem = convertApiItemToFrontend(response.data.data)
      items.value.push(newItem)
      return newItem
    } catch (err) {
      error.value = '創建媒體項目失敗'
      console.error('Error creating media item:', err)
      throw err
    }
  }

  const updateMediaItem = async (id: number, updates: Partial<MediaItem>) => {
    try {
      const existingItem = items.value.find(item => item.id === id)
      if (!existingItem) throw new Error('媒體項目不存在')

      const request: MediaItemRequest = {
        title: updates.title || existingItem.title,
        originalTitle: updates.originalTitle || existingItem.originalTitle,
        cover: updates.cover || existingItem.cover,
        type: updates.type || existingItem.type,
        creator: updates.creator || existingItem.creator,
        genre: updates.genre || existingItem.genre,
        rating: updates.rating !== undefined ? updates.rating : existingItem.rating,
        status: updates.status || existingItem.status,
        dateAdded: updates.dateAdded || existingItem.dateAdded,
        dateWatched: updates.dateWatched || existingItem.dateWatched,
        review: updates.review !== undefined ? updates.review : existingItem.review,
        tags: updates.tags || existingItem.tags,
        doubanUrl: updates.doubanUrl !== undefined ? updates.doubanUrl : existingItem.doubanUrl,
        imdbUrl: updates.imdbUrl !== undefined ? updates.imdbUrl : existingItem.imdbUrl,
        malUrl: updates.malUrl !== undefined ? updates.malUrl : existingItem.malUrl
      }

      const response = await mediaApi.updateMediaItem(id, request)
      const updatedItem = convertApiItemToFrontend(response.data.data)
      
      const index = items.value.findIndex(item => item.id === id)
      if (index !== -1) {
        items.value[index] = updatedItem
      }
      return updatedItem
    } catch (err) {
      error.value = '更新媒體項目失敗'
      console.error('Error updating media item:', err)
      throw err
    }
  }

  const deleteMediaItem = async (id: number) => {
    try {
      await mediaApi.deleteMediaItem(id)
      const index = items.value.findIndex(item => item.id === id)
      if (index !== -1) {
        items.value.splice(index, 1)
      }
      return true
    } catch (err) {
      error.value = '刪除媒體項目失敗'
      console.error('Error deleting media item:', err)
      throw err
    }
  }

  const searchMediaItems = async (query: string) => {
    try {
      loading.value = true
      error.value = null
      const response = await mediaApi.searchMediaItems(query)
      return response.data.data.map(convertApiItemToFrontend)
    } catch (err) {
      error.value = '搜索媒體項目失敗'
      console.error('Error searching media items:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  const getMediaItemsByType = async (type: string) => {
    try {
      const response = await mediaApi.getMediaItemsByType(type)
      return response.data.data.map(convertApiItemToFrontend)
    } catch (err) {
      console.error('Error fetching media items by type:', err)
      return []
    }
  }

  const getMediaItemsByStatus = async (status: string) => {
    try {
      const response = await mediaApi.getMediaItemsByStatus(status)
      return response.data.data.map(convertApiItemToFrontend)
    } catch (err) {
      console.error('Error fetching media items by status:', err)
      return []
    }
  }

  // 工具函數：將API響應轉換為前端格式
  const convertApiItemToFrontend = (apiItem: ApiMediaItem): MediaItem => ({
    id: apiItem.id,
    title: apiItem.title,
    originalTitle: apiItem.originalTitle,
    cover: apiItem.cover,
    type: apiItem.type as MediaType,
    creator: apiItem.creator,
    genre: apiItem.genre,
    rating: apiItem.rating,
    status: apiItem.status as MediaStatus,
    dateAdded: apiItem.dateAdded,
    dateWatched: apiItem.dateWatched,
    review: apiItem.review,
    tags: apiItem.tags,
    doubanUrl: apiItem.doubanUrl,
    imdbUrl: apiItem.imdbUrl,
    malUrl: apiItem.malUrl,
    createdAt: apiItem.createdAt,
    updatedAt: apiItem.updatedAt
  })

  const setFilter = (filter: typeof currentFilter.value) => {
    currentFilter.value = filter
  }

  const setSort = (sort: typeof currentSort.value, order: typeof sortOrder.value) => {
    currentSort.value = sort
    sortOrder.value = order
  }

  // 初始化數據
  const initialize = async () => {
    await Promise.all([
      fetchMediaItems(),
      fetchGenres(),
      fetchTags(),
      fetchTypes(),
      fetchStatuses()
    ])
  }

  return {
    items,
    genres,
    tags,
    types,
    statuses,
    loading,
    error,
    filteredItems,
    currentFilter,
    currentSort,
    sortOrder,
    mediaTypes,
    allGenres,
    allTags,
    fetchMediaItems,
    getMediaItem,
    addMediaItem,
    updateMediaItem,
    deleteMediaItem,
    searchMediaItems,
    getMediaItemsByType,
    getMediaItemsByStatus,
    setFilter,
    setSort,
    initialize
  }
}) 