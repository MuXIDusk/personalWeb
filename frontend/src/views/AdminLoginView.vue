<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { Lock, Eye, EyeOff, Shield, AlertCircle } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const password = ref('')
const showPassword = ref(false)
const isLoading = ref(false)

const handleLogin = async () => {
  if (!password.value.trim()) {
    return
  }

  isLoading.value = true
  const success = await authStore.validateAdminPassword(password.value)
  
  if (success) {
    router.push('/admin')
  }
  
  isLoading.value = false
}

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4">
    <div class="max-w-md w-full space-y-8">
      <!-- Logo 和標題 -->
      <div class="text-center">
        <div class="mx-auto h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center">
          <Shield class="h-8 w-8 text-white" />
        </div>
        <h2 class="mt-6 text-3xl font-bold text-gray-900">
          管理員登入
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          請輸入管理員密碼以存取後台管理系統
        </p>
      </div>

      <!-- 登入表單 -->
      <div class="bg-white shadow-xl rounded-lg p-8">
        <form @submit.prevent="handleLogin" class="space-y-6">
          <!-- 密碼輸入 -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              管理員密碼
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock class="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                required
                class="block w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="請輸入管理員密碼"
                :disabled="isLoading"
              />
              <div class="absolute inset-y-0 right-0 pr-3 flex items-center">
                <button
                  type="button"
                  @click="togglePasswordVisibility"
                  class="text-gray-400 hover:text-gray-600 focus:outline-none"
                  :disabled="isLoading"
                >
                  <Eye v-if="!showPassword" class="h-5 w-5" />
                  <EyeOff v-else class="h-5 w-5" />
                </button>
              </div>
            </div>
          </div>

          <!-- 錯誤信息 -->
          <div v-if="authStore.error" class="flex items-center space-x-2 text-red-600 bg-red-50 p-3 rounded-lg">
            <AlertCircle class="h-5 w-5" />
            <span class="text-sm">{{ authStore.error }}</span>
          </div>

          <!-- 登入按鈕 -->
          <button
            type="submit"
            :disabled="isLoading || !password.trim()"
            class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <div v-if="isLoading" class="flex items-center">
              <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
              驗證中...
            </div>
            <span v-else>登入管理後台</span>
          </button>
        </form>

        <!-- 返回首頁 -->
        <div class="mt-6 text-center">
          <button
            @click="router.push('/')"
            class="text-sm text-blue-600 hover:text-blue-500"
          >
            返回首頁
          </button>
        </div>
      </div>

      <!-- 提示信息 -->
      <div class="text-center text-xs text-gray-500">
        <p>此頁面僅供管理員使用</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 額外的樣式可以在這裡添加 */
</style> 