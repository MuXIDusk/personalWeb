<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore, type BlogPost } from '../../stores/blog'
import { useCommentStore } from '../../stores/comment'
import { Calendar, Eye, User, Tag, ArrowLeft, Share2, ThumbsUp, MessageCircle } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()
const commentStore = useCommentStore()

const postId = route.params.id as string
const post = ref<BlogPost | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const isLiked = ref(false)
const liking = ref(false)

// 评论相关状态
const comments = computed(() => commentStore.getLocalCommentsForPost(postId))
const newComment = ref({
  author: '',
  email: '',
  content: ''
})

// 相关文章
const relatedPosts = computed(() => {
  if (!post.value) return []
  
  return blogStore.publishedPosts
    .filter(p => 
      p.id !== post.value!.id && 
      (p.category === post.value!.category || 
       p.tags.some(tag => post.value!.tags.includes(tag)))
    )
    .slice(0, 3)
})

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

const getReadingTime = (content: string) => {
  const wordsPerMinute = 200
  const wordCount = content.replace(/<[^>]*>/g, '').split(/\s+/).length
  return Math.ceil(wordCount / wordsPerMinute)
}

const toggleLike = async () => {
  if (!post.value || liking.value) {
    return
  }
  
  liking.value = true
  
  try {
    const result = await blogStore.toggleLike(Number(post.value.id))
    
    // 更新點讚狀態
    isLiked.value = result.isLiked
    
    // 強制刷新響應式數據
    if (post.value) {
      post.value = { ...post.value, likeCount: result.newLikeCount }
    }
  } catch (error) {
    console.error('點讚操作失敗:', error)
    // 顯示錯誤提示
    alert((error as any)?.message || '操作失敗，請稍後再試')
  } finally {
    liking.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.author || !newComment.value.content) {
    alert('请填写必要的信息')
    return
  }

  try {
    await commentStore.addComment({
      postId: postId,
      author: newComment.value.author,
      email: newComment.value.email,
      content: newComment.value.content
    })

    // 重置表单
    newComment.value = {
      author: '',
      email: '',
      content: ''
    }

    alert('评论已提交，等待审核')
    
    // 重新獲取評論列表
    await commentStore.getCommentsForPost(Number(postId))
  } catch (error) {
    alert('提交評論失敗，請稍後再試')
    console.error('Submit comment error:', error)
  }
}

const sharePost = async () => {
  if (navigator.share) {
    try {
      await navigator.share({
        title: post.value?.title,
        text: post.value?.excerpt,
        url: window.location.href
      })
    } catch (err) {
      console.log('分享取消')
    }
  } else {
    // 降级到复制链接
    navigator.clipboard.writeText(window.location.href)
    alert('链接已复制到剪贴板')
  }
}



// 獲取文章數據
const fetchPost = async () => {
  try {
    loading.value = true
    error.value = null
    
    const fetchedPost = await blogStore.getPostById(Number(postId))
    
    if (!fetchedPost) {
      error.value = '文章未找到'
      return
    }
    
    post.value = fetchedPost
    
    // 增加阅读量
    blogStore.incrementViewCount(Number(postId))
    
    // 檢查本次會話是否已點讚
    isLiked.value = blogStore.isPostLikedInSession(Number(postId))
    
    // 獲取評論
    await commentStore.getCommentsForPost(Number(postId))
    
  } catch (err) {
    console.error('Error fetching post:', err)
    error.value = '加載文章失敗'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  // 确保博客數據已初始化
  if (blogStore.posts.length === 0) {
    await blogStore.initialize()
  }
  
  await fetchPost()
})
</script>

<template>
  <!-- 加載狀態 -->
  <div v-if="loading" class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="text-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
      <p class="text-gray-600">正在加載文章...</p>
    </div>
  </div>

  <!-- 錯誤狀態 -->
  <div v-else-if="error" class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="text-center">
      <h2 class="text-2xl font-bold text-gray-900 mb-4">{{ error }}</h2>
      <p class="text-gray-600 mb-6">抱歉，無法加載文章內容。</p>
      <div class="space-x-4">
        <button
          @click="fetchPost"
          class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          重試
        </button>
        <button
          @click="$router.push('/blog')"
          class="px-6 py-3 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
        >
          返回博客列表
        </button>
      </div>
    </div>
  </div>

  <!-- 文章內容 -->
  <div v-else-if="post" class="min-h-screen bg-gray-50">
    <!-- 文章头部 -->
    <header class="bg-white border-b">
      <div class="container mx-auto px-4 py-8">
        <!-- 返回按钮 -->
        <button
          @click="$router.go(-1)"
          class="flex items-center text-blue-600 hover:text-blue-700 mb-6 transition-colors"
        >
          <ArrowLeft class="w-4 h-4 mr-2" />
          返回博客列表
        </button>

        <!-- 文章信息 -->
        <div class="max-w-4xl mx-auto">
          <h1 class="text-3xl md:text-4xl font-bold text-gray-900 leading-tight mb-6">
            {{ post.title }}
          </h1>

          <!-- 元信息 -->
          <div class="flex flex-wrap items-center gap-6 text-gray-600 mb-6">
            <div class="flex items-center">
              <User class="w-4 h-4 mr-2" />
              <span>{{ post.author }}</span>
            </div>
            
            <div class="flex items-center">
              <Calendar class="w-4 h-4 mr-2" />
              <span>{{ formatDate(post.publishedAt || post.createdAt) }}</span>
            </div>
            
            <div class="flex items-center">
              <Eye class="w-4 h-4 mr-2" />
              <span>{{ post.viewCount }} 次阅读</span>
            </div>
            
            <div class="flex items-center">
              📖 <span class="ml-1">{{ getReadingTime(post.content) }} 分钟阅读</span>
            </div>
          </div>

          <!-- 分类和标签 -->
          <div class="flex flex-wrap items-center gap-2 mb-6">
            <span class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
              {{ post.category }}
            </span>
            
            <div class="flex flex-wrap gap-1">
              <span
                v-for="tag in post.tags"
                :key="tag"
                class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs flex items-center"
              >
                <Tag class="w-3 h-3 mr-1" />
                {{ tag }}
              </span>
            </div>
          </div>

          <!-- 分享按钮 -->
          <div class="flex items-center gap-4">
            <button
              @click="sharePost"
              class="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Share2 class="w-4 h-4 mr-2" />
              分享文章
            </button>
          </div>
        </div>
      </div>
    </header>

    <div class="container mx-auto px-4 py-8">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- 主内容 -->
        <main class="lg:w-2/3">
          <article class="bg-white rounded-lg shadow-md overflow-hidden">
            <!-- 封面图片 -->
            <div v-if="post.coverImage" class="aspect-video bg-gray-200">
              <img
                :src="post.coverImage"
                :alt="post.title"
                class="w-full h-full object-cover"
              />
            </div>

            <!-- 文章内容 -->
            <div class="p-8">
              <div class="prose prose-lg max-w-none">
                <!-- 这里应该渲染 Markdown 内容，简化显示 -->
                <div v-html="post.content.replace(/\n/g, '<br>')"></div>
              </div>
            </div>

            <!-- 文章底部操作 -->
            <div class="border-t bg-gray-50 px-8 py-6">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                                      <button 
                      @click="toggleLike"
                      :disabled="liking"
                    :class="[
                      'flex items-center transition-colors group',
                      isLiked 
                        ? 'text-red-500 cursor-not-allowed opacity-70' 
                        : liking
                        ? 'text-gray-400 cursor-not-allowed'
                        : 'text-gray-600 hover:text-red-500'
                    ]"
                  >
                    <ThumbsUp 
                      :class="[
                        'w-4 h-4 mr-1 transition-transform',
                        !isLiked && !liking ? 'group-hover:scale-110' : '',
                        isLiked ? 'fill-current' : ''
                      ]" 
                    />
                    <span>
                      {{ post.likeCount || 0 }} 
                      {{ liking ? '處理中...' : isLiked ? '取消點讚' : '點讚' }}
                    </span>
                  </button>
                  
                  <button class="flex items-center text-gray-600 hover:text-blue-500 transition-colors">
                    <MessageCircle class="w-4 h-4 mr-1" />
                    <span>{{ comments.length }} 条评论</span>
                  </button>
                </div>
                
                <button
                  @click="sharePost"
                  class="flex items-center text-gray-600 hover:text-green-500 transition-colors"
                >
                  <Share2 class="w-4 h-4 mr-1" />
                  <span>分享</span>
                </button>
              </div>
            </div>
          </article>

          <!-- 相关文章 -->
          <section v-if="relatedPosts.length > 0" class="mt-8">
            <h3 class="text-2xl font-bold text-gray-900 mb-6">相关文章</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div
                v-for="relatedPost in relatedPosts"
                :key="relatedPost.id"
                class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer"
                @click="$router.push(`/blog/post/${relatedPost.id}`)"
              >
                <div class="p-4">
                  <h4 class="font-semibold text-gray-900 mb-2 line-clamp-2">
                    {{ relatedPost.title }}
                  </h4>
                  <p class="text-gray-600 text-sm mb-3 line-clamp-2">
                    {{ relatedPost.excerpt }}
                  </p>
                  <div class="flex items-center text-xs text-gray-500">
                    <Calendar class="w-3 h-3 mr-1" />
                    <span>{{ formatDate(relatedPost.publishedAt || relatedPost.createdAt) }}</span>
                    <span class="mx-2">•</span>
                    <Eye class="w-3 h-3 mr-1" />
                    <span>{{ relatedPost.viewCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 评论区 -->
          <section class="mt-8">
            <div class="bg-white rounded-lg shadow-md p-8">
              <h3 class="text-2xl font-bold text-gray-900 mb-6">评论 ({{ comments.length }})</h3>

              <!-- 评论表单 -->
              <div class="mb-8 p-6 bg-gray-50 rounded-lg">
                <h4 class="text-lg font-semibold text-gray-900 mb-4">发表评论</h4>
                <form @submit.prevent="submitComment" class="space-y-4">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">姓名 *</label>
                      <input
                        v-model="newComment.author"
                        type="text"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="请输入您的姓名"
                      />
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
                      <input
                        v-model="newComment.email"
                        type="email"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="请输入邮箱（可选）"
                      />
                    </div>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">评论内容 *</label>
                    <textarea
                      v-model="newComment.content"
                      required
                      rows="4"
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="请输入您的评论..."
                    ></textarea>
                  </div>
                  <button
                    type="submit"
                    class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                  >
                    提交评论
                  </button>
                </form>
              </div>

              <!-- 评论列表 -->
              <div class="space-y-6">
                <div
                  v-for="comment in comments"
                  :key="comment.id"
                  class="border-b border-gray-200 pb-6 last:border-b-0"
                >
                  <div class="flex items-center justify-between mb-3">
                    <div class="flex items-center">
                      <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white font-medium text-sm">
                        {{ comment.author.charAt(0).toUpperCase() }}
                      </div>
                      <div class="ml-3">
                        <p class="font-medium text-gray-900">{{ comment.author }}</p>
                        <p class="text-sm text-gray-500">{{ formatDate(comment.createdAt) }}</p>
                      </div>
                    </div>
                  </div>
                  <p class="text-gray-700 leading-relaxed">{{ comment.content }}</p>
                </div>
                
                <div v-if="comments.length === 0" class="text-center py-8">
                  <MessageCircle class="w-12 h-12 text-gray-400 mx-auto mb-3" />
                  <p class="text-gray-500">暂无评论，成为第一个评论者吧！</p>
                </div>
              </div>
            </div>
          </section>
        </main>

        <!-- 侧边栏 -->
        <aside class="lg:w-1/3">
          <!-- 作者信息 -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h4 class="text-lg font-semibold text-gray-900 mb-4">关于作者</h4>
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center text-white font-bold">
                {{ post.author.charAt(0).toUpperCase() }}
              </div>
              <div class="ml-3">
                <p class="font-medium text-gray-900">{{ post.author }}</p>
                <p class="text-sm text-gray-500">文章作者</p>
              </div>
            </div>
            <p class="text-gray-600 text-sm">
              感谢您阅读这篇文章，如果觉得有帮助，欢迎分享和评论！
            </p>
          </div>
        </aside>
      </div>
    </div>
  </div>

  <!-- 文章不存在的情况 -->
  <div v-else class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="text-center">
      <h2 class="text-2xl font-bold text-gray-900 mb-4">文章未找到</h2>
      <p class="text-gray-600 mb-6">抱歉，您访问的文章不存在或已被删除。</p>
      <button
        @click="$router.push('/blog')"
        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        返回博客列表
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 文章内容样式 */
.prose {
  color: #374151;
  line-height: 1.75;
}

.prose h2 {
  font-size: 1.5em;
  font-weight: 600;
  margin-top: 2em;
  margin-bottom: 1em;
  color: #111827;
}

.prose h3 {
  font-size: 1.25em;
  font-weight: 600;
  margin-top: 1.6em;
  margin-bottom: 0.6em;
  color: #111827;
}

.prose p {
  margin-bottom: 1.25em;
}

.prose ul, .prose ol {
  margin-bottom: 1.25em;
  padding-left: 1.625em;
}

.prose li {
  margin-bottom: 0.5em;
}

.prose blockquote {
  border-left: 4px solid #e5e7eb;
  padding-left: 1em;
  font-style: italic;
  margin: 1.6em 0;
  color: #6b7280;
}

.prose code {
  background-color: #f3f4f6;
  padding: 0.125em 0.25em;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

.prose pre {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1.5em 0;
}

/* 文字行数限制 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .prose {
    font-size: 1rem;
  }
}
</style> 