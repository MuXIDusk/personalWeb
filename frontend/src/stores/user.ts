import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserProfile {
  name: string
  avatar: string
  title: string
  bio: string
  email?: string
  location?: string
  skills: string[]
  socialLinks: {
    platform: string
    url: string
    icon: string
  }[]
}

export const useUserStore = defineStore('user', () => {
  const profile = ref<UserProfile>({
    name: '罗恒旭',
    avatar: '/src/assets/logo.svg',
    title: '研究生 & 半个程序员',
    bio: '這裡是您的簡短自我介紹...',
    email: 'luohengxu@163.com',
    location: '北京',
    skills: ['深度学习', '后端开发', '客户端开发'],
    socialLinks: [
      {
        platform: 'GitHub',
        url: 'https://github.com/username',
        icon: 'github'
      },
      {
        platform: 'Twitter',
        url: 'https://twitter.com/username',
        icon: 'twitter'
      }
    ]
  })

  const updateProfile = (newProfile: Partial<UserProfile>) => {
    profile.value = { ...profile.value, ...newProfile }
  }

  return {
    profile,
    updateProfile
  }
}) 