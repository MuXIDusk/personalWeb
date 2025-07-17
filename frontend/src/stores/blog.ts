import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { blogApi, type BlogPost as ApiBlogPost, type BlogPostRequest, type Comment } from '@/services/api'

// 前端BlogPost接口（與後端保持一致）
export interface BlogPost {
  id: number | string  // 保持向後兼容
  title: string
  content: string
  excerpt: string
  coverImage?: string
  category: string
  tags: string[]
  status: 'draft' | 'published' | 'DRAFT' | 'PUBLISHED'
  createdAt: string
  updatedAt: string
  publishedAt?: string
  viewCount: number
  likeCount: number
  author: string
}

export interface BlogComment {
  id: number | string  // 保持向後兼容
  postId: number | string
  author: string
  email?: string
  content: string
  createdAt: string
  approved: boolean
  parentId?: number | string
}

export const useBlogStore = defineStore('blog', () => {
  const posts = ref<BlogPost[]>([])
  const comments = ref<BlogComment[]>([])
  const categories = ref<string[]>([])
  const tags = ref<string[]>([])
  const currentPage = ref(1)
  const postsPerPage = ref(10)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 計算屬性
  const publishedPosts = computed(() => 
    posts.value
      .filter(post => post.status === 'published' || post.status === 'PUBLISHED')
      .sort((a, b) => new Date(b.publishedAt || b.createdAt).getTime() - new Date(a.publishedAt || a.createdAt).getTime())
  )

  const draftPosts = computed(() => 
    posts.value.filter(post => post.status === 'draft' || post.status === 'DRAFT')
  )

  const totalPages = computed(() => 
    Math.ceil(publishedPosts.value.length / postsPerPage.value)
  )

  const paginatedPosts = computed(() => {
    const start = (currentPage.value - 1) * postsPerPage.value
    const end = start + postsPerPage.value
    return publishedPosts.value.slice(start, end)
  })

  const allTags = computed(() => {
    return tags.value.sort()
  })

  // API方法
  const fetchPosts = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.getPosts()
      posts.value = response.data.data.map(convertApiPostToFrontend)
    } catch (err) {
      error.value = '獲取文章列表失敗'
      console.error('Error fetching posts:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchAllPosts = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.getAllPosts()
      posts.value = response.data.data.map(convertApiPostToFrontend)
    } catch (err) {
      error.value = '獲取所有文章失敗'
      console.error('Error fetching all posts:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchCategories = async () => {
    try {
      const response = await blogApi.getCategories()
      categories.value = response.data.data
    } catch (err) {
      console.error('Error fetching categories:', err)
    }
  }

  const fetchTags = async () => {
    try {
      const response = await blogApi.getTags()
      tags.value = response.data.data
    } catch (err) {
      console.error('Error fetching tags:', err)
    }
  }

  const getPostById = async (id: number): Promise<BlogPost | null> => {
    try {
      const response = await blogApi.getPost(id)
      return convertApiPostToFrontend(response.data.data)
    } catch (err) {
      console.error('Error fetching post:', err)
      return null
    }
  }

  const getPostsByCategory = async (category: string) => {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.getPostsByCategory(category)
      return response.data.data.map(convertApiPostToFrontend)
    } catch (err) {
      error.value = '獲取分類文章失敗'
      console.error('Error fetching posts by category:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  const getPostsByTag = async (tag: string) => {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.getPostsByTag(tag)
      return response.data.data.map(convertApiPostToFrontend)
    } catch (err) {
      error.value = '獲取標籤文章失敗'
      console.error('Error fetching posts by tag:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  const searchPosts = async (query: string) => {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.searchPosts(query)
      return response.data.data.map(convertApiPostToFrontend)
    } catch (err) {
      error.value = '搜索文章失敗'
      console.error('Error searching posts:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  const addPost = async (post: Omit<BlogPost, 'id' | 'createdAt' | 'updatedAt' | 'viewCount'>) => {
    try {
      const request: BlogPostRequest = {
        title: post.title,
        content: post.content,
        excerpt: post.excerpt,
        coverImage: post.coverImage,
        category: post.category,
        tags: post.tags,
        status: post.status === 'published' ? 'PUBLISHED' : 'DRAFT',
        author: post.author
      }
      
      const response = await blogApi.createPost(request)
      const newPost = convertApiPostToFrontend(response.data.data)
      posts.value.push(newPost)
      return newPost
    } catch (err) {
      error.value = '創建文章失敗'
      console.error('Error creating post:', err)
      throw err
    }
  }

  const updatePost = async (id: number, updates: Partial<BlogPost>) => {
    try {
      const existingPost = posts.value.find(p => Number(p.id) === id)
      if (!existingPost) throw new Error('文章不存在')

      const request: BlogPostRequest = {
        title: updates.title || existingPost.title,
        content: updates.content || existingPost.content,
        excerpt: updates.excerpt || existingPost.excerpt,
        coverImage: updates.coverImage || existingPost.coverImage,
        category: updates.category || existingPost.category,
        tags: updates.tags || existingPost.tags,
        status: (updates.status === 'published' || updates.status === 'PUBLISHED') ? 'PUBLISHED' : 'DRAFT',
        author: updates.author || existingPost.author
      }

      const response = await blogApi.updatePost(id, request)
      const updatedPost = convertApiPostToFrontend(response.data.data)
      
      const index = posts.value.findIndex(p => Number(p.id) === id)
      if (index !== -1) {
        posts.value[index] = updatedPost
      }
      return updatedPost
    } catch (err) {
      error.value = '更新文章失敗'
      console.error('Error updating post:', err)
      throw err
    }
  }

  const deletePost = async (id: number) => {
    try {
      await blogApi.deletePost(id)
      const index = posts.value.findIndex(p => Number(p.id) === id)
      if (index !== -1) {
        posts.value.splice(index, 1)
      }
      return true
    } catch (err) {
      error.value = '刪除文章失敗'
      console.error('Error deleting post:', err)
      throw err
    }
  }

  const publishPost = async (id: number) => {
    const post = posts.value.find(p => Number(p.id) === id)
    if (post) {
      return await updatePost(id, { status: 'PUBLISHED', publishedAt: new Date().toISOString() })
    }
    return null
  }

  const incrementViewCount = (id: number | string) => {
    const post = posts.value.find(p => p.id.toString() === id.toString())
    if (post) {
      post.viewCount++
    }
  }

  const setPage = (page: number) => {
    currentPage.value = page
  }

    const likePost = async (id: number) => {
    try {
      // 檢查這次會話是否已經點讚過
      const likedPosts = JSON.parse(sessionStorage.getItem('likedPosts') || '[]')
      if (likedPosts.includes(id)) {
        throw new Error('您已經點讚過這篇文章了')
      }
      
      const response = await blogApi.likePost(id)
      const newLikeCount = response.data.data
      
      // 更新本地狀態
      const post = posts.value.find(p => Number(p.id) === id)
      if (post) {
        post.likeCount = newLikeCount
      }
      
      // 記錄這次會話的點讚狀態
      likedPosts.push(id)
      sessionStorage.setItem('likedPosts', JSON.stringify(likedPosts))
      
      return newLikeCount
    } catch (err: any) {
      error.value = err.message || '點讚失敗'
      console.error('點讚失敗:', err)
      throw err
    }
  }

  const isPostLikedInSession = (id: number) => {
    const likedPosts = JSON.parse(sessionStorage.getItem('likedPosts') || '[]')
    return likedPosts.includes(id)
  }

  const toggleLike = async (id: number) => {
    try {
      const currentlyLiked = isPostLikedInSession(id)
      
      const response = await blogApi.toggleLike(id, currentlyLiked)
      const newLikeCount = response.data.data
      
      // 更新本地狀態
      const post = posts.value.find(p => Number(p.id) === id)
      if (post) {
        post.likeCount = newLikeCount
      }
      
      // 更新 sessionStorage 中的點讚狀態
      const likedPosts = JSON.parse(sessionStorage.getItem('likedPosts') || '[]')
      if (currentlyLiked) {
        // 取消點讚，從列表中移除
        const index = likedPosts.indexOf(id)
        if (index > -1) {
          likedPosts.splice(index, 1)
        }
      } else {
        // 添加點讚，加入列表
        likedPosts.push(id)
      }
      sessionStorage.setItem('likedPosts', JSON.stringify(likedPosts))
      
      return { newLikeCount, isLiked: !currentlyLiked }
    } catch (err: any) {
      error.value = err.message || '操作失敗'
      console.error('點讚操作失敗:', err)
      throw err
    }
  }

  // 工具函數：將API響應轉換為前端格式
  const convertApiPostToFrontend = (apiPost: ApiBlogPost): BlogPost => ({
    id: apiPost.id,
    title: apiPost.title,
    content: apiPost.content,
    excerpt: apiPost.excerpt,
    coverImage: apiPost.coverImage,
    category: apiPost.category,
    tags: apiPost.tags,
    status: apiPost.status.toLowerCase() as 'draft' | 'published',
    createdAt: apiPost.createdAt,
    updatedAt: apiPost.updatedAt,
    publishedAt: apiPost.publishedAt,
    viewCount: apiPost.viewCount,
    likeCount: apiPost.likeCount || 0,
    author: apiPost.author
  })

  // 初始化數據
  const initialize = async () => {
    await Promise.all([
      fetchPosts(),
      fetchCategories(),
      fetchTags()
    ])
  }

  return {
    posts,
    comments,
    categories,
    tags,
    currentPage,
    postsPerPage,
    loading,
    error,
    publishedPosts,
    draftPosts,
    totalPages,
    paginatedPosts,
    allTags,
    fetchPosts,
    fetchAllPosts,
    fetchCategories,
    fetchTags,
    getPostById,
    getPostsByCategory,
    getPostsByTag,
    searchPosts,
    addPost,
    updatePost,
    deletePost,
    publishPost,
    incrementViewCount,
    setPage,
    likePost,
    toggleLike,
    isPostLikedInSession,
    initialize
  }
}) 