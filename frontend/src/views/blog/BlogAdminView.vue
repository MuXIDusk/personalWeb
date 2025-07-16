<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useBlogStore } from '../../stores/blog'
import { Plus, Edit, Trash2, Eye, Search, Filter, Save, X, FileText, Calendar, User, Lock, EyeOff, Shield } from 'lucide-vue-next'

const blogStore = useBlogStore()

// 访问控制状态
const isAuthenticated = ref(false)
const password = ref('')
const showPassword = ref(false)
const errorMessage = ref('')

// 简单的密码验证（在实际项目中应该使用更安全的方法）
const correctPassword = 'admin123'

const authenticate = () => {
  if (password.value === correctPassword) {
    isAuthenticated.value = true
    errorMessage.value = ''
    // 可以将认证状态保存到 localStorage
    localStorage.setItem('blog_admin_auth', 'true')
  } else {
    errorMessage.value = '密码错误，请重试'
    setTimeout(() => {
      errorMessage.value = ''
    }, 3000)
  }
}

// 状态管理
const activeTab = ref<'posts' | 'editor' | 'comments'>('posts')
const showEditor = ref(false)
const editingPost = ref<any>(null)
const searchQuery = ref('')
const filterStatus = ref<'all' | 'published' | 'draft'>('all')

// 编辑器状态
const editorForm = ref({
  title: '',
  content: '',
  excerpt: '',
  category: '',
  tags: [] as string[],
  status: 'draft' as 'draft' | 'published',
  coverImage: ''
})

const newTag = ref('')

// 计算属性
const filteredPosts = computed(() => {
  let posts = [...blogStore.posts]
  
  // 搜索筛选
  if (searchQuery.value) {
    posts = posts.filter(post => 
      post.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  // 状态筛选
  if (filterStatus.value !== 'all') {
    posts = posts.filter(post => post.status === filterStatus.value)
  }
  
  return posts.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
})

const pendingComments = computed(() => {
  return blogStore.comments.filter(comment => !comment.approved)
})

// 方法
const createNewPost = () => {
  editingPost.value = null
  editorForm.value = {
    title: '',
    content: '',
    excerpt: '',
    category: '技术',
    tags: [],
    status: 'draft',
    coverImage: ''
  }
  showEditor.value = true
  activeTab.value = 'editor'
}

const editPost = (post: any) => {
  editingPost.value = post
  editorForm.value = {
    title: post.title,
    content: post.content,
    excerpt: post.excerpt,
    category: post.category,
    tags: [...post.tags],
    status: post.status,
    coverImage: post.coverImage || ''
  }
  showEditor.value = true
  activeTab.value = 'editor'
}

const savePost = () => {
  if (!editorForm.value.title || !editorForm.value.content) {
    alert('请填写标题和内容')
    return
  }

  // 自动生成摘要（如果没有）
  if (!editorForm.value.excerpt) {
    editorForm.value.excerpt = editorForm.value.content.substring(0, 200) + '...'
  }

  const postData = {
    ...editorForm.value,
    author: '站长' // 这里应该从用户系统获取
  }

  if (editingPost.value) {
    // 更新现有文章
    blogStore.updatePost(editingPost.value.id, postData)
  } else {
    // 创建新文章
    blogStore.addPost(postData)
  }

  showEditor.value = false
  activeTab.value = 'posts'
  alert('文章保存成功')
}

const deletePost = (postId: string) => {
  if (confirm('确定要删除这篇文章吗？此操作不可恢复。')) {
    blogStore.deletePost(postId)
    alert('文章已删除')
  }
}

const publishPost = (postId: string) => {
  if (confirm('确定要发布这篇文章吗？')) {
    blogStore.publishPost(postId)
    alert('文章已发布')
  }
}

const addTag = () => {
  if (newTag.value && !editorForm.value.tags.includes(newTag.value)) {
    editorForm.value.tags.push(newTag.value)
    newTag.value = ''
  }
}

const removeTag = (index: number) => {
  editorForm.value.tags.splice(index, 1)
}

const approveComment = (commentId: string) => {
  blogStore.approveComment(commentId)
  alert('评论已通过审核')
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusColor = (status: string) => {
  return status === 'published' 
    ? 'bg-green-100 text-green-800' 
    : 'bg-yellow-100 text-yellow-800'
}

const getStatusText = (status: string) => {
  return status === 'published' ? '已发布' : '草稿'
}

const logout = () => {
  if (confirm('确定要退出后台管理吗？')) {
    isAuthenticated.value = false
    localStorage.removeItem('blog_admin_auth')
    // 重置所有状态
    activeTab.value = 'posts'
    showEditor.value = false
    editingPost.value = null
    searchQuery.value = ''
    filterStatus.value = 'all'
  }
}

onMounted(() => {
  // 检查是否已经认证过
  const savedAuth = localStorage.getItem('blog_admin_auth')
  if (savedAuth === 'true') {
    isAuthenticated.value = true
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 访问控制界面 -->
    <div v-if="!isAuthenticated" class="min-h-screen flex items-center justify-center">
      <div class="max-w-md w-full mx-4">
        <div class="bg-white rounded-lg shadow-lg p-8">
          <!-- 管理员图标 -->
          <div class="text-center mb-6">
            <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Shield class="w-8 h-8 text-blue-600" />
            </div>
            <h2 class="text-2xl font-bold text-gray-800 mb-2">博客管理后台</h2>
            <p class="text-gray-600">请输入管理员密码以访问后台系统</p>
          </div>
          
          <!-- 安全提示 -->
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <div class="flex items-start">
              <Lock class="w-5 h-5 text-blue-600 mt-0.5 mr-3 flex-shrink-0" />
              <div class="text-sm text-blue-800">
                <p class="font-medium mb-1">安全提示</p>
                <p>此页面仅供管理员使用，包含文章编辑、发布和评论管理等功能。</p>
              </div>
            </div>
          </div>
          
          <!-- 密码输入 -->
          <div class="space-y-4">
            <div class="relative">
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入管理员密码"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent pr-12"
                @keyup.enter="authenticate"
              />
              <button
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <Eye v-if="!showPassword" class="w-5 h-5" />
                <EyeOff v-else class="w-5 h-5" />
              </button>
            </div>
            
            <button
              @click="authenticate"
              class="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition-colors duration-200 font-medium"
            >
              进入管理后台
            </button>
            
            <!-- 错误信息 -->
            <div v-if="errorMessage" class="text-red-600 text-sm text-center">
              {{ errorMessage }}
            </div>
            
            <!-- 提示信息 -->
            <div class="text-center text-sm text-gray-500 mt-4">
              <p>演示密码：admin123</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 管理后台界面（认证后显示） -->
    <div v-else>
      <!-- 页面头部 -->
      <header class="bg-white shadow-sm">
        <div class="container mx-auto px-4 py-6">
          <div class="flex justify-between items-center">
            <div class="flex items-center">
              <Shield class="w-6 h-6 text-blue-600 mr-3" />
              <h1 class="text-3xl font-bold text-gray-800">博客管理后台</h1>
            </div>
            <div class="flex items-center space-x-4">
              <button
                @click="createNewPost"
                class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <Plus class="w-4 h-4 mr-2" />
                写新文章
              </button>
              <button
                @click="logout"
                class="flex items-center px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
              >
                <Lock class="w-4 h-4 mr-2" />
                退出登录
              </button>
            </div>
          </div>
        </div>
      </header>

      <div class="container mx-auto px-4 py-8">
        <!-- 标签页导航 -->
        <div class="flex space-x-1 mb-8">
          <button
            @click="activeTab = 'posts'; showEditor = false"
            class="px-4 py-2 rounded-lg font-medium transition-colors"
            :class="activeTab === 'posts' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-100'"
          >
            文章管理
          </button>
          <button
            @click="activeTab = 'editor'"
            class="px-4 py-2 rounded-lg font-medium transition-colors"
            :class="activeTab === 'editor' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-100'"
          >
            编辑器
          </button>
          <button
            @click="activeTab = 'comments'; showEditor = false"
            class="px-4 py-2 rounded-lg font-medium transition-colors relative"
            :class="activeTab === 'comments' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-100'"
          >
            评论管理
            <span v-if="pendingComments.length > 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
              {{ pendingComments.length }}
            </span>
          </button>
        </div>

        <!-- 文章管理 -->
        <div v-if="activeTab === 'posts'" class="space-y-6">
          <!-- 搜索和筛选 -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="md:col-span-2 relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="搜索文章标题或内容..."
                  class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              <select
                v-model="filterStatus"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="all">全部状态</option>
                <option value="published">已发布</option>
                <option value="draft">草稿</option>
              </select>
            </div>
          </div>

          <!-- 文章列表 -->
          <div class="bg-white rounded-lg shadow-md overflow-hidden">
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">标题</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">分类</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">阅读量</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">更新时间</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="post in filteredPosts" :key="post.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4">
                      <div class="flex items-center">
                        <FileText class="w-4 h-4 text-gray-400 mr-3" />
                        <div>
                          <div class="text-sm font-medium text-gray-900">{{ post.title }}</div>
                          <div class="text-sm text-gray-500">{{ post.excerpt.substring(0, 60) }}...</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        class="px-2 py-1 text-xs rounded-full"
                        :class="getStatusColor(post.status)"
                      >
                        {{ getStatusText(post.status) }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ post.category }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center text-sm text-gray-900">
                        <Eye class="w-4 h-4 mr-1" />
                        {{ post.viewCount }}
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {{ formatDate(post.updatedAt) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex items-center space-x-2">
                        <button
                          @click="editPost(post)"
                          class="text-blue-600 hover:text-blue-700"
                          title="编辑"
                        >
                          <Edit class="w-4 h-4" />
                        </button>
                        <button
                          v-if="post.status === 'draft'"
                          @click="publishPost(post.id)"
                          class="text-green-600 hover:text-green-700"
                          title="发布"
                        >
                          <FileText class="w-4 h-4" />
                        </button>
                        <button
                          @click="deletePost(post.id)"
                          class="text-red-600 hover:text-red-700"
                          title="删除"
                        >
                          <Trash2 class="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- 编辑器 -->
        <div v-if="activeTab === 'editor'" class="space-y-6">
          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-xl font-semibold text-gray-900">
                {{ editingPost ? '编辑文章' : '写新文章' }}
              </h2>
              <div class="flex space-x-2">
                <button
                  @click="editorForm.status = 'draft'; savePost()"
                  class="flex items-center px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
                >
                  <Save class="w-4 h-4 mr-2" />
                  保存草稿
                </button>
                <button
                  @click="editorForm.status = 'published'; savePost()"
                  class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                  <FileText class="w-4 h-4 mr-2" />
                  {{ editingPost?.status === 'published' ? '更新文章' : '发布文章' }}
                </button>
              </div>
            </div>

            <form @submit.prevent="savePost" class="space-y-6">
              <!-- 标题 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">文章标题 *</label>
                <input
                  v-model="editorForm.title"
                  type="text"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="请输入文章标题"
                />
              </div>

              <!-- 分类和标签 -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">分类</label>
                  <select
                    v-model="editorForm.category"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option v-for="category in blogStore.categories" :key="category" :value="category">
                      {{ category }}
                    </option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">标签</label>
                  <div class="flex flex-wrap gap-2 mb-2">
                    <span
                      v-for="(tag, index) in editorForm.tags"
                      :key="index"
                      class="inline-flex items-center px-2 py-1 bg-blue-100 text-blue-800 rounded text-sm"
                    >
                      {{ tag }}
                      <button
                        type="button"
                        @click="removeTag(index)"
                        class="ml-1 text-blue-600 hover:text-blue-800"
                      >
                        <X class="w-3 h-3" />
                      </button>
                    </span>
                  </div>
                  <div class="flex">
                    <input
                      v-model="newTag"
                      type="text"
                      class="flex-1 px-3 py-2 border border-gray-300 rounded-l-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="添加标签"
                      @keyup.enter="addTag"
                    />
                    <button
                      type="button"
                      @click="addTag"
                      class="px-3 py-2 bg-gray-600 text-white rounded-r-lg hover:bg-gray-700"
                    >
                      添加
                    </button>
                  </div>
                </div>
              </div>

              <!-- 摘要 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">文章摘要</label>
                <textarea
                  v-model="editorForm.excerpt"
                  rows="3"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="文章摘要（如果不填写，系统会自动生成）"
                ></textarea>
              </div>

              <!-- 内容 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">文章内容 *</label>
                <textarea
                  v-model="editorForm.content"
                  required
                  rows="20"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono"
                  placeholder="请输入文章内容（支持 Markdown 格式）"
                ></textarea>
                <p class="text-sm text-gray-500 mt-1">支持 Markdown 语法</p>
              </div>

              <!-- 封面图片 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">封面图片</label>
                <input
                  v-model="editorForm.coverImage"
                  type="url"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="封面图片 URL（可选）"
                />
              </div>
            </form>
          </div>
        </div>

        <!-- 评论管理 -->
        <div v-if="activeTab === 'comments'" class="space-y-6">
          <div class="bg-white rounded-lg shadow-md">
            <div class="px-6 py-4 border-b border-gray-200">
              <h3 class="text-lg font-semibold text-gray-900">待审核评论</h3>
            </div>
            
            <div v-if="pendingComments.length === 0" class="p-6 text-center text-gray-500">
              暂无待审核的评论
            </div>
            
            <div v-else class="divide-y divide-gray-200">
              <div
                v-for="comment in pendingComments"
                :key="comment.id"
                class="p-6"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <div class="flex items-center mb-2">
                      <User class="w-4 h-4 text-gray-400 mr-2" />
                      <span class="font-medium text-gray-900">{{ comment.author }}</span>
                      <span class="mx-2 text-gray-400">•</span>
                      <span class="text-sm text-gray-500">{{ formatDate(comment.createdAt) }}</span>
                    </div>
                    <p class="text-gray-700 mb-2">{{ comment.content }}</p>
                    <p class="text-sm text-gray-500">
                      文章：{{ blogStore.getPostById(comment.postId)?.title }}
                    </p>
                  </div>
                  <div class="ml-4">
                    <button
                      @click="approveComment(comment.id)"
                      class="px-3 py-1 bg-green-600 text-white rounded text-sm hover:bg-green-700 transition-colors"
                    >
                      通过
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
</template>

<style scoped>
/* 编辑器样式优化 */
textarea {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  line-height: 1.5;
}

/* 表格样式 */
table {
  border-collapse: collapse;
}

tbody tr:hover {
  background-color: #f9fafb;
}

/* 标签样式 */
.inline-flex {
  display: inline-flex;
  align-items: center;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .grid-cols-1.md\\:grid-cols-3 {
    grid-template-columns: 1fr;
  }
  
  .grid-cols-1.md\\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
  
  .overflow-x-auto {
    font-size: 0.875rem;
  }
}

/* 平滑过渡 */
.transition-colors {
  transition-property: color, background-color, border-color;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}
</style> 