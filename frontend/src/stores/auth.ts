import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const isPoliticalAuthenticated = ref(false)
  const isAdminAuthenticated = ref(false)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 驗證博客管理密碼
  const validateBlogPassword = async (password: string): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      
      const response = await authApi.validateBlogPassword(password)
      
      if (response.data.success) {
        isAuthenticated.value = true
        // 將認證狀態保存到localStorage（可選）
        localStorage.setItem('blog_auth', 'true')
        return true
      } else {
        error.value = response.data.message || '密碼驗證失敗'
        return false
      }
    } catch (err) {
      error.value = '密碼驗證失敗，請檢查網絡連接'
      console.error('Error validating blog password:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  // 驗證政治傾向頁面密碼
  const validatePoliticalPassword = async (password: string): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      
      const response = await authApi.validatePoliticalPassword(password)
      
      if (response.data.success) {
        isPoliticalAuthenticated.value = true
        // 將認證狀態保存到localStorage（可選）
        localStorage.setItem('political_auth', 'true')
        return true
      } else {
        error.value = response.data.message || '密碼驗證失敗'
        return false
      }
    } catch (err) {
      error.value = '密碼驗證失敗，請檢查網絡連接'
      console.error('Error validating political password:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  // 驗證管理員密碼
  const validateAdminPassword = async (password: string): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      
      // 暫時使用硬編碼密碼，後續可以改為API調用
      const correctPassword = 'admin123'
      
      if (password === correctPassword) {
        isAdminAuthenticated.value = true
        // 將認證狀態保存到localStorage
        localStorage.setItem('admin_auth', 'true')
        return true
      } else {
        error.value = '管理員密碼錯誤'
        return false
      }
    } catch (err) {
      error.value = '密碼驗證失敗，請檢查網絡連接'
      console.error('Error validating admin password:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  // 登出博客管理
  const logoutBlog = () => {
    isAuthenticated.value = false
    localStorage.removeItem('blog_auth')
  }

  // 登出政治傾向頁面
  const logoutPolitical = () => {
    isPoliticalAuthenticated.value = false
    localStorage.removeItem('political_auth')
  }

  // 登出管理員
  const logoutAdmin = () => {
    isAdminAuthenticated.value = false
    localStorage.removeItem('admin_auth')
  }

  // 統一登出方法（兼容舊版本）
  const logout = () => {
    logoutAdmin()
  }

  // 登出所有
  const logoutAll = () => {
    logoutBlog()
    logoutPolitical()
    logoutAdmin()
  }

  // 檢查本地存儲的認證狀態
  const checkStoredAuth = () => {
    const blogAuth = localStorage.getItem('blog_auth')
    const politicalAuth = localStorage.getItem('political_auth')
    const adminAuth = localStorage.getItem('admin_auth')
    
    if (blogAuth === 'true') {
      isAuthenticated.value = true
    }
    
    if (politicalAuth === 'true') {
      isPoliticalAuthenticated.value = true
    }

    if (adminAuth === 'true') {
      isAdminAuthenticated.value = true
    }
  }

  // 清除錯誤信息
  const clearError = () => {
    error.value = null
  }

  // 初始化時檢查認證狀態
  checkStoredAuth()

  return {
    isAuthenticated,
    isPoliticalAuthenticated,
    isAdminAuthenticated,
    loading,
    error,
    validateBlogPassword,
    validatePoliticalPassword,
    validateAdminPassword,
    logoutBlog,
    logoutPolitical,
    logoutAdmin,
    logout,
    logoutAll,
    checkStoredAuth,
    clearError
  }
}) 