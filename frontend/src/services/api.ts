import axios, { type AxiosResponse } from 'axios'

// API基礎配置
const API_BASE_URL = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 響應攔截器處理統一的錯誤
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: any) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 通用API響應類型
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// 博客相關API
export const blogApi = {
  // 獲取已發佈的文章列表
  getPosts: () => apiClient.get<ApiResponse<BlogPost[]>>('/blog/posts'),
  
  // 分頁獲取文章
  getPostsWithPagination: (page: number = 0, size: number = 10) => 
    apiClient.get<ApiResponse<{ content: BlogPost[], totalElements: number, totalPages: number }>>(`/blog/posts/page?page=${page}&size=${size}`),
  
  // 獲取所有文章（包括草稿）
  getAllPosts: () => apiClient.get<ApiResponse<BlogPost[]>>('/blog/posts/all'),
  
  // 獲取單篇文章
  getPost: (id: number) => apiClient.get<ApiResponse<BlogPost>>(`/blog/posts/${id}`),
  
  // 按分類獲取文章
  getPostsByCategory: (category: string) => 
    apiClient.get<ApiResponse<BlogPost[]>>(`/blog/posts/category/${encodeURIComponent(category)}`),
  
  // 按標籤獲取文章
  getPostsByTag: (tag: string) => 
    apiClient.get<ApiResponse<BlogPost[]>>(`/blog/posts/tag/${encodeURIComponent(tag)}`),
  
  // 搜索文章
  searchPosts: (query: string) => 
    apiClient.get<ApiResponse<BlogPost[]>>(`/blog/posts/search?q=${encodeURIComponent(query)}`),
  
  // 創建文章
  createPost: (post: BlogPostRequest) => 
    apiClient.post<ApiResponse<BlogPost>>('/blog/posts', post),
  
  // 更新文章
  updatePost: (id: number, post: BlogPostRequest) => 
    apiClient.put<ApiResponse<BlogPost>>(`/blog/posts/${id}`, post),
  
  // 刪除文章
  deletePost: (id: number) => 
    apiClient.delete<ApiResponse<void>>(`/blog/posts/${id}`),
  
  // 獲取分類列表
  getCategories: () => apiClient.get<ApiResponse<string[]>>('/blog/categories'),
  
  // 獲取標籤列表
  getTags: () => apiClient.get<ApiResponse<string[]>>('/blog/tags'),
  
  // 點讚文章
  likePost: (id: number) => apiClient.post<ApiResponse<number>>(`/blog/posts/${id}/like`),
  
  // 切換點讚狀態
  toggleLike: (id: number, currentlyLiked: boolean) => 
    apiClient.post<ApiResponse<number>>(`/blog/posts/${id}/toggle-like?currentlyLiked=${currentlyLiked}`),
  
  // 獲取點讚數
  getLikeCount: (id: number) => apiClient.get<ApiResponse<number>>(`/blog/posts/${id}/likes`),
}

// 媒體庫相關API
export const mediaApi = {
  // 獲取所有媒體項目
  getMediaItems: () => apiClient.get<ApiResponse<MediaItem[]>>('/media'),
  
  // 根據ID獲取媒體項目
  getMediaItem: (id: number) => apiClient.get<ApiResponse<MediaItem>>(`/media/${id}`),
  
  // 根據類型獲取媒體項目
  getMediaItemsByType: (type: string) => 
    apiClient.get<ApiResponse<MediaItem[]>>(`/media/type/${encodeURIComponent(type)}`),
  
  // 根據狀態獲取媒體項目
  getMediaItemsByStatus: (status: string) => 
    apiClient.get<ApiResponse<MediaItem[]>>(`/media/status/${encodeURIComponent(status)}`),
  
  // 搜索媒體項目
  searchMediaItems: (query: string) => 
    apiClient.get<ApiResponse<MediaItem[]>>(`/media/search?q=${encodeURIComponent(query)}`),
  
  // 根據標籤獲取媒體項目
  getMediaItemsByTag: (tag: string) => 
    apiClient.get<ApiResponse<MediaItem[]>>(`/media/tag/${encodeURIComponent(tag)}`),
  
  // 根據分類獲取媒體項目
  getMediaItemsByGenre: (genre: string) => 
    apiClient.get<ApiResponse<MediaItem[]>>(`/media/genre/${encodeURIComponent(genre)}`),
  
  // 創建媒體項目
  createMediaItem: (item: MediaItemRequest) => 
    apiClient.post<ApiResponse<MediaItem>>('/media', item),
  
  // 更新媒體項目
  updateMediaItem: (id: number, item: MediaItemRequest) => 
    apiClient.put<ApiResponse<MediaItem>>(`/media/${id}`, item),
  
  // 刪除媒體項目
  deleteMediaItem: (id: number) => 
    apiClient.delete<ApiResponse<void>>(`/media/${id}`),
  
  // 獲取所有分類
  getGenres: () => apiClient.get<ApiResponse<string[]>>('/media/genres'),
  
  // 獲取所有標籤
  getTags: () => apiClient.get<ApiResponse<string[]>>('/media/tags'),
  
  // 獲取媒體類型列表
  getTypes: () => apiClient.get<ApiResponse<string[]>>('/media/types'),
  
  // 獲取媒體狀態列表
  getStatuses: () => apiClient.get<ApiResponse<string[]>>('/media/statuses'),
}

// 用戶資料相關API
export const profileApi = {
  // 獲取用戶資料
  getUserProfile: () => apiClient.get<ApiResponse<UserProfile>>('/profile'),
  
  // 根據ID獲取用戶資料
  getUserProfileById: (id: number) => 
    apiClient.get<ApiResponse<UserProfile>>(`/profile/${id}`),
  
  // 創建用戶資料
  createUserProfile: (profile: UserProfileRequest) => 
    apiClient.post<ApiResponse<UserProfile>>('/profile', profile),
  
  // 更新主要用戶資料
  updateMainUserProfile: (profile: UserProfileRequest) => 
    apiClient.put<ApiResponse<UserProfile>>('/profile', profile),
  
  // 更新指定ID的用戶資料
  updateUserProfile: (id: number, profile: UserProfileRequest) => 
    apiClient.put<ApiResponse<UserProfile>>(`/profile/${id}`, profile),
  
  // 刪除用戶資料
  deleteUserProfile: (id: number) => 
    apiClient.delete<ApiResponse<void>>(`/profile/${id}`),
  
  // 檢查郵箱是否存在
  checkEmailExists: (email: string) => 
    apiClient.get<ApiResponse<boolean>>(`/profile/check-email?email=${encodeURIComponent(email)}`),
  
  // 獲取所有技能
  getAllSkills: () => apiClient.get<ApiResponse<string[]>>('/profile/skills'),
}

// 認證相關API
export const authApi = {
  // 驗證博客管理密碼
  validateBlogPassword: (password: string) => 
    apiClient.post<ApiResponse<boolean>>('/auth/blog/validate', { password }),
  
  // 驗證政治傾向頁面密碼
  validatePoliticalPassword: (password: string) => 
    apiClient.post<ApiResponse<boolean>>('/auth/political/validate', { password }),
}

// 政治觀點訪問權限相關API
export const politicalAccessApi = {
  // 獲取所有訪問權限
  getAllAccess: () => apiClient.get<ApiResponse<PoliticalViewAccess[]>>('/political-access'),
  
  // 獲取活躍的訪問權限
  getActiveAccess: () => apiClient.get<ApiResponse<PoliticalViewAccess[]>>('/political-access/active'),
  
  // 根據ID獲取訪問權限
  getAccessById: (id: number) => apiClient.get<ApiResponse<PoliticalViewAccess>>(`/political-access/${id}`),
  
  // 驗證訪問權限
  validateAccess: (request: PoliticalViewAccessRequest) => 
    apiClient.post<ApiResponse<boolean>>('/political-access/validate', request),
  
  // 創建訪問權限
  createAccess: (request: PoliticalViewAccessRequest) => 
    apiClient.post<ApiResponse<PoliticalViewAccess>>('/political-access', request),
  
  // 更新訪問權限
  updateAccess: (id: number, request: PoliticalViewAccessRequest) => 
    apiClient.put<ApiResponse<PoliticalViewAccess>>(`/political-access/${id}`, request),
  
  // 刪除訪問權限
  deleteAccess: (id: number) => 
    apiClient.delete<ApiResponse<void>>(`/political-access/${id}`),
  
  // 切換激活狀態
  toggleActive: (id: number) => 
    apiClient.put<ApiResponse<PoliticalViewAccess>>(`/political-access/${id}/toggle-active`),
}

// 導出單獨的驗證方法以便在組件中使用
export const validatePoliticalAccess = politicalAccessApi.validateAccess

// 評論審核請求接口
export interface CommentReviewRequest {
  commentId: number
  action: 'approve' | 'reject'
  reviewedBy: string
}

// 評論統計接口
export interface CommentStatistics {
  total: number
  pending: number
  approved: number
  rejected: number
  recent: number
  approvalRate: number
}

// 評論相關API
export const commentApi = {
  // 獲取文章的評論
  getCommentsForPost: (postId: number) => 
    apiClient.get<ApiResponse<Comment[]>>(`/comments/post/${postId}`),
  
  // 獲取文章的所有評論（包括未審核）
  getAllCommentsForPost: (postId: number) => 
    apiClient.get<ApiResponse<Comment[]>>(`/comments/post/${postId}/all`),
  
  // 獲取待審核評論
  getPendingComments: () => 
    apiClient.get<ApiResponse<Comment[]>>('/comments/pending'),
  
  // 創建評論
  createComment: (comment: CommentRequest) => 
    apiClient.post<ApiResponse<Comment>>('/comments', comment),
  
  // 審核評論（舊方法，保持兼容）
  approveComment: (id: number) => 
    apiClient.put<ApiResponse<void>>(`/comments/${id}/approve`),
  
  // 刪除評論
  deleteComment: (id: number) => 
    apiClient.delete<ApiResponse<void>>(`/comments/${id}`),
  
  // 獲取文章評論數量
  getCommentCount: (postId: number) => 
    apiClient.get<ApiResponse<number>>(`/comments/post/${postId}/count`),
  
  // === 簡化的審核功能 ===
  
  // 審核評論（新方法）
  reviewComment: (request: CommentReviewRequest) => 
    apiClient.post<ApiResponse<void>>('/comments/review', request),
  
  // 批量審核評論
  batchReviewComments: (commentIds: number[], action: string, reviewedBy: string) => {
    const params = new URLSearchParams({
      action,
      reviewedBy
    })
    commentIds.forEach(id => params.append('commentIds', id.toString()))
    
    return apiClient.post<ApiResponse<any>>(`/comments/batch-review?${params}`)
  },
  
  // 搜索評論
  searchComments: (keyword: string) => 
    apiClient.get<ApiResponse<Comment[]>>(`/comments/search?keyword=${encodeURIComponent(keyword)}`),
  
  // 獲取評論統計
  getStatistics: () => 
    apiClient.get<ApiResponse<CommentStatistics>>('/comments/statistics'),
  
  // 按狀態獲取評論
  getCommentsByStatus: (status: string) => 
    apiClient.get<ApiResponse<Comment[]>>(`/comments/status/${status}`),
  
  // 獲取高風險評論
  getHighRiskComments: (threshold: number = 0.6) => 
    apiClient.get<ApiResponse<Comment[]>>(`/comments/high-risk?threshold=${threshold}`),
  
  // 按日期範圍獲取評論
  getCommentsByDateRange: (startDate: string, endDate: string) => 
    apiClient.get<ApiResponse<Comment[]>>(`/comments/date-range?startDate=${startDate}&endDate=${endDate}`),
  
  // 獲取所有評論（管理員）
  getAllCommentsForAdmin: () => 
    apiClient.get<ApiResponse<Comment[]>>('/comments/admin/all'),
  
  // 批量刪除評論
  batchDeleteComments: (commentIds: number[]) => {
    const params = new URLSearchParams()
    commentIds.forEach(id => params.append('commentIds', id.toString()))
    return apiClient.delete<ApiResponse<any>>(`/comments/batch?${params}`)
  }
}

// 博客相關類型定義（與後端保持一致）
export interface BlogPost {
  id: number
  title: string
  content: string
  excerpt: string
  coverImage?: string
  category: string
  tags: string[]
  status: 'DRAFT' | 'PUBLISHED'
  createdAt: string
  updatedAt: string
  publishedAt?: string
  viewCount: number
  likeCount: number
  author: string
}

export interface BlogPostRequest {
  title: string
  content: string
  excerpt?: string
  coverImage?: string
  category: string
  tags: string[]
  status: 'DRAFT' | 'PUBLISHED'
  author: string
}

// 媒體庫相關類型定義
export interface MediaItem {
  id: number
  title: string
  originalTitle?: string
  cover: string
  type: 'BOOK' | 'MOVIE' | 'TV' | 'ANIME'
  creator: string
  genre: string[]
  rating?: number
  status: 'COMPLETED' | 'WATCHING' | 'WANT_TO_WATCH'
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

export interface MediaItemRequest {
  title: string
  originalTitle?: string
  cover: string
  type: string
  creator: string
  genre?: string[]
  rating?: number
  status: string
  dateAdded?: string
  dateWatched?: string
  review?: string
  tags?: string[]
  doubanUrl?: string
  imdbUrl?: string
  malUrl?: string
}

// 用戶資料相關類型定義
export interface UserProfile {
  id: number
  name: string
  avatar: string
  title: string
  bio?: string
  email?: string
  location?: string
  skills: string[]
  socialLinks: SocialLink[]
  createdAt: string
  updatedAt: string
}

export interface UserProfileRequest {
  name: string
  avatar: string
  title: string
  bio?: string
  email?: string
  location?: string
  skills?: string[]
  socialLinks?: SocialLinkRequest[]
}

export interface SocialLink {
  id: number
  platform: string
  url: string
  icon: string
}

export interface SocialLinkRequest {
  platform: string
  url: string
  icon: string
}

// 評論狀態枚舉
export type CommentStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

// 評論相關類型定義
export interface Comment {
  id: number
  postId: number
  author: string
  email?: string
  content: string
  createdAt: string
  approved: boolean
  parentId?: number
  // 新增字段
  status: CommentStatus
  reviewedAt?: string
  reviewedBy?: string
  ipAddress?: string
  isSpam: boolean
  spamScore?: number
  userAgent?: string
}

export interface CommentRequest {
  postId: number
  author: string
  email?: string
  content: string
  parentId?: number
  // 新增字段
  ipAddress?: string
  userAgent?: string
}

// 政治觀點相關類型
export type ViewType = 'CORE_VALUE' | 'TEST_RESULT'

export interface PoliticalView {
  id: number
  type: ViewType
  title: string
  description?: string
  icon?: string
  displayOrder: number
  isActive: boolean
  position?: string
  details?: string
  testDate?: string
  createdAt: string
  updatedAt: string
}

export interface PoliticalViewRequest {
  type: ViewType
  title: string
  description?: string
  icon?: string
  displayOrder?: number
  isActive?: boolean
  position?: string
  details?: string
  testDate?: string
}

// 政治觀點訪問權限相關接口
export interface PoliticalViewAccess {
  id: number
  name: string
  description?: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface PoliticalViewAccessRequest {
  name: string
  description?: string
  isActive?: boolean
}

// 政治觀點相關API
export const politicalApi = {
  // 獲取所有政治觀點
  getAllViews: () => apiClient.get<ApiResponse<PoliticalView[]>>('/political'),
  
  // 獲取活躍的政治觀點
  getActiveViews: () => apiClient.get<ApiResponse<PoliticalView[]>>('/political/active'),
  
  // 根據ID獲取政治觀點
  getViewById: (id: number) => apiClient.get<ApiResponse<PoliticalView>>(`/political/${id}`),
  
  // 根據類型獲取政治觀點
  getViewsByType: (type: ViewType) => apiClient.get<ApiResponse<PoliticalView[]>>(`/political/type/${type}`),
  
  // 獲取核心價值觀
  getCoreValues: () => apiClient.get<ApiResponse<PoliticalView[]>>('/political/core-values'),
  
  // 獲取政治測試結果
  getTestResults: () => apiClient.get<ApiResponse<PoliticalView[]>>('/political/test-results'),
  
  // 創建政治觀點
  createView: (viewData: PoliticalViewRequest) => apiClient.post<ApiResponse<PoliticalView>>('/political', viewData),
  
  // 更新政治觀點
  updateView: (id: number, viewData: PoliticalViewRequest) => apiClient.put<ApiResponse<PoliticalView>>(`/political/${id}`, viewData),
  
  // 刪除政治觀點
  deleteView: (id: number) => apiClient.delete<ApiResponse<void>>(`/political/${id}`),
  
  // 切換激活狀態
  toggleActive: (id: number) => apiClient.put<ApiResponse<PoliticalView>>(`/political/${id}/toggle-active`),
  
  // 更新顯示順序
  updateDisplayOrder: (id: number, displayOrder: number) => apiClient.put<ApiResponse<PoliticalView>>(`/political/${id}/display-order?displayOrder=${displayOrder}`)
}

export default { blogApi, mediaApi, profileApi, authApi, commentApi, politicalApi } 