# 个人网站

一个现代化的个人网站，使用 Vue 3、TypeScript 和 Tailwind CSS 构建。

## ✨ 功能特色

### 📄 个人简介
- 个人基本信息展示
- 技能进度条可视化
- 兴趣爱好介绍
- 成长历程时间线
- 联系方式和社交媒体链接

### 📚 媒体库管理
- **多媒体类型支持**：书籍、电影、电视剧、动画
- **丰富的信息展示**：封面、标题、创作者、评分、状态
- **强大的搜索筛选**：支持按类型、状态、类型、关键词搜索
- **多种查看模式**：网格视图和列表视图
- **详情页面**：完整信息展示和相关推荐

### 🗳️ 政治观点展示
- **访问控制**：密码保护敏感内容
- **价值观声明**：核心理念和立场
- **测试结果**：政治坐标和倾向分析
- **议题立场表**：具体问题的详细立场
- **免责声明**：明确个人观点性质

### ✍️ 博客系统
- **文章管理**：创建、编辑、发布文章
- **分类标签**：多维度内容组织
- **搜索功能**：全文搜索和分类筛选
- **评论系统**：互动交流平台
- **后台管理**：完整的内容管理界面

### 🎨 设计特色
- **完全响应式**：支持手机、平板、桌面设备
- **现代化UI**：简洁美观的界面设计
- **流畅动画**：优雅的过渡效果
- **直观导航**：清晰的页面结构

## 🛠️ 技术栈

- **前端框架**：Vue 3 (Composition API)
- **类型安全**：TypeScript
- **状态管理**：Pinia
- **路由管理**：Vue Router 4
- **样式框架**：Tailwind CSS v3.4.17
- **构建工具**：Vite
- **图标库**：Lucide Vue Next

## 📦 安装和使用

### 环境要求
- Node.js 16+ 
- npm 或 yarn

### 安装依赖
```bash
cd frontend
npm install
```

### 开发环境
```bash
npm run dev
```
访问 http://localhost:5173

### 生产构建
```bash
npm run build
```

### 预览构建结果
```bash
npm run preview
```

## 📁 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── assets/            # 图片、样式等资源
│   ├── components/        # 可复用组件
│   │   ├── NavigationBar.vue
│   │   └── FooterComponent.vue
│   ├── stores/            # Pinia 状态管理
│   │   ├── user.ts        # 用户信息
│   │   ├── media.ts       # 媒体库
│   │   └── blog.ts        # 博客系统
│   ├── views/             # 页面组件
│   │   ├── HomeView.vue
│   │   ├── AboutView.vue
│   │   ├── PoliticalView.vue
│   │   ├── media/         # 媒体库相关页面
│   │   └── blog/          # 博客相关页面
│   ├── router/            # 路由配置
│   ├── App.vue
│   └── main.ts
├── index.html
├── package.json
├── tailwind.config.js     # Tailwind 配置
├── tsconfig.json          # TypeScript 配置
└── vite.config.ts         # Vite 配置
```

## 🔧 配置说明

### Tailwind CSS 配置
项目使用 Tailwind CSS v3.4.17 稳定版本，配置文件位于 `tailwind.config.js`。

### TypeScript 配置
严格的 TypeScript 配置，确保代码质量和类型安全。

### 路由配置
使用 Vue Router 4，支持嵌套路由和路由守卫。

## 🎯 使用指南

### 个人信息配置
在 `src/stores/user.ts` 中修改个人信息：
```typescript
const profile = ref<UserProfile>({
  name: '您的姓名',
  title: '您的职业',
  bio: '个人简介',
  // ...其他信息
})
```

### 媒体库管理
- 在媒体库页面添加新的收藏
- 支持书籍、电影、电视剧、动画四种类型
- 可以设置评分、状态、添加评论

### 博客内容管理
- 访问 `/blog/admin` 进入管理后台
- 支持 Markdown 编辑
- 可以设置分类、标签、发布状态

### 政治观点访问
- 默认密码：`demo`
- 可在 `PoliticalView.vue` 中修改访问密码
- 内容包含个人政治立场和价值观

## 🎨 自定义样式

项目使用 Tailwind CSS，可以通过以下方式自定义：

1. 修改 `tailwind.config.js` 中的主题配置
2. 在 `src/assets/main.css` 中添加自定义样式
3. 使用 Tailwind 的工具类进行快速样式调整

## 🚀 部署

### 静态部署
构建完成后，`dist` 目录包含所有静态文件，可以部署到任何静态托管服务：
- Netlify
- Vercel
- GitHub Pages
- 传统 Web 服务器

### 环境变量
如需配置环境变量，在项目根目录创建 `.env` 文件。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 📧 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱：[你的邮箱]
- GitHub：[你的GitHub]

---

**注意**：这是一个个人网站模板，包含政治观点等个人内容。使用时请根据自己的实际情况进行修改。
