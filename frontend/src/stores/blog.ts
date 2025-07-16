import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface BlogPost {
  id: string
  title: string
  content: string
  excerpt: string
  coverImage?: string
  category: string
  tags: string[]
  status: 'draft' | 'published'
  createdAt: string
  updatedAt: string
  publishedAt?: string
  viewCount: number
  author: string
}

export interface BlogComment {
  id: string
  postId: string
  author: string
  email?: string
  content: string
  createdAt: string
  approved: boolean
  parentId?: string
}

export const useBlogStore = defineStore('blog', () => {
  const posts = ref<BlogPost[]>([])
  const comments = ref<BlogComment[]>([])
  const categories = ref<string[]>(['技术', '生活', '观点', '随笔'])
  const currentPage = ref(1)
  const postsPerPage = ref(10)

  // 示例数据
  const samplePosts: BlogPost[] = [
    {
      id: '1',
      title: '欢迎来到我的个人网站',
      content: '这是我的第一篇博客文章...',
      excerpt: '欢迎来到我的个人网站，这里将分享我的思考和创作。',
      category: '生活',
      tags: ['欢迎', '第一篇'],
      status: 'published',
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z',
      publishedAt: '2024-01-01T10:00:00Z',
      viewCount: 42,
      author: '站长'
    },
    {
      id: '2',
      title: '关于这个网站的技术架构',
      content: '这个网站使用了Vue 3、TypeScript和Vite...',
      excerpt: '分享一下这个个人网站的技术选型和架构设计思路。',
      category: '技术',
      tags: ['Vue3', 'TypeScript', 'Vite'],
      status: 'published',
      createdAt: '2024-01-05T14:30:00Z',
      updatedAt: '2024-01-05T14:30:00Z',
      publishedAt: '2024-01-05T14:30:00Z',
      viewCount: 68,
      author: '站长'
    }
  ]

  // 初始化示例数据
  if (posts.value.length === 0) {
    posts.value = samplePosts
  }

  const publishedPosts = computed(() => 
    posts.value
      .filter(post => post.status === 'published')
      .sort((a, b) => new Date(b.publishedAt || b.createdAt).getTime() - new Date(a.publishedAt || a.createdAt).getTime())
  )

  const draftPosts = computed(() => 
    posts.value.filter(post => post.status === 'draft')
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
    const tags = new Set<string>()
    posts.value.forEach(post => {
      post.tags.forEach(tag => tags.add(tag))
    })
    return Array.from(tags).sort()
  })

  const getPostById = (id: string) => {
    return posts.value.find(post => post.id === id)
  }

  const getPostsByCategory = (category: string) => {
    return publishedPosts.value.filter(post => post.category === category)
  }

  const getPostsByTag = (tag: string) => {
    return publishedPosts.value.filter(post => post.tags.includes(tag))
  }

  const searchPosts = (query: string) => {
    const lowerQuery = query.toLowerCase()
    return publishedPosts.value.filter(post => 
      post.title.toLowerCase().includes(lowerQuery) ||
      post.content.toLowerCase().includes(lowerQuery) ||
      post.excerpt.toLowerCase().includes(lowerQuery) ||
      post.tags.some(tag => tag.toLowerCase().includes(lowerQuery))
    )
  }

  const addPost = (post: Omit<BlogPost, 'id' | 'createdAt' | 'updatedAt' | 'viewCount'>) => {
    const now = new Date().toISOString()
    const newPost: BlogPost = {
      ...post,
      id: Date.now().toString(),
      createdAt: now,
      updatedAt: now,
      viewCount: 0
    }
    posts.value.push(newPost)
    return newPost
  }

  const updatePost = (id: string, updates: Partial<BlogPost>) => {
    const index = posts.value.findIndex(post => post.id === id)
    if (index !== -1) {
      posts.value[index] = {
        ...posts.value[index],
        ...updates,
        updatedAt: new Date().toISOString()
      }
      return posts.value[index]
    }
    return null
  }

  const deletePost = (id: string) => {
    const index = posts.value.findIndex(post => post.id === id)
    if (index !== -1) {
      posts.value.splice(index, 1)
      return true
    }
    return false
  }

  const publishPost = (id: string) => {
    const post = getPostById(id)
    if (post) {
      post.status = 'published'
      post.publishedAt = new Date().toISOString()
      post.updatedAt = new Date().toISOString()
      return true
    }
    return false
  }

  const incrementViewCount = (id: string) => {
    const post = getPostById(id)
    if (post) {
      post.viewCount++
    }
  }

  const setPage = (page: number) => {
    currentPage.value = page
  }

  const addComment = (comment: Omit<BlogComment, 'id' | 'createdAt' | 'approved'>) => {
    const newComment: BlogComment = {
      ...comment,
      id: Date.now().toString(),
      createdAt: new Date().toISOString(),
      approved: false // 需要审核
    }
    comments.value.push(newComment)
    return newComment
  }

  const getCommentsForPost = (postId: string) => {
    return comments.value
      .filter(comment => comment.postId === postId && comment.approved)
      .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
  }

  const approveComment = (id: string) => {
    const comment = comments.value.find(c => c.id === id)
    if (comment) {
      comment.approved = true
      return true
    }
    return false
  }

  return {
    posts,
    comments,
    categories,
    currentPage,
    postsPerPage,
    publishedPosts,
    draftPosts,
    totalPages,
    paginatedPosts,
    allTags,
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
    addComment,
    getCommentsForPost,
    approveComment
  }
}) 