<script setup lang="ts">
import { ref } from 'vue'
import { Lock, Eye, EyeOff, AlertTriangle, Heart, Shield, Users, CheckCircle } from 'lucide-vue-next'
import { validatePoliticalAccess } from '@/services/api'

const isAuthenticated = ref(false)
const name = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const authenticate = async () => {
  if (!name.value.trim()) {
    errorMessage.value = '请输入姓名'
    setTimeout(() => {
      errorMessage.value = ''
    }, 3000)
    return
  }

  isLoading.value = true
  try {
    const response = await validatePoliticalAccess({ name: name.value.trim() })
    if (response.data.success && response.data.data) {
      isAuthenticated.value = true
      errorMessage.value = ''
    } else {
      errorMessage.value = response.data.message || '验证失败，该姓名不在访问列表中'
      setTimeout(() => {
        errorMessage.value = ''
      }, 3000)
    }
  } catch (error) {
    errorMessage.value = '验证失败，请稍后重试'
    setTimeout(() => {
      errorMessage.value = ''
    }, 3000)
  } finally {
    isLoading.value = false
  }
}

// 核心价值观
const coreValues = [
  {
    icon: Heart,
    title: '平等与包容',
    description: '支持所有人的基本权利和尊严，反对任何形式的歧视'
  },
  {
    icon: Shield,
    title: '民主与法治',
    description: '相信民主制度和法治的重要性，支持透明的治理'
  },
  {
    icon: Users,
    title: '社会责任',
    description: '关注社会公正，支持弱势群体，推动可持续发展'
  }
]

// 政治测试结果
const testResults = [
  {
    testName: '政治坐标测试',
    position: '中左倾向',
    details: '经济：偏左 (-2.5)，社会：自由主义 (-3.1)',
    date: '2024年1月'
  },
  {
    testName: '8values 测试',
    position: '社会民主主义',
    details: '平等 75%，世界 68%，自由 73%，进步 82%',
    date: '2024年1月'
  }
]

// 议题立场
const issuePositions = [
  {
    category: '经济政策',
    issues: [
      { issue: '最低工资标准', position: '支持提高', explanation: '保障基本生活水平' },
      { issue: '累进税制', position: '支持', explanation: '促进财富分配公平' },
      { issue: '社会保障', position: '强烈支持', explanation: '构建完善的社会安全网' }
    ]
  },
  {
    category: '社会议题',
    issues: [
      { issue: '性别平等', position: '强烈支持', explanation: '反对性别歧视，支持平等权利' },
      { issue: '多元化社会', position: '支持', explanation: '尊重不同文化和价值观' },
      { issue: '环境保护', position: '强烈支持', explanation: '应对气候变化刻不容缓' }
    ]
  },
  {
    category: '科技与隐私',
    issues: [
      { issue: '数据隐私保护', position: '强烈支持', explanation: '个人数据权利不可侵犯' },
      { issue: '人工智能监管', position: '支持适度监管', explanation: '平衡创新与安全' },
      { issue: '互联网自由', position: '支持', explanation: '反对过度审查和限制' }
    ]
  }
]

const getPositionColor = (position: string) => {
  if (position.includes('强烈支持')) return 'text-green-600 bg-green-100'
  if (position.includes('支持')) return 'text-blue-600 bg-blue-100'
  if (position.includes('中立')) return 'text-gray-600 bg-gray-100'
  if (position.includes('反对')) return 'text-red-600 bg-red-100'
  return 'text-purple-600 bg-purple-100'
}
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 访问控制界面 -->
    <div v-if="!isAuthenticated" class="min-h-screen flex items-center justify-center">
      <div class="max-w-md w-full mx-4">
        <div class="bg-white rounded-lg shadow-lg p-8">
          <!-- 警告图标 -->
          <div class="text-center mb-6">
            <div class="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Lock class="w-8 h-8 text-yellow-600" />
            </div>
            <h2 class="text-2xl font-bold text-gray-800 mb-2">访问受限内容</h2>
            <p class="text-gray-600">此页面包含个人政治观点，需要密码访问</p>
          </div>
          
          <!-- 免责声明 -->
          <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
            <div class="flex items-start">
              <AlertTriangle class="w-5 h-5 text-yellow-600 mt-0.5 mr-3 flex-shrink-0" />
              <div class="text-sm text-yellow-800">
                <p class="font-medium mb-1">免责声明</p>
                <p>以下内容仅代表个人观点，不构成任何政治立场建议。观点可能因时间和环境变化而调整。</p>
              </div>
            </div>
          </div>
          
          <!-- 密码输入 -->
          <div class="space-y-4">
            <div class="relative">
              <input
                v-model="name"
                placeholder="请输入您的姓名"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent pr-12"
                @keyup.enter="authenticate"
              />
              <button
                @click="authenticate"
                :disabled="isLoading"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Lock v-if="isLoading" class="w-5 h-5 animate-spin" />
                <Lock v-else class="w-5 h-5" />
              </button>
            </div>
            
            <!-- 错误信息 -->
            <div v-if="errorMessage" class="text-red-600 text-sm text-center">
              {{ errorMessage }}
            </div>
            
            <!-- 提示信息 -->
            <div class="text-center text-sm text-gray-500 mt-4">
              <p>请输入您的真实姓名进行验证</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容（认证后显示） -->
    <div v-else>
      <!-- 页面标题 -->
      <section class="bg-white py-16">
        <div class="container mx-auto px-4">
          <div class="text-center max-w-4xl mx-auto">
            <h1 class="text-4xl md:text-5xl font-bold text-gray-800 mb-6">我的政治观点</h1>
            <p class="text-xl text-gray-600 leading-relaxed">
              在这里分享我的政治立场和价值观念。这些观点反映了我对社会、经济和治理问题的思考，
              会随着时间和经验的积累而不断演进。
            </p>
          </div>
        </div>
      </section>

      <!-- 核心价值观 -->
      <section class="py-16 bg-gray-50">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-gray-800 text-center mb-12">核心价值观</h2>
          
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div
              v-for="value in coreValues"
              :key="value.title"
              class="bg-white rounded-lg p-6 shadow-md hover:shadow-lg transition-shadow duration-200"
            >
              <div class="text-center">
                <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <component :is="value.icon" class="w-8 h-8 text-blue-600" />
                </div>
                <h3 class="text-xl font-semibold text-gray-800 mb-3">{{ value.title }}</h3>
                <p class="text-gray-600 leading-relaxed">{{ value.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 政治测试结果 -->
      <section class="py-16 bg-white">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-gray-800 text-center mb-12">政治测试结果</h2>
          
          <div class="max-w-4xl mx-auto space-y-6">
            <div
              v-for="result in testResults"
              :key="result.testName"
              class="bg-gray-50 rounded-lg p-6"
            >
              <div class="flex items-start justify-between mb-4">
                <div>
                  <h3 class="text-xl font-semibold text-gray-800 mb-2">{{ result.testName }}</h3>
                  <div class="flex items-center">
                    <CheckCircle class="w-5 h-5 text-green-600 mr-2" />
                    <span class="text-lg font-medium text-green-600">{{ result.position }}</span>
                  </div>
                </div>
                <span class="text-sm text-gray-500">{{ result.date }}</span>
              </div>
              <p class="text-gray-700">{{ result.details }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 具体议题立场 -->
      <section class="py-16 bg-gray-50">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-gray-800 text-center mb-12">具体议题立场</h2>
          
          <div class="max-w-6xl mx-auto space-y-8">
            <div
              v-for="category in issuePositions"
              :key="category.category"
              class="bg-white rounded-lg shadow-md overflow-hidden"
            >
              <div class="bg-gradient-to-r from-blue-500 to-purple-600 px-6 py-4">
                <h3 class="text-xl font-semibold text-white">{{ category.category }}</h3>
              </div>
              
              <div class="p-6">
                <div class="overflow-x-auto">
                  <table class="min-w-full">
                    <thead>
                      <tr class="border-b border-gray-200">
                        <th class="text-left py-3 px-4 font-medium text-gray-700">议题</th>
                        <th class="text-left py-3 px-4 font-medium text-gray-700">立场</th>
                        <th class="text-left py-3 px-4 font-medium text-gray-700">说明</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="item in category.issues"
                        :key="item.issue"
                        class="border-b border-gray-100 hover:bg-gray-50 transition-colors duration-200"
                      >
                        <td class="py-4 px-4 font-medium text-gray-800">{{ item.issue }}</td>
                        <td class="py-4 px-4">
                          <span
                            class="px-3 py-1 rounded-full text-sm font-medium"
                            :class="getPositionColor(item.position)"
                          >
                            {{ item.position }}
                          </span>
                        </td>
                        <td class="py-4 px-4 text-gray-600">{{ item.explanation }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 重要说明 -->
      <section class="py-16 bg-white">
        <div class="container mx-auto px-4">
          <div class="max-w-4xl mx-auto">
            <div class="bg-blue-50 border border-blue-200 rounded-lg p-6">
              <div class="flex items-start">
                <AlertTriangle class="w-6 h-6 text-blue-600 mt-1 mr-4 flex-shrink-0" />
                <div>
                  <h3 class="text-lg font-semibold text-blue-800 mb-3">重要说明</h3>
                  <div class="text-blue-700 space-y-2">
                    <p>• 以上观点仅代表个人立场，不代表任何组织或团体</p>
                    <p>• 政治观点会随着时间、经验和环境的变化而演进</p>
                    <p>• 欢迎理性讨论，但请尊重不同观点</p>
                    <p>• 本页面内容不构成任何政治建议或投票指导</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* 平滑过渡效果 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

/* 表格样式优化 */
table {
  border-collapse: collapse;
}

tbody tr:last-child {
  border-bottom: none;
}

/* 响应式表格 */
@media (max-width: 768px) {
  .overflow-x-auto {
    font-size: 0.875rem;
  }
  
  table th,
  table td {
    padding: 0.75rem 0.5rem;
  }
}

/* 输入框焦点效果 */
input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 按钮悬停效果 */
button:hover {
  transform: translateY(-1px);
}

button:active {
  transform: translateY(0);
}
</style> 