# 个人网站 (PersonalWeb)

一个现代化的个人网站项目，包含完整的前后端实现，支持博客管理、媒体库、政治观点管理、评论系统和访问权限控制。

## 🎯 项目特色

- **现代化技术栈**: Vue 3 + TypeScript + Spring Boot
- **完全响应式**: 适配桌面端和移动端
- **博客系统**: 支持文章管理、分类、标签和搜索
- **媒体库管理**: 支持书籍、电影、电视剧、动漫的管理
- **政治观点管理**: 支持核心价值观和政治测试结果展示
- **访问权限控制**: 政治观点页面的姓名验证访问控制
- **评论系统**: 支持用户评论和审核机制
- **后台管理**: 完整的后台管理系统
- **实时数据**: 前后端实时数据同步

## 🛠️ 技术架构

### 前端技术栈
- **框架**: Vue 3 + Composition API
- **语言**: TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router
- **UI框架**: Tailwind CSS
- **图标**: Lucide Vue
- **HTTP客户端**: Axios

### 后端技术栈
- **框架**: Spring Boot 3.5.3
- **语言**: Java 21
- **数据库**: H2 Database (内存)
- **ORM**: Spring Data JPA
- **安全**: Spring Security
- **构建工具**: Maven
- **API设计**: RESTful

## 🚀 快速开始

### 环境要求
- **Java**: 21 或更高版本
- **Node.js**: 18 或更高版本
- **Maven**: 3.6 或更高版本

### 一键启动 (推荐)

```bash
# 克隆项目
git clone <your-repo-url>
cd personalWeb

# 一键启动开发环境
./start-dev.sh
```

脚本会自动：
- 检查环境依赖
- 安装前端依赖
- 启动后端服务 (http://localhost:8080)
- 启动前端服务 (http://localhost:5173)

### 手动启动

#### 启动后端
```bash
cd backend
./mvnw spring-boot:run
```

#### 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 停止服务

```bash
# 停止所有服务
./stop-dev.sh
```

## 🔗 访问地址

启动成功后，可以访问以下地址：

- **🏠 前端网站**: http://localhost:5173
- **🔧 后端API**: http://localhost:8080/api
- **💾 数据库控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - 用户名: `sa`
  - 密码: `password`

## 🔐 默认密码

- **博客管理密码**: `blog123`
- **政治倾向页面密码**: `political123`

可在 `backend/src/main/resources/application.properties` 中修改：
```properties
app.blog.admin.password=your_blog_password
app.political.password=your_political_password
```

## 📱 功能展示

### 🏠 首页
- 响应式设计的欢迎页面
- 导航菜单和个人简介

### 📝 博客系统
- **文章列表**: 支持分页、分类筛选、标签筛选
- **文章搜索**: 全文搜索功能
- **文章详情**: 文章内容展示和浏览统计
- **文章管理**: 密码保护的管理界面
- **文章编辑**: 支持创建、编辑、删除文章
- **状态管理**: 草稿和发布状态

### 📚 媒体库系统
- **媒体展示**: 支持书籍、电影、电视剧、动漫
- **媒体管理**: 添加、编辑、删除媒体项目
- **状态跟踪**: 想看、观看中、已完成状态
- **评分系统**: 五星评分机制
- **分类管理**: 按类型、状态、分类筛选
- **搜索功能**: 标题、创作者、分类搜索
- **详情页面**: 完整的媒体信息展示
- **相关推荐**: 基于类型和分类的推荐

### 🗳️ 政治观点系统
- **核心价值观**: 展示个人核心价值观
- **政治测试结果**: 政治倾向测试结果
- **访问权限控制**: 基于姓名的访问验证
- **权限管理**: 后台管理准入用户名单
- **增删改查**: 完整的权限管理功能

### 💬 评论系统
- **用户评论**: 支持匿名评论
- **评论审核**: 管理员审核机制
- **批量操作**: 批量审核和删除
- **状态管理**: 待审核、已通过、已拒绝状态
- **搜索筛选**: 按内容、作者、状态筛选
- **统计功能**: 评论数量、通过率统计

### 🔧 后台管理系统
- **数据总览**: 文章、媒体、评论统计
- **博客管理**: 文章增删改查
- **媒体库管理**: 媒体项目增删改查
- **个人资料管理**: 个人信息编辑
- **政治观点管理**: 观点内容和访问权限管理
- **评论管理**: 评论审核和管理
- **快速操作**: 常用功能快捷入口

## 🔧 API 文档

### 博客API
```
GET    /api/blog/posts              # 获取已发布文章
GET    /api/blog/posts/page         # 分页获取文章
GET    /api/blog/posts/{id}         # 获取单篇文章
POST   /api/blog/posts              # 创建文章
PUT    /api/blog/posts/{id}         # 更新文章
DELETE /api/blog/posts/{id}         # 删除文章
GET    /api/blog/categories         # 获取分类列表
GET    /api/blog/tags               # 获取标签列表
GET    /api/blog/posts/search       # 搜索文章
```

### 媒体库API
```
GET    /api/media                   # 获取所有媒体项目
GET    /api/media/{id}              # 获取单个媒体项目
POST   /api/media                   # 创建媒体项目
PUT    /api/media/{id}              # 更新媒体项目
DELETE /api/media/{id}              # 删除媒体项目
GET    /api/media/types             # 获取媒体类型
GET    /api/media/genres            # 获取分类列表
GET    /api/media/search            # 搜索媒体项目
```

### 政治观点API
```
GET    /api/political/views         # 获取政治观点
GET    /api/political/views/active  # 获取活跃观点
GET    /api/political/core-values   # 获取核心价值观
GET    /api/political/test-results  # 获取测试结果
POST   /api/political/views         # 创建政治观点
PUT    /api/political/views/{id}    # 更新政治观点
DELETE /api/political/views/{id}    # 删除政治观点
PUT    /api/political/views/{id}/toggle # 切换活跃状态
```

### 政治观点访问权限API
```
GET    /api/political/access        # 获取访问权限列表
POST   /api/political/access        # 创建访问权限
PUT    /api/political/access/{id}   # 更新访问权限
DELETE /api/political/access/{id}   # 删除访问权限
PUT    /api/political/access/{id}/toggle # 切换活跃状态
POST   /api/political/access/validate # 验证访问权限
```

### 认证API
```
POST   /api/auth/blog/validate      # 验证博客管理密码
POST   /api/auth/political/validate # 验证政治倾向页面密码
```

### 评论API
```
GET    /api/comments/post/{postId}  # 获取文章评论
GET    /api/comments/admin/all      # 获取所有评论(管理员)
GET    /api/comments/admin/pending  # 获取待审核评论
GET    /api/comments/admin/statistics # 获取评论统计
POST   /api/comments                # 提交评论
PUT    /api/comments/{id}/review    # 审核评论
DELETE /api/comments/{id}           # 删除评论
POST   /api/comments/batch-review   # 批量审核评论
POST   /api/comments/batch-delete   # 批量删除评论
```

### 用户资料API
```
GET    /api/user/profile            # 获取用户资料
PUT    /api/user/profile            # 更新用户资料
```

详细API文档请参考：[后端API文档](backend/README.md)

## 📂 项目结构

```
personalWeb/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── components/      # Vue组件
│   │   │   ├── icons/      # 图标组件
│   │   │   └── ...         # 其他组件
│   │   ├── views/          # 页面视图
│   │   │   ├── blog/       # 博客相关页面
│   │   │   ├── media/      # 媒体库相关页面
│   │   │   └── ...         # 其他页面
│   │   ├── stores/         # Pinia状态管理
│   │   │   ├── auth.ts     # 认证状态
│   │   │   ├── blog.ts     # 博客状态
│   │   │   ├── media.ts    # 媒体库状态
│   │   │   ├── political.ts # 政治观点状态
│   │   │   ├── comment.ts  # 评论状态
│   │   │   └── user.ts     # 用户状态
│   │   ├── services/       # API服务
│   │   │   └── api.ts      # API接口定义
│   │   ├── router/         # 路由配置
│   │   └── assets/         # 静态资源
│   ├── package.json
│   └── vite.config.ts
├── backend/                  # 后端项目
│   ├── src/main/java/com/luohengxu/backend/
│   │   ├── controller/     # REST控制器
│   │   │   ├── AuthController.java
│   │   │   ├── BlogController.java
│   │   │   ├── MediaController.java
│   │   │   ├── PoliticalViewController.java
│   │   │   ├── PoliticalViewAccessController.java
│   │   │   ├── CommentController.java
│   │   │   └── UserProfileController.java
│   │   ├── service/        # 业务逻辑
│   │   │   ├── AuthService.java
│   │   │   ├── BlogService.java
│   │   │   ├── MediaService.java
│   │   │   ├── PoliticalViewService.java
│   │   │   ├── PoliticalViewAccessService.java
│   │   │   ├── CommentService.java
│   │   │   └── UserProfileService.java
│   │   ├── repository/     # 数据访问
│   │   │   ├── BlogPostRepository.java
│   │   │   ├── MediaItemRepository.java
│   │   │   ├── PoliticalViewRepository.java
│   │   │   ├── PoliticalViewAccessRepository.java
│   │   │   ├── CommentRepository.java
│   │   │   └── UserProfileRepository.java
│   │   ├── entity/         # 数据实体
│   │   │   ├── BlogPost.java
│   │   │   ├── MediaItem.java
│   │   │   ├── PoliticalView.java
│   │   │   ├── PoliticalViewAccess.java
│   │   │   ├── Comment.java
│   │   │   └── UserProfile.java
│   │   ├── dto/           # 数据传输对象
│   │   │   ├── BlogPostRequest.java
│   │   │   ├── MediaItemRequest.java
│   │   │   ├── PoliticalViewRequest.java
│   │   │   ├── PoliticalViewAccessRequest.java
│   │   │   ├── CommentRequest.java
│   │   │   └── UserProfileRequest.java
│   │   └── config/        # 配置类
│   │       ├── SecurityConfig.java
│   │       └── DataInitializer.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── README.md           # 后端详细文档
├── start-dev.sh             # 开发环境启动脚本
├── stop-dev.sh              # 开发环境停止脚本
├── logs/                    # 日志目录
├── .gitignore
└── README.md               # 本文档
```

## 🔄 前后端集成

### 状态管理
- **Blog Store**: 管理博客文章数据
- **Media Store**: 管理媒体库数据
- **Political Store**: 管理政治观点数据
- **Comment Store**: 管理评论数据
- **Auth Store**: 管理认证状态
- **User Store**: 管理用户信息

### API集成
- 使用Axios进行HTTP请求
- 统一的错误处理
- 响应数据格式化
- 自动重试机制
- 图片加载失败处理

### 数据流
```
Frontend (Vue) <-> API Service (Axios) <-> Backend (Spring Boot) <-> Database (H2)
```

## 🎨 设计特色

### 响应式设计
- 移动端优先设计
- Tailwind CSS响应式工具类
- 流畅的动画过渡
- 自适应布局

### 用户体验
- 加载状态提示
- 错误信息显示
- 搜索防抖
- 无限滚动支持
- 图片加载优化

### 视觉设计
- 现代化卡片式布局
- 柔和的色彩搭配
- 清晰的信息层次
- 一致的设计语言

## 🚧 开发指南

### 前端开发
```bash
cd frontend
npm install
npm run dev        # 开发服务器
npm run build      # 生产构建
npm run preview    # 预览构建结果
```

### 后端开发
```bash
cd backend
./mvnw spring-boot:run  # 开发模式
./mvnw clean package    # 打包应用
```

### 新增功能
1. **前端新页面**: 在 `frontend/src/views/` 添加Vue组件
2. **后端新API**: 在 `backend/src/main/java/.../controller/` 添加控制器
3. **数据模型**: 在 `backend/src/main/java/.../entity/` 添加实体类
4. **状态管理**: 在 `frontend/src/stores/` 添加Pinia store

## 📝 部署指南

### 生产环境部署

#### 后端部署
1. 修改 `application.properties` 使用生产数据库
2. 构建JAR包：`./mvnw clean package`
3. 运行：`java -jar target/backend-0.0.1-SNAPSHOT.jar`

#### 前端部署
1. 构建生产版本：`npm run build`
2. 将 `dist/` 目录部署到Web服务器
3. 配置代理转发API请求到后端

### Docker部署 (可选)
```dockerfile
# 后端Dockerfile示例
FROM openjdk:21-jdk-slim
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔧 配置说明

### 环境变量
可以通过环境变量覆盖配置：
```bash
export SERVER_PORT=8080
export BLOG_ADMIN_PASSWORD=your_password
export POLITICAL_PASSWORD=your_password
```

### 数据库配置
开发环境使用H2内存数据库，生产环境建议使用：
- PostgreSQL
- MySQL
- MariaDB

## 🐛 故障排除

### 常见问题

1. **端口被占用**
   ```bash
   # 查找占用进程
   lsof -i :8080
   lsof -i :5173
   
   # 终止进程
   kill -9 <PID>
   ```

2. **Java版本问题**
   ```bash
   # 检查Java版本
   java -version
   
   # macOS使用homebrew安装Java 21
   brew install openjdk@21
   ```

3. **Node.js版本问题**
   ```bash
   # 检查Node版本
   node -v
   
   # 使用nvm管理Node版本
   nvm install 18
   nvm use 18
   ```

4. **图片加载失败**
   - 检查图片URL是否有效
   - 确认网络连接正常
   - 查看浏览器控制台错误信息

### 日志查看
- 后端日志：`logs/backend.log`
- 前端日志：`logs/frontend.log`
- Spring Boot内置日志：控制台输出

## 🚀 性能优化

### 前端优化
- **代码分割**: 路由级别的懒加载
- **资源压缩**: Vite自动压缩
- **缓存策略**: 浏览器缓存和CDN
- **图片优化**: 图片加载失败处理
- **组件优化**: 按需加载组件

### 后端优化
- **数据库索引**: 为常用查询添加索引
- **连接池**: HikariCP连接池优化
- **缓存**: Redis缓存热点数据
- **分页查询**: 大数据量分页处理

## 🔐 安全考虑

### 前端安全
- **XSS防护**: Vue自动转义
- **CSRF防护**: CSRF令牌
- **内容安全策略**: CSP头部
- **输入验证**: 前端表单验证

### 后端安全
- **密码安全**: BCrypt加密
- **SQL注入防护**: JPA参数化查询
- **CORS配置**: 限制允许的来源域名
- **访问控制**: 基于角色的权限控制

## 📊 监控和分析

### 推荐工具
- **前端**: Vue DevTools, Lighthouse
- **后端**: Spring Boot Actuator, Micrometer
- **数据库**: H2 Console (开发), pgAdmin (生产)

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 📄 许可证

此项目仅供个人学习和使用。

## 🙋 支持与反馈

如有问题或建议：
1. 查看[常见问题](#故障排除)
2. 检查项目日志
3. 提交Issue或联系开发者

---

**享受构建你的个人网站吧！** 🎉
