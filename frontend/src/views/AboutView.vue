<script setup lang="ts">
import { useUserStore } from '../stores/user'
import { Mail, MapPin, Calendar, Award, Code, Book, Heart, Coffee } from 'lucide-vue-next'

const userStore = useUserStore()

// 示例技能数据（可以后续从 store 中获取）
const skillCategories = [
  {
    title: '技术技能',
    icon: Code,
    skills: [
      { name: 'Vue.js', level: 90 },
      { name: 'TypeScript', level: 85 },
      { name: 'Node.js', level: 80 },
      { name: 'Python', level: 75 },
      { name: 'React', level: 70 }
    ]
  },
  {
    title: '设计技能',
    icon: Award,
    skills: [
      { name: 'UI/UX 设计', level: 80 },
      { name: 'Figma', level: 85 },
      { name: '用户体验', level: 75 },
      { name: '原型设计', level: 70 }
    ]
  }
]

const interests = [
  { name: '阅读', icon: Book, description: '喜欢阅读各类书籍，特别是科幻和哲学类' },
  { name: '电影', icon: Heart, description: '电影爱好者，偏爱艺术片和独立电影' },
  { name: '咖啡', icon: Coffee, description: '咖啡爱好者，喜欢研究不同的冲泡方法' }
]

const timeline = [
  {
    year: '2024',
    title: '创建个人网站',
    description: '使用 Vue 3 和 TypeScript 建立了这个个人网站平台'
  },
  {
    year: '2023',
    title: '职业发展',
    description: '在技术领域取得了重要进展，学习了新的框架和技术'
  },
  {
    year: '2022',
    title: '开始博客写作',
    description: '开始定期分享技术文章和个人思考'
  }
]
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 页面标题 -->
    <section class="bg-white py-16">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h1 class="text-4xl md:text-5xl font-bold text-gray-800 mb-4">关于我</h1>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto">
            了解更多关于我的背景、技能和兴趣
          </p>
        </div>
      </div>
    </section>

    <!-- 个人资料区域 -->
    <section class="py-16">
      <div class="container mx-auto px-4">
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
          <div class="md:flex">
            <!-- 左侧：头像和基本信息 -->
            <div class="md:w-1/3 bg-gradient-to-br from-blue-500 to-purple-600 p-8 text-white">
              <div class="text-center">
                <img
                  :src="userStore.profile.avatar"
                  :alt="userStore.profile.name"
                  class="w-32 h-32 rounded-full mx-auto mb-6 border-4 border-white shadow-lg"
                />
                <h2 class="text-2xl font-bold mb-2">{{ userStore.profile.name }}</h2>
                <p class="text-blue-100 mb-6">{{ userStore.profile.title }}</p>
                
                <!-- 联系信息 -->
                <div class="space-y-3 text-left">
                  <div v-if="userStore.profile.email" class="flex items-center">
                    <Mail class="w-4 h-4 mr-3 flex-shrink-0" />
                    <span class="text-sm">{{ userStore.profile.email }}</span>
                  </div>
                  <div v-if="userStore.profile.location" class="flex items-center">
                    <MapPin class="w-4 h-4 mr-3 flex-shrink-0" />
                    <span class="text-sm">{{ userStore.profile.location }}</span>
                  </div>
                </div>

                <!-- 社交链接 -->
                <div class="mt-6 flex justify-center space-x-4">
                  <a
                    v-for="social in userStore.profile.socialLinks"
                    :key="social.platform"
                    :href="social.url"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="w-10 h-10 bg-white/20 hover:bg-white/30 rounded-full flex items-center justify-center transition-colors duration-200"
                  >
                    <span class="text-sm">{{ social.platform.charAt(0) }}</span>
                  </a>
                </div>
              </div>
            </div>

            <!-- 右侧：详细介绍 -->
            <div class="md:w-2/3 p-8">
              <h3 class="text-2xl font-bold text-gray-800 mb-4">个人简介</h3>
              <p class="text-gray-700 leading-relaxed mb-6">
                {{ userStore.profile.bio }}
              </p>
              <p class="text-gray-700 leading-relaxed">
                我热爱技术创新，喜欢探索新的技术和解决方案。在工作中，我专注于创建用户友好的界面和高效的系统。
                除了技术工作，我也热衷于分享知识，通过博客和开源项目与社区互动。
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 技能区域 -->
    <section class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <h2 class="text-3xl font-bold text-gray-800 text-center mb-12">技能与专长</h2>
        
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div
            v-for="category in skillCategories"
            :key="category.title"
            class="bg-gray-50 rounded-lg p-6"
          >
            <div class="flex items-center mb-6">
              <component :is="category.icon" class="w-6 h-6 text-blue-600 mr-3" />
              <h3 class="text-xl font-semibold text-gray-800">{{ category.title }}</h3>
            </div>
            
            <div class="space-y-4">
              <div
                v-for="skill in category.skills"
                :key="skill.name"
                class="space-y-2"
              >
                <div class="flex justify-between items-center">
                  <span class="text-gray-700 font-medium">{{ skill.name }}</span>
                  <span class="text-gray-500 text-sm">{{ skill.level }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div
                    class="bg-blue-600 h-2 rounded-full transition-all duration-1000 ease-out"
                    :style="{ width: `${skill.level}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 兴趣爱好 -->
    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <h2 class="text-3xl font-bold text-gray-800 text-center mb-12">兴趣爱好</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div
            v-for="interest in interests"
            :key="interest.name"
            class="bg-white rounded-lg p-6 text-center shadow-md hover:shadow-lg transition-shadow duration-200"
          >
            <component :is="interest.icon" class="w-12 h-12 text-blue-600 mx-auto mb-4" />
            <h3 class="text-xl font-semibold text-gray-800 mb-3">{{ interest.name }}</h3>
            <p class="text-gray-600">{{ interest.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 时间线 -->
    <section class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <h2 class="text-3xl font-bold text-gray-800 text-center mb-12">成长历程</h2>
        
        <div class="max-w-3xl mx-auto">
          <div class="relative">
            <!-- 时间线 -->
            <div class="absolute left-4 md:left-1/2 transform md:-translate-x-1/2 w-0.5 h-full bg-blue-200"></div>
            
            <div class="space-y-8">
              <div
                v-for="(item, index) in timeline"
                :key="item.year"
                class="relative flex items-center"
                :class="index % 2 === 0 ? 'md:flex-row' : 'md:flex-row-reverse'"
              >
                <!-- 时间节点 -->
                <div class="absolute left-4 md:left-1/2 transform md:-translate-x-1/2 w-4 h-4 bg-blue-600 rounded-full border-4 border-white shadow-md z-10"></div>
                
                <!-- 内容卡片 -->
                <div
                  class="ml-12 md:ml-0 md:w-5/12 bg-gray-50 rounded-lg p-6 shadow-md"
                  :class="index % 2 === 0 ? 'md:mr-auto md:ml-0' : 'md:ml-auto md:mr-0'"
                >
                  <div class="flex items-center mb-2">
                    <Calendar class="w-4 h-4 text-blue-600 mr-2" />
                    <span class="text-blue-600 font-semibold">{{ item.year }}</span>
                  </div>
                  <h3 class="text-lg font-semibold text-gray-800 mb-2">{{ item.title }}</h3>
                  <p class="text-gray-600">{{ item.description }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 技能条动画 */
.bg-blue-600 {
  animation: skillProgress 2s ease-in-out;
}

@keyframes skillProgress {
  from {
    width: 0%;
  }
  to {
    width: var(--final-width);
  }
}

/* 时间线响应式调整 */
@media (max-width: 768px) {
  .absolute.left-4 {
    left: 1rem;
  }
  
  .ml-12 {
    margin-left: 3rem;
  }
}
</style> 