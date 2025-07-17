import { defineStore } from 'pinia'
import { ref } from 'vue'
import { profileApi, type UserProfile as ApiUserProfile, type UserProfileRequest } from '@/services/api'

export interface SocialLink {
  id: number
  platform: string
  url: string
  icon: string
}

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

export const useUserStore = defineStore('user', () => {
  const profile = ref<UserProfile | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 獲取用戶資料
  const fetchProfile = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await profileApi.getUserProfile()
      profile.value = response.data.data
    } catch (err: any) {
      if (err.response?.status === 404) {
        // 用戶資料不存在，可以創建默認資料
        profile.value = null
      } else {
        error.value = '獲取用戶資料失敗'
        console.error('Error fetching profile:', err)
      }
    } finally {
      loading.value = false
    }
  }

  // 更新用戶資料
  const updateProfile = async (updates: Partial<UserProfile>) => {
    try {
      loading.value = true
      error.value = null

      const request: UserProfileRequest = {
        name: updates.name || profile.value?.name || '',
        avatar: updates.avatar || profile.value?.avatar || '',
        title: updates.title || profile.value?.title || '',
        bio: updates.bio !== undefined ? updates.bio : profile.value?.bio,
        email: updates.email !== undefined ? updates.email : profile.value?.email,
        location: updates.location !== undefined ? updates.location : profile.value?.location,
        skills: updates.skills || profile.value?.skills || [],
        socialLinks: updates.socialLinks?.map(link => ({
          platform: link.platform,
          url: link.url,
          icon: link.icon
        })) || profile.value?.socialLinks?.map(link => ({
          platform: link.platform,
          url: link.url,
          icon: link.icon
        })) || []
      }

      const response = await profileApi.updateMainUserProfile(request)
      profile.value = response.data.data
      return response.data.data
    } catch (err) {
      error.value = '更新用戶資料失敗'
      console.error('Error updating profile:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 創建用戶資料
  const createProfile = async (profileData: UserProfileRequest) => {
    try {
      loading.value = true
      error.value = null
      const response = await profileApi.createUserProfile(profileData)
      profile.value = response.data.data
      return response.data.data
    } catch (err) {
      error.value = '創建用戶資料失敗'
      console.error('Error creating profile:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 檢查郵箱是否存在
  const checkEmailExists = async (email: string) => {
    try {
      const response = await profileApi.checkEmailExists(email)
      return response.data.data
    } catch (err) {
      console.error('Error checking email:', err)
      return false
    }
  }

  // 獲取所有技能
  const getAllSkills = async () => {
    try {
      const response = await profileApi.getAllSkills()
      return response.data.data
    } catch (err) {
      console.error('Error fetching skills:', err)
      return []
    }
  }

  // 初始化數據
  const initialize = async () => {
    await fetchProfile()
  }

  return {
    profile,
    loading,
    error,
    fetchProfile,
    updateProfile,
    createProfile,
    checkEmailExists,
    getAllSkills,
    initialize
  }
}) 