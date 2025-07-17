<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useBlogStore } from '../stores/blog'
import { useMediaStore } from '../stores/media'
import { useUserStore } from '../stores/user'
import { useAuthStore } from '../stores/auth'
import { usePoliticalStore } from '../stores/political'
import { useCommentStore } from '../stores/comment'
import { useRouter } from 'vue-router'
import { politicalAccessApi, type PoliticalViewAccess, type PoliticalViewAccessRequest } from '../services/api'
import { 
  Settings, 
  FileText, 
  Image, 
  User, 
  BarChart3, 
  LogOut,
  Plus,
  Edit,
  Trash2,
  Eye,
  Calendar,
  Star,
  Tag,
  Book,
  Film,
  Tv,
  Palette,
  MessageSquare,
  Heart,
  Shield,
  Users,
  CheckCircle,
  Search,
  Lock,
  UserCheck
} from 'lucide-vue-next'

const router = useRouter()
const blogStore = useBlogStore()
const mediaStore = useMediaStore()
const userStore = useUserStore()
const authStore = useAuthStore()
const politicalStore = usePoliticalStore()
const commentStore = useCommentStore()

const activeTab = ref('dashboard')
const loading = ref(true)

// 表单数据
const newBlogPost = ref({
  title: '',
  content: '',
  excerpt: '',
  category: '',
  tags: [] as string[],
  status: 'DRAFT' as const,
  author: '站长',
  likeCount: 0
})

const newMediaItem = ref({
  title: '',
  originalTitle: '',
  cover: '',
  type: 'BOOK' as const,
  creator: '',
  genre: [] as string[],
  rating: 0,
  status: 'WANT_TO_WATCH' as const,
  dateAdded: new Date().toISOString().split('T')[0],
  review: '',
  tags: [] as string[]
})

const newPoliticalView = ref({
  type: 'CORE_VALUE' as const,
  title: '',
  description: '',
  icon: '',
  displayOrder: 0,
  isActive: true,
  position: '',
  details: '',
  testDate: ''
})

const newPoliticalAccess = ref<PoliticalViewAccessRequest>({
  name: '',
  description: '',
  isActive: true
})

// 政治观点访问权限数据
const politicalAccessList = ref<PoliticalViewAccess[]>([])

const userProfile = ref({
  name: '',
  avatar: '',
  title: '',
  bio: '',
  email: '',
  location: '',
  skills: [] as string[],
  socialLinks: [] as Array<{id: number, platform: string, url: string, icon: string}>
})

// 统计数据
const stats = ref({
  totalPosts: 0,
  publishedPosts: 0,
  draftPosts: 0,
  totalViews: 0,
  totalMedia: 0,
  completedMedia: 0,
  watchingMedia: 0,
  wantToWatchMedia: 0
})

// 对话框状态
const showBlogForm = ref(false)
const showMediaForm = ref(false)
const showProfileForm = ref(false)
const showPoliticalAccessForm = ref(false)
const showPoliticalForm = ref(false)
const editingBlogId = ref<number | null>(null)
const editingMediaId = ref<number | null>(null)
const editingAccessId = ref<number | null>(null)
const editingPoliticalId = ref<number | null>(null)

// 评论管理状态
const allComments = ref<any[]>([])
const pendingComments = ref<any[]>([])
const commentStatistics = ref<any>(null)
const commentSearchQuery = ref('')
const commentFilterStatus = ref('all')
const selectedComments = ref<Set<number>>(new Set())
const showBatchActions = ref(false)
const batchActionType = ref('')

// 图片加载失败状态
const failedImages = ref<Set<number>>(new Set())

// 初始化数据
onMounted(async () => {
  // 检查是否已登录管理员
  if (!authStore.isAdminAuthenticated) {
    router.push('/admin/login')
    return
  }

  try {
    await Promise.all([
      blogStore.fetchAllPosts(), // 管理后台需要所有文章
      blogStore.fetchCategories(),
      blogStore.fetchTags(),
      mediaStore.initialize(),
      userStore.initialize(),
      politicalStore.fetchPoliticalViews(),
      initializeCommentData(), // 初始化评论数据
      fetchPoliticalAccess() // 初始化政治观点访问权限数据
    ])

    // 计算统计数据
    calculateStats()
    
    // 初始化表单数据
    if (userStore.profile) {
      userProfile.value = {
        name: userStore.profile.name,
        avatar: userStore.profile.avatar,
        title: userStore.profile.title,
        bio: userStore.profile.bio || '',
        email: userStore.profile.email || '',
        location: userStore.profile.location || '',
        skills: [...userStore.profile.skills],
        socialLinks: userStore.profile.socialLinks.map(link => ({
          id: link.id,
          platform: link.platform,
          url: link.url,
          icon: link.icon
        }))
      }
    }
  } catch (error) {
    console.error('初始化失败:', error)
  } finally {
    loading.value = false
  }
})

const calculateStats = () => {
  stats.value = {
    totalPosts: blogStore.posts.length,
    publishedPosts: blogStore.publishedPosts.length,
    draftPosts: blogStore.draftPosts.length,
    totalViews: blogStore.posts.reduce((sum, post) => sum + post.viewCount, 0),
    totalMedia: mediaStore.items.length,
    completedMedia: mediaStore.items.filter(item => item.status === 'COMPLETED').length,
    watchingMedia: mediaStore.items.filter(item => item.status === 'WATCHING').length,
    wantToWatchMedia: mediaStore.items.filter(item => item.status === 'WANT_TO_WATCH').length
  }
}

// 博客管理
const openBlogForm = (post?: any) => {
  // 清除之前的錯誤
  blogStore.error = null
  
  if (post) {
    editingBlogId.value = post.id
    newBlogPost.value = {
      title: post.title,
      content: post.content,
      excerpt: post.excerpt,
      category: post.category,
      tags: [...post.tags],
      status: post.status,
      author: post.author,
      likeCount: post.likeCount || 0
    }
  } else {
    editingBlogId.value = null
    newBlogPost.value = {
      title: '',
      content: '',
      excerpt: '',
      category: '',
      tags: [],
      status: 'DRAFT',
      author: '站長',
      likeCount: 0
    }
  }
  showBlogForm.value = true
}

const saveBlogPost = async () => {
  try {
    console.log('开始保存博客文章:', JSON.stringify(newBlogPost.value, null, 2))
    
    // 验证必填字段
    if (!newBlogPost.value.title.trim()) {
      alert('请输入文章标题')
      return
    }
    if (!newBlogPost.value.content.trim()) {
      alert('请输入文章内容')
      return
    }
    if (!newBlogPost.value.category.trim()) {
      alert('请输入文章分类')
      return
    }
    
    const isEditing = !!editingBlogId.value
    let result
    
    if (isEditing && editingBlogId.value !== null) {
      console.log('更新现有文章, ID:', editingBlogId.value)
      result = await blogStore.updatePost(editingBlogId.value, newBlogPost.value)
      console.log('文章更新结果:', result)
    } else {
      console.log('创建新文章')
      result = await blogStore.addPost(newBlogPost.value)
      console.log('新文章创建结果:', result)
    }
    
    // 重新获取文章列表以确保数据同步
    console.log('重新获取文章列表')
    await blogStore.fetchAllPosts()
    console.log('当前文章总数:', blogStore.posts.length)
    console.log('更新后的文章列表:', blogStore.posts.map(p => ({ id: p.id, title: p.title, status: p.status })))
    
    calculateStats()
    
    // 关闭表单
    showBlogForm.value = false
    
    // 清空表单
    newBlogPost.value = {
      title: '',
      content: '',
      excerpt: '',
      category: '',
      tags: [],
      status: 'DRAFT',
      author: '站长',
      likeCount: 0
    }
    editingBlogId.value = null
    
    console.log('博客保存完成')
    
    // 显示成功消息
    const action = isEditing ? '更新' : '创建'
    alert(`文章${action}成功！文章ID: ${result?.id || 'N/A'}`)
  } catch (error) {
    console.error('保存博客失败:', error)
    
    // 提供更详细的错误信息
    let errorMessage = '未知错误'
    if (error && typeof error === 'object') {
      if ('response' in error && error.response) {
        const response = error.response as any
        errorMessage = response.data?.message || response.statusText || '服务器错误'
      } else if ('message' in error) {
        errorMessage = (error as any).message
      }
    }
    
    alert(`保存失败：${errorMessage}`)
  }
}

const deleteBlogPost = async (id: number) => {
  if (confirm('确定要删除这篇文章吗？')) {
    try {
      await blogStore.deletePost(id)
      calculateStats()
    } catch (error) {
      console.error('删除博客失败:', error)
    }
  }
}

// 媒体库管理
const openMediaForm = (item?: any) => {
  if (item) {
    editingMediaId.value = item.id
    newMediaItem.value = {
      title: item.title,
      originalTitle: item.originalTitle || '',
      cover: item.cover,
      type: item.type,
      creator: item.creator,
      genre: [...item.genre],
      rating: item.rating || 0,
      status: item.status,
      dateAdded: item.dateAdded,
      review: item.review || '',
      tags: [...item.tags]
    }
  } else {
    editingMediaId.value = null
    newMediaItem.value = {
      title: '',
      originalTitle: '',
      cover: '',
      type: 'BOOK',
      creator: '',
      genre: [],
      rating: 0,
      status: 'WANT_TO_WATCH',
      dateAdded: new Date().toISOString().split('T')[0],
      review: '',
      tags: []
    }
  }
  showMediaForm.value = true
}

const saveMediaItem = async () => {
  try {
    if (editingMediaId.value) {
      await mediaStore.updateMediaItem(editingMediaId.value, newMediaItem.value)
    } else {
      await mediaStore.addMediaItem(newMediaItem.value)
    }
    showMediaForm.value = false
    calculateStats()
  } catch (error) {
    console.error('保存媒体项目失败:', error)
  }
}

const deleteMediaItem = async (id: number) => {
  if (confirm('确定要删除这个媒体项目吗？')) {
    try {
      await mediaStore.deleteMediaItem(id)
      calculateStats()
    } catch (error) {
      console.error('删除媒体项目失败:', error)
    }
  }
}

// 个人资料管理
const openProfileForm = () => {
  showProfileForm.value = true
}

const saveProfile = async () => {
  try {
    await userStore.updateProfile(userProfile.value)
    showProfileForm.value = false
  } catch (error) {
    console.error('保存个人资料失败:', error)
  }
}

// 添加标签/分类
const addTag = (target: 'blog' | 'media', value: string) => {
  if (!value.trim()) return
  
  if (target === 'blog') {
    if (!newBlogPost.value.tags.includes(value.trim())) {
      newBlogPost.value.tags.push(value.trim())
    }
  } else {
    if (!newMediaItem.value.tags.includes(value.trim())) {
      newMediaItem.value.tags.push(value.trim())
    }
  }
}

const removeTag = (target: 'blog' | 'media', index: number) => {
  if (target === 'blog') {
    newBlogPost.value.tags.splice(index, 1)
  } else {
    newMediaItem.value.tags.splice(index, 1)
  }
}

const addGenre = (value: string) => {
  if (!value.trim()) return
  if (!newMediaItem.value.genre.includes(value.trim())) {
    newMediaItem.value.genre.push(value.trim())
  }
}

const removeGenre = (index: number) => {
  newMediaItem.value.genre.splice(index, 1)
}

const addSkill = (value: string) => {
  if (!value.trim()) return
  if (!userProfile.value.skills.includes(value.trim())) {
    userProfile.value.skills.push(value.trim())
  }
}

const removeSkill = (index: number) => {
  userProfile.value.skills.splice(index, 1)
}

const addSocialLink = () => {
  const newId = Math.max(0, ...userProfile.value.socialLinks.map(link => link.id)) + 1
  userProfile.value.socialLinks.push({
    id: newId,
    platform: '',
    url: '',
    icon: ''
  })
}

const removeSocialLink = (index: number) => {
  userProfile.value.socialLinks.splice(index, 1)
}

// 登出
const logout = () => {
  authStore.logout()
  router.push('/')
}

const getMediaTypeIcon = (type: string) => {
  switch (type) {
    case 'BOOK': return Book
    case 'MOVIE': return Film
    case 'TV': return Tv
    case 'ANIME': return Palette
    default: return FileText
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// === 评论管理功能 ===

// 初始化评论数据
const initializeCommentData = async () => {
  try {
    // 并行获取评论数据
    const [allCommentsResult, pendingCommentsResult, statisticsResult] = await Promise.all([
      commentStore.getAllCommentsForAdmin(),
      commentStore.getPendingComments(),
      commentStore.getStatistics()
    ])
    
    allComments.value = allCommentsResult
    pendingComments.value = pendingCommentsResult
    commentStatistics.value = statisticsResult
  } catch (error) {
    console.error('初始化评论数据失败:', error)
  }
}

// 获取政治观点访问权限数据
const fetchPoliticalAccess = async () => {
  try {
    const response = await politicalAccessApi.getAllAccess()
    if (response.data.success) {
      politicalAccessList.value = response.data.data
    }
  } catch (error) {
    console.error('获取政治观点访问权限失败:', error)
  }
}

// 搜索评论
const searchComments = async () => {
  if (!commentSearchQuery.value.trim()) {
    allComments.value = await commentStore.getAllCommentsForAdmin()
    return
  }
  
  try {
    allComments.value = await commentStore.searchComments(commentSearchQuery.value)
  } catch (error) {
    console.error('搜索评论失败:', error)
  }
}

// 筛选评论
const filterCommentsByStatus = async () => {
  if (commentFilterStatus.value === 'all') {
    allComments.value = await commentStore.getAllCommentsForAdmin()
  } else {
    allComments.value = await commentStore.getCommentsByStatus(commentFilterStatus.value)
  }
}

// 审核评论
const reviewComment = async (commentId: number, action: 'approve' | 'reject') => {
  try {
    await commentStore.reviewComment(commentId, action, '管理员')
    
    // 重新加载数据
    await initializeCommentData()
    
    // 显示成功消息
    const actionText = action === 'approve' ? '通过' : '拒绝'
    alert(`评论已${actionText}`)
  } catch (error) {
    console.error('审核评论失败:', error)
    alert('审核失败，请稍后再试')
  }
}

// 批量审核
const batchReview = async () => {
  if (selectedComments.value.size === 0) {
    alert('请先选择要操作的评论')
    return
  }
  
  if (!batchActionType.value) {
    alert('请选择操作类型')
    return
  }
  
  try {
    const commentIds = Array.from(selectedComments.value)
    await commentStore.batchReviewComments(
      commentIds, 
      batchActionType.value, 
      '管理员'
    )
    
    // 清空选择
    selectedComments.value.clear()
    
    // 重新加载数据
    await initializeCommentData()
    
    // 关闭批量操作面板
    showBatchActions.value = false
    batchActionType.value = ''
    
    alert('批量操作完成')
  } catch (error) {
    console.error('批量操作失败:', error)
    alert('批量操作失败，请稍后再试')
  }
}

// 批量删除
const batchDelete = async () => {
  if (selectedComments.value.size === 0) {
    alert('请先选择要删除的评论')
    return
  }
  
  if (!confirm(`确定要删除选中的 ${selectedComments.value.size} 条评论吗？`)) {
    return
  }
  
  try {
    const commentIds = Array.from(selectedComments.value)
    await commentStore.batchDeleteComments(commentIds)
    
    // 清空选择
    selectedComments.value.clear()
    
    // 重新加载数据
    await initializeCommentData()
    
    alert('批量删除完成')
  } catch (error) {
    console.error('批量删除失败:', error)
    alert('批量删除失败，请稍后再试')
  }
}

// 切换评论选择
const toggleCommentSelection = (commentId: number) => {
  if (selectedComments.value.has(commentId)) {
    selectedComments.value.delete(commentId)
  } else {
    selectedComments.value.add(commentId)
  }
}

// 全选/取消全选
const toggleSelectAll = () => {
  if (selectedComments.value.size === allComments.value.length) {
    selectedComments.value.clear()
  } else {
    allComments.value.forEach(comment => {
      selectedComments.value.add(Number(comment.id))
    })
  }
}

// 获取评论状态文本
const getCommentStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

// 获取评论状态样式
const getCommentStatusClass = (status: string) => {
  const classMap: Record<string, string> = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'APPROVED': 'bg-green-100 text-green-800',
    'REJECTED': 'bg-red-100 text-red-800'
  }
  return classMap[status] || 'bg-gray-100 text-gray-800'
}

// 拒绝评论
const rejectComment = async (commentId: number) => {
  await reviewComment(commentId, 'reject')
}

// 删除单个评论
const deleteSingleComment = async (commentId: number) => {
  if (confirm('确定要删除这条评论吗？')) {
    try {
      await commentStore.batchDeleteComments([commentId])
      await initializeCommentData()
      alert('评论已删除')
    } catch (error) {
      console.error('删除评论失败:', error)
      alert('删除失败，请稍后再试')
    }
  }
}

// 政治观点管理
const openPoliticalForm = (view?: any) => {
  if (view) {
    editingPoliticalId.value = view.id
    newPoliticalView.value = {
      type: view.type,
      title: view.title,
      description: view.description,
      icon: view.icon || '',
      displayOrder: view.displayOrder || 0,
      isActive: view.isActive,
      position: view.position || '',
      details: view.details || '',
      testDate: view.testDate || ''
    }
  } else {
    editingPoliticalId.value = null
    newPoliticalView.value = {
      type: 'CORE_VALUE' as const,
      title: '',
      description: '',
      icon: '',
      displayOrder: 0,
      isActive: true,
      position: '',
      details: '',
      testDate: ''
    }
  }
  showPoliticalForm.value = true
}

const savePoliticalView = async () => {
  try {
    if (editingPoliticalId.value) {
      // 更新
      await politicalStore.updatePoliticalView(editingPoliticalId.value, newPoliticalView.value)
      alert('政治观点更新成功')
    } else {
      // 创建
      await politicalStore.createPoliticalView(newPoliticalView.value)
      alert('政治观点创建成功')
    }
    
    showPoliticalForm.value = false
    await politicalStore.fetchPoliticalViews() // 重新加载数据
  } catch (error) {
    console.error('保存政治观点失败:', error)
    alert('保存失败，请稍后再试')
  }
}

const editPoliticalView = (view: any) => {
  openPoliticalForm(view)
}

const togglePoliticalViewStatus = async (id: number) => {
  try {
    await politicalStore.toggleActiveStatus(id)
    alert('状态切换成功')
  } catch (error) {
    console.error('切换政治观点状态失败:', error)
    alert('操作失败，请稍后再试')
  }
}

const deletePoliticalView = async (id: number) => {
  if (confirm('确定要删除这个政治观点吗？')) {
    try {
      await politicalStore.deletePoliticalView(id)
      alert('政治观点删除成功')
    } catch (error) {
      console.error('删除政治观点失败:', error)
      alert('删除失败，请稍后再试')
    }
  }
}

// === 政治观点访问权限管理功能 ===

// 打开访问权限表单
const openPoliticalAccessForm = (access?: PoliticalViewAccess) => {
  if (access) {
    editingAccessId.value = access.id
    newPoliticalAccess.value = {
      name: access.name,
      description: access.description || '',
      isActive: access.isActive
    }
  } else {
    editingAccessId.value = null
    newPoliticalAccess.value = {
      name: '',
      description: '',
      isActive: true
    }
  }
  showPoliticalAccessForm.value = true
}

// 保存访问权限
const savePoliticalAccess = async () => {
  try {
    if (editingAccessId.value) {
      // 更新
      await politicalAccessApi.updateAccess(editingAccessId.value, newPoliticalAccess.value)
      alert('访问权限更新成功')
    } else {
      // 创建
      await politicalAccessApi.createAccess(newPoliticalAccess.value)
      alert('访问权限创建成功')
    }
    
    showPoliticalAccessForm.value = false
    await fetchPoliticalAccess() // 重新加载数据
  } catch (error) {
    console.error('保存访问权限失败:', error)
    alert('保存失败，请稍后再试')
  }
}

// 编辑访问权限
const editPoliticalAccess = (access: PoliticalViewAccess) => {
  openPoliticalAccessForm(access)
}

// 删除访问权限
const deletePoliticalAccess = async (id: number) => {
  if (confirm('确定要删除这个访问权限吗？')) {
    try {
      await politicalAccessApi.deleteAccess(id)
      alert('访问权限删除成功')
      await fetchPoliticalAccess() // 重新加载数据
    } catch (error) {
      console.error('删除访问权限失败:', error)
      alert('删除失败，请稍后再试')
    }
  }
}

// 切换访问权限状态
const togglePoliticalAccessStatus = async (id: number) => {
  try {
    await politicalAccessApi.toggleActive(id)
    await fetchPoliticalAccess() // 重新加载数据
  } catch (error) {
    console.error('切换访问权限状态失败:', error)
    alert('操作失败，请稍后再试')
  }
}


</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
    </div>

    <!-- 主要内容 -->
    <div v-else class="flex">
      <!-- 侧边栏 -->
      <div class="w-64 bg-white shadow-lg h-screen sticky top-0">
        <div class="p-6">
          <h1 class="text-2xl font-bold text-gray-800 mb-8">管理后台</h1>
          
          <nav class="space-y-2">
            <button
              @click="activeTab = 'dashboard'"
              :class="[
                'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors',
                activeTab === 'dashboard' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <BarChart3 class="w-5 h-5 mr-3" />
              数据总览
            </button>
            
            <button
              @click="activeTab = 'blogs'"
              :class="[
                'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors',
                activeTab === 'blogs' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <FileText class="w-5 h-5 mr-3" />
              博客管理
            </button>
            
            <button
              @click="activeTab = 'media'"
              :class="[
                'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors',
                activeTab === 'media' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <Image class="w-5 h-5 mr-3" />
              媒体库管理
            </button>
            
            <button
              @click="activeTab = 'profile'"
              :class="[
                'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors',
                activeTab === 'profile' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <User class="w-5 h-5 mr-3" />
              个人资料
            </button>
            
            <button
              @click="activeTab = 'political'"
              :class="[
                'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors',
                activeTab === 'political' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <MessageSquare class="w-5 h-5 mr-3" />
              政治观点
            </button>
            
            <button
              @click="activeTab = 'comments'"
              :class="[
                'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors',
                activeTab === 'comments' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <MessageSquare class="w-5 h-5 mr-3" />
              评论管理
              <span v-if="pendingComments.length > 0" class="ml-auto bg-red-500 text-white text-xs rounded-full px-2 py-1">
                {{ pendingComments.length }}
              </span>
            </button>
          </nav>
        </div>
        
        <div class="absolute bottom-0 w-64 p-6">
          <button
            @click="logout"
            class="w-full flex items-center px-4 py-3 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
          >
            <LogOut class="w-5 h-5 mr-3" />
            登出
          </button>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="flex-1 p-8">
        <!-- 数据总览 -->
        <div v-if="activeTab === 'dashboard'" class="space-y-6">
          <h2 class="text-3xl font-bold text-gray-900">数据总览</h2>
          
          <!-- 统计卡片 -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <FileText class="w-8 h-8 text-blue-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">总文章数</p>
                  <p class="text-2xl font-bold text-gray-900">{{ stats.totalPosts }}</p>
                </div>
              </div>
            </div>
            
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <Eye class="w-8 h-8 text-green-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">总阅读量</p>
                  <p class="text-2xl font-bold text-gray-900">{{ stats.totalViews }}</p>
                </div>
              </div>
            </div>
            
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <Image class="w-8 h-8 text-purple-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">媒体项目</p>
                  <p class="text-2xl font-bold text-gray-900">{{ stats.totalMedia }}</p>
                </div>
              </div>
            </div>
            
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <Star class="w-8 h-8 text-yellow-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">已完成</p>
                  <p class="text-2xl font-bold text-gray-900">{{ stats.completedMedia }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 快速操作 -->
          <div class="bg-white p-6 rounded-lg shadow">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">快速操作</h3>
            <div class="flex flex-wrap gap-4">
              <button
                @click="openBlogForm()"
                class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                <Plus class="w-4 h-4 mr-2" />
                新增文章
              </button>

              <button
                @click="openMediaForm()"
                class="flex items-center px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
              >
                <Plus class="w-4 h-4 mr-2" />
                新增媒体
              </button>
              <button
                @click="openProfileForm()"
                class="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                <Settings class="w-4 h-4 mr-2" />
                编辑资料
              </button>
            </div>
          </div>
        </div>

        <!-- 博客管理 -->
        <div v-else-if="activeTab === 'blogs'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-3xl font-bold text-gray-900">博客管理</h2>
            <button
              @click="openBlogForm()"
              class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              <Plus class="w-4 h-4 mr-2" />
              新增文章
            </button>
          </div>

          <div class="bg-white rounded-lg shadow overflow-hidden">
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">文章</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">分类</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">阅读量</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">发布时间</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="post in blogStore.posts" :key="post.id">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="text-sm font-medium text-gray-900">{{ post.title }}</div>
                      <div class="text-sm text-gray-500">{{ post.excerpt }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 text-xs font-semibold bg-blue-100 text-blue-800 rounded-full">
                        {{ post.category }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span :class="[
                        'px-2 py-1 text-xs font-semibold rounded-full',
                        post.status === 'PUBLISHED' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                      ]">
                        {{ post.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ post.viewCount }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {{ formatDate(post.publishedAt || post.createdAt) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        @click="openBlogForm(post)"
                        class="text-indigo-600 hover:text-indigo-900 mr-4"
                      >
                        <Edit class="w-4 h-4" />
                      </button>
                      <button
                        @click="deleteBlogPost(Number(post.id))"
                        class="text-red-600 hover:text-red-900"
                      >
                        <Trash2 class="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- 媒体库管理 -->
        <div v-else-if="activeTab === 'media'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-3xl font-bold text-gray-900">媒体库管理</h2>
            <button
              @click="openMediaForm()"
              class="flex items-center px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
            >
              <Plus class="w-4 h-4 mr-2" />
              新增媒体
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div
              v-for="item in mediaStore.items"
              :key="item.id"
              class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow"
            >
              <div class="aspect-[2/3] bg-gray-200 relative">
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
                  <component :is="getMediaTypeIcon(item.type)" class="w-8 h-8 text-gray-400" />
                </div>
              </div>
              
              <div class="p-3">
                <div class="flex items-center mb-1">
                  <component :is="getMediaTypeIcon(item.type)" class="w-3 h-3 text-gray-600 mr-1" />
                  <span class="text-xs text-gray-600 uppercase">{{ item.type }}</span>
                </div>
                
                <h3 class="font-semibold text-gray-900 mb-1 text-sm line-clamp-2">{{ item.title }}</h3>
                <p class="text-xs text-gray-600 mb-2">{{ item.creator }}</p>
                
                <div class="flex items-center justify-between mb-2">
                  <div v-if="item.rating" class="flex items-center">
                    <Star class="w-3 h-3 text-yellow-500 mr-1" />
                    <span class="text-xs">{{ item.rating }}/5</span>
                  </div>
                  <span :class="[
                    'px-1 py-0.5 text-xs rounded-full',
                    item.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                    item.status === 'WATCHING' ? 'bg-blue-100 text-blue-800' :
                    'bg-gray-100 text-gray-800'
                  ]">
                    {{ item.status === 'COMPLETED' ? '已完成' : 
                       item.status === 'WATCHING' ? '观看中' : '想看' }}
                  </span>
                </div>
                
                <div class="flex justify-between items-center">
                  <span class="text-xs text-gray-500">{{ formatDate(item.dateAdded) }}</span>
                  <div class="flex space-x-1">
                    <button
                      @click="openMediaForm(item)"
                      class="text-indigo-600 hover:text-indigo-900 p-1"
                    >
                      <Edit class="w-3 h-3" />
                    </button>
                    <button
                      @click="deleteMediaItem(item.id)"
                      class="text-red-600 hover:text-red-900 p-1"
                    >
                      <Trash2 class="w-3 h-3" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 个人资料管理 -->
        <div v-else-if="activeTab === 'profile'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-3xl font-bold text-gray-900">个人资料</h2>
            <button
              @click="openProfileForm()"
              class="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
            >
              <Edit class="w-4 h-4 mr-2" />
              编辑资料
            </button>
          </div>

          <div v-if="userStore.profile" class="bg-white rounded-lg shadow p-8">
            <div class="flex items-center mb-6">
              <img
                :src="userStore.profile.avatar"
                :alt="userStore.profile.name"
                class="w-20 h-20 rounded-full object-cover mr-6"
              />
              <div>
                <h3 class="text-2xl font-bold text-gray-900">{{ userStore.profile.name }}</h3>
                <p class="text-gray-600">{{ userStore.profile.title }}</p>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h4 class="text-lg font-semibold text-gray-900 mb-3">基本信息</h4>
                <dl class="space-y-2">
                  <div>
                    <dt class="text-sm font-medium text-gray-500">邮箱</dt>
                    <dd class="text-sm text-gray-900">{{ userStore.profile.email || '未设置' }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">位置</dt>
                    <dd class="text-sm text-gray-900">{{ userStore.profile.location || '未设置' }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">个人简介</dt>
                    <dd class="text-sm text-gray-900">{{ userStore.profile.bio || '未设置' }}</dd>
                  </div>
                </dl>
              </div>

              <div>
                <h4 class="text-lg font-semibold text-gray-900 mb-3">技能</h4>
                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="skill in userStore.profile.skills"
                    :key="skill"
                    class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm"
                  >
                    {{ skill }}
                  </span>
                </div>

                <h4 class="text-lg font-semibold text-gray-900 mb-3 mt-6">社交链接</h4>
                <div class="space-y-2">
                  <div
                    v-for="link in userStore.profile.socialLinks"
                    :key="link.id"
                    class="flex items-center"
                  >
                    <span class="w-20 text-sm font-medium text-gray-500">{{ link.platform }}:</span>
                    <a :href="link.url" target="_blank" class="text-sm text-blue-600 hover:text-blue-800">
                      {{ link.url }}
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 政治观点管理 -->
        <div v-if="activeTab === 'political'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-3xl font-bold text-gray-900">政治观点管理</h2>
            <div class="flex space-x-4">
              <button
                @click="openPoliticalAccessForm()"
                class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                <UserCheck class="w-4 h-4 mr-2" />
                管理访问权限
              </button>
              <button
                @click="openPoliticalForm()"
                class="flex items-center px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700"
              >
                <Plus class="w-4 h-4 mr-2" />
                新增观点
              </button>
            </div>
          </div>

          <!-- 统计卡片 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="bg-white rounded-lg shadow p-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-gray-600">核心价值观</p>
                  <p class="text-3xl font-bold text-blue-600">{{ politicalStore.coreValues.length }}</p>
                </div>
                <Heart class="w-8 h-8 text-blue-500" />
              </div>
            </div>
            <div class="bg-white rounded-lg shadow p-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-gray-600">测试结果</p>
                  <p class="text-3xl font-bold text-green-600">{{ politicalStore.testResults.length }}</p>
                </div>
                <Shield class="w-8 h-8 text-green-500" />
              </div>
            </div>
            <div class="bg-white rounded-lg shadow p-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-gray-600">总计</p>
                  <p class="text-3xl font-bold text-purple-600">{{ politicalStore.views.length }}</p>
                </div>
                <MessageSquare class="w-8 h-8 text-purple-500" />
              </div>
            </div>
          </div>

          <!-- 政治观点列表 -->
          <div class="bg-white rounded-lg shadow">
            <div class="px-6 py-4 border-b border-gray-200">
              <h3 class="text-lg font-semibold text-gray-900">所有政治观点</h3>
            </div>
            <div class="p-6">
              <div class="space-y-4">
                <div
                  v-for="view in politicalStore.views"
                  :key="view.id"
                  class="border border-gray-200 rounded-lg p-4"
                >
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <div class="flex items-center mb-2">
                        <span :class="[
                          'px-2 py-1 text-xs rounded-full mr-3',
                          view.type === 'CORE_VALUE' 
                            ? 'bg-blue-100 text-blue-800' 
                            : 'bg-green-100 text-green-800'
                        ]">
                          {{ view.type === 'CORE_VALUE' ? '核心价值观' : '政治测试结果' }}
                        </span>
                        <span :class="[
                          'px-2 py-1 text-xs rounded-full',
                          view.isActive 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-gray-100 text-gray-800'
                        ]">
                          {{ view.isActive ? '活跃' : '停用' }}
                        </span>
                      </div>
                      <h4 class="text-lg font-semibold text-gray-900 mb-2">{{ view.title }}</h4>
                      <p class="text-gray-600 mb-2">{{ view.description }}</p>
                      <div v-if="view.type === 'TEST_RESULT'" class="text-sm text-gray-500">
                        <p v-if="view.position"><strong>立场：</strong>{{ view.position }}</p>
                        <p v-if="view.details"><strong>详细：</strong>{{ view.details }}</p>
                        <p v-if="view.testDate"><strong>测试日期：</strong>{{ view.testDate }}</p>
                      </div>
                    </div>
                    <div class="flex space-x-2 ml-4">
                      <button
                        @click="editPoliticalView(view)"
                        class="p-2 text-blue-600 hover:bg-blue-50 rounded"
                      >
                        <Edit class="w-4 h-4" />
                      </button>
                      <button
                        @click="togglePoliticalViewStatus(view.id)"
                        class="p-2 text-gray-600 hover:bg-gray-50 rounded"
                      >
                        <Eye class="w-4 h-4" />
                      </button>
                      <button
                        @click="deletePoliticalView(view.id)"
                        class="p-2 text-red-600 hover:bg-red-50 rounded"
                      >
                        <Trash2 class="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 政治观点访问权限管理 -->
          <div class="bg-white rounded-lg shadow mt-8">
            <div class="px-6 py-4 border-b border-gray-200">
              <h3 class="text-lg font-semibold text-gray-900">访问权限管理</h3>
              <p class="text-sm text-gray-600 mt-1">管理可以访问政治观点页面的人员名单</p>
            </div>
            <div class="p-6">
              <div class="space-y-4">
                <div
                  v-for="access in politicalAccessList"
                  :key="access.id"
                  class="border border-gray-200 rounded-lg p-4"
                >
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <div class="flex items-center mb-2">
                        <span :class="[
                          'px-2 py-1 text-xs rounded-full',
                          access.isActive 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-gray-100 text-gray-800'
                        ]">
                          {{ access.isActive ? '活跃' : '停用' }}
                        </span>
                      </div>
                      <h4 class="text-lg font-semibold text-gray-900 mb-2">{{ access.name }}</h4>
                      <p v-if="access.description" class="text-gray-600 mb-2">{{ access.description }}</p>
                      <div class="text-sm text-gray-500">
                        <p>创建时间：{{ new Date(access.createdAt).toLocaleString() }}</p>
                      </div>
                    </div>
                    <div class="flex space-x-2 ml-4">
                      <button
                        @click="editPoliticalAccess(access)"
                        class="p-2 text-blue-600 hover:bg-blue-50 rounded"
                      >
                        <Edit class="w-4 h-4" />
                      </button>
                      <button
                        @click="togglePoliticalAccessStatus(access.id)"
                        class="p-2 text-gray-600 hover:bg-gray-50 rounded"
                      >
                        <Eye class="w-4 h-4" />
                      </button>
                      <button
                        @click="deletePoliticalAccess(access.id)"
                        class="p-2 text-red-600 hover:bg-red-50 rounded"
                      >
                        <Trash2 class="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
                
                <!-- 空状态 -->
                <div v-if="politicalAccessList.length === 0" class="text-center py-8">
                  <Lock class="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <p class="text-gray-500">暂无访问权限记录</p>
                  <button
                    @click="openPoliticalAccessForm()"
                    class="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    添加第一个访问权限
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 评论管理 -->
        <div v-if="activeTab === 'comments'" class="space-y-6">
          <div class="flex justify-between items-center">
            <h2 class="text-3xl font-bold text-gray-900">评论管理</h2>
            <div class="flex space-x-2">
              <button
                v-if="selectedComments.size > 0"
                @click="showBatchActions = !showBatchActions"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                批量操作 ({{ selectedComments.size }})
              </button>
              <button
                @click="initializeCommentData()"
                class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                重新加载
              </button>
            </div>
          </div>

          <!-- 统计卡片 -->
          <div v-if="commentStatistics" class="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <MessageSquare class="w-8 h-8 text-blue-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">总评论数</p>
                  <p class="text-2xl font-bold text-gray-900">{{ commentStatistics.total }}</p>
                </div>
              </div>
            </div>
            
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <Eye class="w-8 h-8 text-yellow-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">待审核</p>
                  <p class="text-2xl font-bold text-gray-900">{{ commentStatistics.pending }}</p>
                </div>
              </div>
            </div>
            
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <CheckCircle class="w-8 h-8 text-green-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">已通过</p>
                  <p class="text-2xl font-bold text-gray-900">{{ commentStatistics.approved }}</p>
                </div>
              </div>
            </div>
            
            <div class="bg-white p-6 rounded-lg shadow">
              <div class="flex items-center">
                <Shield class="w-8 h-8 text-red-600" />
                <div class="ml-4">
                  <p class="text-sm font-medium text-gray-600">通过率</p>
                  <p class="text-2xl font-bold text-gray-900">{{ Math.round(commentStatistics.approvalRate) }}%</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 搜索和筛选 -->
          <div class="bg-white rounded-lg shadow p-6">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="md:col-span-2 relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  v-model="commentSearchQuery"
                  @input="searchComments"
                  type="text"
                  placeholder="搜索评论内容或作者..."
                  class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              <select
                v-model="commentFilterStatus"
                @change="filterCommentsByStatus"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="all">全部状态</option>
                <option value="PENDING">待审核</option>
                <option value="APPROVED">已通过</option>
                <option value="REJECTED">已拒绝</option>
              </select>
            </div>
          </div>

          <!-- 批量操作面板 -->
          <div v-if="showBatchActions" class="bg-white rounded-lg shadow p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">批量操作</h3>
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
              <select
                v-model="batchActionType"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">选择操作</option>
                <option value="approve">批量通过</option>
                <option value="reject">批量拒绝</option>
              </select>
              
              <button
                @click="batchReview"
                :disabled="!batchActionType"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                执行操作
              </button>
              
              <button
                @click="batchDelete"
                class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                批量删除
              </button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="bg-white rounded-lg shadow overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
              <h3 class="text-lg font-semibold text-gray-900">评论列表</h3>
              <button
                @click="toggleSelectAll()"
                class="text-sm text-blue-600 hover:text-blue-700"
              >
                {{ selectedComments.size === allComments.length ? '取消全选' : '全选' }}
              </button>
            </div>
            
            <div v-if="allComments.length === 0" class="p-6 text-center text-gray-500">
              暂无评论数据
            </div>
            
            <div v-else class="divide-y divide-gray-200 max-h-96 overflow-y-auto">
              <div
                v-for="comment in allComments"
                :key="comment.id"
                class="p-6 hover:bg-gray-50"
                :class="{ 'bg-blue-50': selectedComments.has(Number(comment.id)) }"
              >
                <div class="flex items-start space-x-4">
                  <!-- 选择框 -->
                  <input
                    type="checkbox"
                    :checked="selectedComments.has(Number(comment.id))"
                    @change="toggleCommentSelection(Number(comment.id))"
                    class="mt-1"
                  />
                  
                  <!-- 评论内容 -->
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between mb-2">
                      <div class="flex items-center space-x-2">
                        <span class="font-medium text-gray-900">{{ comment.author }}</span>
                        <span
                          class="px-2 py-1 text-xs font-semibold rounded-full"
                          :class="getCommentStatusClass(comment.status)"
                        >
                          {{ getCommentStatusText(comment.status) }}
                        </span>
                        <span v-if="comment.spamScore && comment.spamScore > 0.5" class="px-2 py-1 text-xs bg-orange-100 text-orange-800 rounded-full">
                          风险分数: {{ Math.round(comment.spamScore * 100) }}%
                        </span>
                      </div>
                      <span class="text-sm text-gray-500">{{ formatDate(comment.createdAt) }}</span>
                    </div>
                    
                    <p class="text-gray-700 mb-2">{{ comment.content }}</p>
                    
                    <div class="flex items-center justify-between">
                                             <div class="text-sm text-gray-500">
                         <span>文章ID: {{ comment.postId }}</span>
                         <span v-if="comment.reviewedBy" class="ml-4">审核人: {{ comment.reviewedBy }}</span>
                       </div>
                      
                      <!-- 操作按钮 -->
                      <div class="flex space-x-2">
                        <button
                          v-if="comment.status === 'PENDING'"
                          @click="reviewComment(Number(comment.id), 'approve')"
                          class="px-3 py-1 bg-green-600 text-white rounded text-sm hover:bg-green-700"
                        >
                          通过
                        </button>
                                                                          <button
                           v-if="comment.status === 'PENDING'"
                           @click="rejectComment(Number(comment.id))"
                           class="px-3 py-1 bg-red-600 text-white rounded text-sm hover:bg-red-700"
                         >
                           拒绝
                         </button>
                                                 <button
                           @click="deleteSingleComment(Number(comment.id))"
                           class="px-3 py-1 bg-red-500 text-white rounded text-sm hover:bg-red-600"
                         >
                           删除
                         </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 博客表单对话框 -->
    <div v-if="showBlogForm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">
            {{ editingBlogId ? '编辑文章' : '新增文章' }}
          </h3>
          
          <form @submit.prevent="saveBlogPost" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input
                v-model="newBlogPost.title"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
                <input
                  v-model="newBlogPost.category"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                <select
                  v-model="newBlogPost.status"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="DRAFT">草稿</option>
                  <option value="PUBLISHED">发布</option>
                </select>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">摘要</label>
              <textarea
                v-model="newBlogPost.excerpt"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">内容</label>
              <textarea
                v-model="newBlogPost.content"
                rows="10"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标签</label>
              <div class="flex flex-wrap gap-2 mb-2">
                <span
                  v-for="(tag, index) in newBlogPost.tags"
                  :key="index"
                  class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-sm flex items-center"
                >
                  {{ tag }}
                  <button
                    type="button"
                    @click="removeTag('blog', index)"
                    class="ml-2 text-blue-600 hover:text-blue-800"
                  >
                    ×
                  </button>
                </span>
              </div>
              <input
                type="text"
                placeholder="输入标签后按Enter"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                @keypress.enter.prevent="(e) => { const target = e.target as HTMLInputElement; if (target) { addTag('blog', target.value); target.value = '' } }"
              />
            </div>

            <div class="flex justify-end space-x-4 pt-4">
              <button
                type="button"
                @click="showBlogForm = false"
                class="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                取消
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                保存
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 媒体表单对话框 -->
    <div v-if="showMediaForm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">
            {{ editingMediaId ? '编辑媒体' : '新增媒体' }}
          </h3>
          
          <form @submit.prevent="saveMediaItem" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
                <input
                  v-model="newMediaItem.title"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">原标题</label>
                <input
                  v-model="newMediaItem.originalTitle"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">封面图片URL</label>
              <input
                v-model="newMediaItem.cover"
                type="url"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
                <select
                  v-model="newMediaItem.type"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="BOOK">书籍</option>
                  <option value="MOVIE">电影</option>
                  <option value="TV">电视剧</option>
                  <option value="ANIME">动漫</option>
                </select>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                <select
                  v-model="newMediaItem.status"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="WANT_TO_WATCH">想看</option>
                  <option value="WATCHING">观看中</option>
                  <option value="COMPLETED">已完成</option>
                </select>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">评分</label>
                <input
                  v-model.number="newMediaItem.rating"
                  type="number"
                  min="0"
                  max="5"
                  step="0.1"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">创作者</label>
              <input
                v-model="newMediaItem.creator"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">评价</label>
              <textarea
                v-model="newMediaItem.review"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              ></textarea>
            </div>

            <div class="flex justify-end space-x-4 pt-4">
              <button
                type="button"
                @click="showMediaForm = false"
                class="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                取消
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
              >
                保存
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 个人资料表单对话框 -->
    <div v-if="showProfileForm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">编辑个人资料</h3>
          
          <form @submit.prevent="saveProfile" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">姓名</label>
                <input
                  v-model="userProfile.name"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
                <input
                  v-model="userProfile.title"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">头像URL</label>
              <input
                v-model="userProfile.avatar"
                type="url"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
                <input
                  v-model="userProfile.email"
                  type="email"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">位置</label>
                <input
                  v-model="userProfile.location"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">个人简介</label>
              <textarea
                v-model="userProfile.bio"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">技能</label>
              <div class="flex flex-wrap gap-2 mb-2">
                <span
                  v-for="(skill, index) in userProfile.skills"
                  :key="index"
                  class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-sm flex items-center"
                >
                  {{ skill }}
                  <button
                    type="button"
                    @click="removeSkill(index)"
                    class="ml-2 text-blue-600 hover:text-blue-800"
                  >
                    ×
                  </button>
                </span>
              </div>
              <input
                type="text"
                placeholder="输入技能后按Enter"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                @keypress.enter.prevent="(e) => { const target = e.target as HTMLInputElement; if (target) { addSkill(target.value); target.value = '' } }"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">社交链接</label>
              <div class="space-y-2">
                <div
                  v-for="(link, index) in userProfile.socialLinks"
                  :key="index"
                  class="flex gap-2"
                >
                  <input
                    v-model="link.platform"
                    type="text"
                    placeholder="平台名称"
                    class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    v-model="link.url"
                    type="url"
                    placeholder="链接URL"
                    class="flex-2 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    v-model="link.icon"
                    type="text"
                    placeholder="图标"
                    class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <button
                    type="button"
                    @click="removeSocialLink(index)"
                    class="px-3 py-2 text-red-600 hover:text-red-800"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </div>
              <button
                type="button"
                @click="addSocialLink"
                class="mt-2 px-4 py-2 text-blue-600 border border-blue-300 rounded-lg hover:bg-blue-50"
              >
                <Plus class="w-4 h-4 inline mr-2" />
                添加链接
              </button>
            </div>

            <div class="flex justify-end space-x-4 pt-4">
              <button
                type="button"
                @click="showProfileForm = false"
                class="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                取消
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                保存
              </button>
            </div>
          </form>
        </div>

        </div>
      </div>
    </div>



    <!-- 政治观点表单对话框 -->
    <div v-if="showPoliticalForm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">
            {{ editingPoliticalId ? '编辑政治观点' : '新增政治观点' }}
          </h3>
          
          <form @submit.prevent="savePoliticalView" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
                <select
                  v-model="newPoliticalView.type"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="CORE_VALUE">核心价值观</option>
                  <option value="TEST_RESULT">政治测试结果</option>
                </select>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                <select
                  v-model="newPoliticalView.isActive"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option :value="true">活跃</option>
                  <option :value="false">停用</option>
                </select>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input
                v-model="newPoliticalView.title"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <textarea
                v-model="newPoliticalView.description"
                rows="3"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">图标</label>
              <input
                v-model="newPoliticalView.icon"
                type="text"
                placeholder="图标名称或URL"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">显示顺序</label>
              <input
                v-model.number="newPoliticalView.displayOrder"
                type="number"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <!-- 政治测试结果特有字段 -->
            <div v-if="newPoliticalView.type === 'TEST_RESULT' as any" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">政治立场</label>
                <input
                  v-model="newPoliticalView.position"
                  type="text"
                  placeholder="如：自由主义、保守主义等"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">详细说明</label>
                <textarea
                  v-model="newPoliticalView.details"
                  rows="3"
                  placeholder="详细的政治观点说明"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                ></textarea>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">测试日期</label>
                <input
                  v-model="newPoliticalView.testDate"
                  type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div class="flex justify-end space-x-4 pt-4">
              <button
                type="button"
                @click="showPoliticalForm = false"
                class="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                取消
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700"
              >
                {{ editingPoliticalId ? '更新' : '创建' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 访问权限表单对话框 -->
    <div v-if="showPoliticalAccessForm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">
          {{ editingAccessId ? '编辑访问权限' : '新增访问权限' }}
        </h3>
        
        <form @submit.prevent="savePoliticalAccess" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">姓名 *</label>
            <input
              v-model="newPoliticalAccess.name"
              type="text"
              required
              placeholder="请输入姓名"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
            <textarea
              v-model="newPoliticalAccess.description"
              placeholder="可选：添加描述信息"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            ></textarea>
          </div>
          
          <div class="flex items-center">
            <input
              v-model="newPoliticalAccess.isActive"
              type="checkbox"
              id="access-active"
              class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label for="access-active" class="ml-2 block text-sm text-gray-900">
              启用此访问权限
            </label>
          </div>
          
          <div class="flex justify-end space-x-4 pt-4">
            <button
              type="button"
              @click="showPoliticalAccessForm = false"
              class="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              取消
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              {{ editingAccessId ? '更新' : '创建' }}
            </button>
          </div>
        </form>
      </div>
    </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style> 