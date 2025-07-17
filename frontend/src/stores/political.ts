import { defineStore } from 'pinia'
import { ref } from 'vue'
import { politicalApi, type PoliticalView, type PoliticalViewRequest, type ViewType } from '@/services/api'

export const usePoliticalStore = defineStore('political', () => {
  const views = ref<PoliticalView[]>([])
  const coreValues = ref<PoliticalView[]>([])
  const testResults = ref<PoliticalView[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 獲取所有政治觀點
  const fetchPoliticalViews = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.getAllViews()
      views.value = response.data.data
      
      // 分類數據
      coreValues.value = views.value.filter(view => view.type === 'CORE_VALUE')
      testResults.value = views.value.filter(view => view.type === 'TEST_RESULT')
    } catch (err: any) {
      error.value = '獲取政治觀點失敗'
      console.error('Error fetching political views:', err)
    } finally {
      loading.value = false
    }
  }

  // 獲取活躍的政治觀點
  const fetchActivePoliticalViews = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.getActiveViews()
      const activeViews = response.data.data
      
      // 分類數據
      coreValues.value = activeViews.filter(view => view.type === 'CORE_VALUE')
      testResults.value = activeViews.filter(view => view.type === 'TEST_RESULT')
    } catch (err: any) {
      error.value = '獲取活躍政治觀點失敗'
      console.error('Error fetching active political views:', err)
    } finally {
      loading.value = false
    }
  }

  // 獲取核心價值觀
  const fetchCoreValues = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.getCoreValues()
      coreValues.value = response.data.data
    } catch (err: any) {
      error.value = '獲取核心價值觀失敗'
      console.error('Error fetching core values:', err)
    } finally {
      loading.value = false
    }
  }

  // 獲取政治測試結果
  const fetchTestResults = async () => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.getTestResults()
      testResults.value = response.data.data
    } catch (err: any) {
      error.value = '獲取政治測試結果失敗'
      console.error('Error fetching test results:', err)
    } finally {
      loading.value = false
    }
  }

  // 創建政治觀點
  const createPoliticalView = async (viewData: PoliticalViewRequest) => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.createView(viewData)
      const newView = response.data.data
      views.value.push(newView)
      
      // 更新分類數據
      if (newView.type === 'CORE_VALUE') {
        coreValues.value.push(newView)
      } else if (newView.type === 'TEST_RESULT') {
        testResults.value.push(newView)
      }
      
      return newView
    } catch (err: any) {
      error.value = err.response?.data?.message || '創建政治觀點失敗'
      console.error('Error creating political view:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新政治觀點
  const updatePoliticalView = async (id: number, viewData: PoliticalViewRequest) => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.updateView(id, viewData)
      const updatedView = response.data.data
      
      // 更新列表中的數據
      const index = views.value.findIndex(view => view.id === id)
      if (index !== -1) {
        views.value[index] = updatedView
      }
      
      // 更新分類數據
      const coreIndex = coreValues.value.findIndex(view => view.id === id)
      const testIndex = testResults.value.findIndex(view => view.id === id)
      
      if (coreIndex !== -1) {
        if (updatedView.type === 'CORE_VALUE') {
          coreValues.value[coreIndex] = updatedView
        } else {
          coreValues.value.splice(coreIndex, 1)
          testResults.value.push(updatedView)
        }
      } else if (testIndex !== -1) {
        if (updatedView.type === 'TEST_RESULT') {
          testResults.value[testIndex] = updatedView
        } else {
          testResults.value.splice(testIndex, 1)
          coreValues.value.push(updatedView)
        }
      }
      
      return updatedView
    } catch (err: any) {
      error.value = err.response?.data?.message || '更新政治觀點失敗'
      console.error('Error updating political view:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 刪除政治觀點
  const deletePoliticalView = async (id: number) => {
    try {
      loading.value = true
      error.value = null
      await politicalApi.deleteView(id)
      
      // 從列表中移除
      views.value = views.value.filter(view => view.id !== id)
      coreValues.value = coreValues.value.filter(view => view.id !== id)
      testResults.value = testResults.value.filter(view => view.id !== id)
    } catch (err: any) {
      error.value = err.response?.data?.message || '刪除政治觀點失敗'
      console.error('Error deleting political view:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 切換激活狀態
  const toggleActiveStatus = async (id: number) => {
    try {
      loading.value = true
      error.value = null
      const response = await politicalApi.toggleActive(id)
      const updatedView = response.data.data
      
      // 更新列表中的數據
      const index = views.value.findIndex(view => view.id === id)
      if (index !== -1) {
        views.value[index] = updatedView
      }
      
      // 更新分類數據
      const coreIndex = coreValues.value.findIndex(view => view.id === id)
      const testIndex = testResults.value.findIndex(view => view.id === id)
      
      if (coreIndex !== -1) {
        coreValues.value[coreIndex] = updatedView
      } else if (testIndex !== -1) {
        testResults.value[testIndex] = updatedView
      }
      
      return updatedView
    } catch (err: any) {
      error.value = err.response?.data?.message || '切換狀態失敗'
      console.error('Error toggling active status:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 清空數據
  const clearData = () => {
    views.value = []
    coreValues.value = []
    testResults.value = []
    error.value = null
  }

  return {
    // 狀態
    views,
    coreValues,
    testResults,
    loading,
    error,
    
    // 操作
    fetchPoliticalViews,
    fetchActivePoliticalViews,
    fetchCoreValues,
    fetchTestResults,
    createPoliticalView,
    updatePoliticalView,
    deletePoliticalView,
    toggleActiveStatus,
    clearData
  }
}) 