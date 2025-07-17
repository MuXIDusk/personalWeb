import { defineStore } from 'pinia'
import { ref } from 'vue'
import { 
  commentApi, 
  type Comment as ApiComment, 
  type CommentRequest,
  type CommentReviewRequest,
  type CommentStatistics,
  type CommentStatus
} from '@/services/api'

export interface Comment {
  id: number | string
  postId: number | string
  author: string
  email?: string
  content: string
  createdAt: string
  approved: boolean
  parentId?: number | string
  // 新增字段
  status: CommentStatus
  rejectReason?: string
  reviewedAt?: string
  reviewedBy?: string
  ipAddress?: string
  isSpam: boolean
  spamScore?: number
  userAgent?: string
}

export const useCommentStore = defineStore('comment', () => {
  const comments = ref<Comment[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const statistics = ref<CommentStatistics | null>(null)
  
  // 批量操作狀態
  const batchLoading = ref(false)
  const selectedComments = ref<Set<number>>(new Set())

  // 獲取文章的已審核評論
  const getCommentsForPost = async (postId: number) => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getCommentsForPost(postId)
      const postComments = response.data.data.map(convertApiCommentToFrontend)
      
      // 更新本地評論列表
      const otherComments = comments.value.filter(c => Number(c.postId) !== postId)
      comments.value = [...otherComments, ...postComments]
      
      return postComments
    } catch (err) {
      error.value = '獲取評論失敗'
      console.error('Error fetching comments:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  // 獲取文章的所有評論（包括未審核）
  const getAllCommentsForPost = async (postId: number) => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getAllCommentsForPost(postId)
      return response.data.data.map(convertApiCommentToFrontend)
    } catch (err) {
      error.value = '獲取所有評論失敗'
      console.error('Error fetching all comments:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  // 獲取待審核評論
  const getPendingComments = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getPendingComments()
      return response.data.data.map(convertApiCommentToFrontend)
    } catch (err) {
      error.value = '獲取待審核評論失敗'
      console.error('Error fetching pending comments:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  // 創建評論
  const addComment = async (comment: Omit<Comment, 'id' | 'createdAt' | 'approved' | 'status' | 'isSpam'>) => {
    try {
      loading.value = true
      error.value = null
      
      const request: CommentRequest = {
        postId: Number(comment.postId),
        author: comment.author,
        email: comment.email,
        content: comment.content,
        parentId: comment.parentId ? Number(comment.parentId) : undefined,
        ipAddress: comment.ipAddress,
        userAgent: comment.userAgent
      }
      
      const response = await commentApi.createComment(request)
      const newComment = convertApiCommentToFrontend(response.data.data)
      
      // 如果是已審核的評論，添加到本地列表
      if (newComment.approved) {
        comments.value.push(newComment)
      }
      
      return newComment
    } catch (err) {
      error.value = '提交評論失敗'
      console.error('Error creating comment:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // === 新增的審核功能 ===

  // 審核評論（新方法）
  const reviewComment = async (commentId: number, action: 'approve' | 'reject', reviewedBy: string) => {
    try {
      const request: CommentReviewRequest = {
        commentId,
        action,
        reviewedBy
      }
      
      await commentApi.reviewComment(request)
      
      // 更新本地評論狀態
      const comment = comments.value.find(c => Number(c.id) === commentId)
      if (comment) {
        comment.status = action.toUpperCase() as CommentStatus
        comment.approved = action === 'approve'
        comment.isSpam = action === 'reject'
        comment.reviewedBy = reviewedBy
        comment.reviewedAt = new Date().toISOString()
      }
      
      return true
    } catch (err) {
      error.value = '審核評論失敗'
      console.error('Error reviewing comment:', err)
      throw err
    }
  }

  // 批量審核評論
  const batchReviewComments = async (commentIds: number[], action: string, reviewedBy: string) => {
    try {
      batchLoading.value = true
      const response = await commentApi.batchReviewComments(commentIds, action, reviewedBy)
      
      // 更新本地評論狀態
      const actionUpper = action.toUpperCase() as CommentStatus
      commentIds.forEach(id => {
        const comment = comments.value.find(c => Number(c.id) === id)
        if (comment) {
          comment.status = actionUpper
          comment.approved = action === 'approve'
          comment.isSpam = action === 'reject'
          comment.reviewedBy = reviewedBy
          comment.reviewedAt = new Date().toISOString()
        }
      })
      
      return response.data.data
    } catch (err) {
      error.value = '批量審核失敗'
      console.error('Error batch reviewing comments:', err)
      throw err
    } finally {
      batchLoading.value = false
    }
  }

  // 搜索評論
  const searchComments = async (keyword: string) => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.searchComments(keyword)
      return response.data.data.map(convertApiCommentToFrontend)
    } catch (err) {
      error.value = '搜索評論失敗'
      console.error('Error searching comments:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  // 獲取評論統計
  const getStatistics = async () => {
    try {
      const response = await commentApi.getStatistics()
      statistics.value = response.data.data
      return response.data.data
    } catch (err) {
      error.value = '獲取統計數據失敗'
      console.error('Error getting statistics:', err)
      return null
    }
  }

  // 按狀態獲取評論
  const getCommentsByStatus = async (status: string) => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getCommentsByStatus(status)
      return response.data.data.map(convertApiCommentToFrontend)
    } catch (err) {
      error.value = '獲取評論失敗'
      console.error('Error fetching comments by status:', err)
      return []
    } finally {
      loading.value = false
    }
  }



  // 獲取高風險評論
  const getHighRiskComments = async (threshold: number = 0.6) => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getHighRiskComments(threshold)
      return response.data.data.map(convertApiCommentToFrontend)
    } catch (err) {
      error.value = '獲取高風險評論失敗'
      console.error('Error fetching high risk comments:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  // 獲取所有評論（管理員）
  const getAllCommentsForAdmin = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getAllCommentsForAdmin()
      const allComments = response.data.data.map(convertApiCommentToFrontend)
      comments.value = allComments
      return allComments
    } catch (err) {
      error.value = '獲取所有評論失敗'
      console.error('Error fetching all comments for admin:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  // 批量刪除評論
  const batchDeleteComments = async (commentIds: number[]) => {
    try {
      batchLoading.value = true
      const response = await commentApi.batchDeleteComments(commentIds)
      
      // 從本地列表中移除
      commentIds.forEach(id => {
        const index = comments.value.findIndex(c => Number(c.id) === id)
        if (index !== -1) {
          comments.value.splice(index, 1)
        }
      })
      
      return response.data.data
    } catch (err) {
      error.value = '批量刪除失敗'
      console.error('Error batch deleting comments:', err)
      throw err
    } finally {
      batchLoading.value = false
    }
  }

  // 選擇/取消選擇評論
  const toggleCommentSelection = (commentId: number) => {
    if (selectedComments.value.has(commentId)) {
      selectedComments.value.delete(commentId)
    } else {
      selectedComments.value.add(commentId)
    }
  }

  // 全選/取消全選
  const toggleSelectAll = (commentIds: number[]) => {
    if (selectedComments.value.size === commentIds.length) {
      selectedComments.value.clear()
    } else {
      selectedComments.value = new Set(commentIds)
    }
  }

  // 清空選擇
  const clearSelection = () => {
    selectedComments.value.clear()
  }

  // === 保留原有方法 ===

  // 審核評論（舊方法，保持兼容）
  const approveComment = async (id: number) => {
    try {
      await commentApi.approveComment(id)
      
      // 更新本地評論狀態
      const comment = comments.value.find(c => Number(c.id) === id)
      if (comment) {
        comment.approved = true
        comment.status = 'APPROVED'
      }
      
      return true
    } catch (err) {
      error.value = '審核評論失敗'
      console.error('Error approving comment:', err)
      throw err
    }
  }

  // 刪除評論
  const deleteComment = async (id: number) => {
    try {
      await commentApi.deleteComment(id)
      
      // 從本地列表中移除
      const index = comments.value.findIndex(c => Number(c.id) === id)
      if (index !== -1) {
        comments.value.splice(index, 1)
      }
      
      return true
    } catch (err) {
      error.value = '刪除評論失敗'
      console.error('Error deleting comment:', err)
      throw err
    }
  }

  // 獲取文章評論數量
  const getCommentCount = async (postId: number) => {
    try {
      const response = await commentApi.getCommentCount(postId)
      return response.data.data
    } catch (err) {
      console.error('Error getting comment count:', err)
      return 0
    }
  }

  // 工具函數：將API響應轉換為前端格式
  const convertApiCommentToFrontend = (apiComment: ApiComment): Comment => ({
    id: apiComment.id,
    postId: apiComment.postId,
    author: apiComment.author,
    email: apiComment.email,
    content: apiComment.content,
    createdAt: apiComment.createdAt,
    approved: apiComment.approved,
    parentId: apiComment.parentId,
    status: apiComment.status,
    reviewedAt: apiComment.reviewedAt,
    reviewedBy: apiComment.reviewedBy,
    ipAddress: apiComment.ipAddress,
    isSpam: apiComment.isSpam,
    spamScore: apiComment.spamScore,
    userAgent: apiComment.userAgent
  })

  // 本地方法（不調用API）
  const getLocalCommentsForPost = (postId: number | string) => {
    return comments.value
      .filter(comment => comment.postId.toString() === postId.toString() && comment.approved)
      .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
  }

  return {
    // 狀態
    comments,
    loading,
    error,
    statistics,
    batchLoading,
    selectedComments,
    
    // 原有方法
    getCommentsForPost,
    getAllCommentsForPost,
    getPendingComments,
    addComment,
    approveComment,
    deleteComment,
    getCommentCount,
    getLocalCommentsForPost,
    
    // 新增方法
    reviewComment,
    batchReviewComments,
    searchComments,
    getStatistics,
    getCommentsByStatus,
    getHighRiskComments,
    getAllCommentsForAdmin,
    batchDeleteComments,
    
    // 選擇相關方法
    toggleCommentSelection,
    toggleSelectAll,
    clearSelection
  }
}) 