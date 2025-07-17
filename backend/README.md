# 個人網站後端服務

這是一個基於Spring Boot的個人網站後端服務，提供博客管理、密碼驗證和評論系統等功能。

## 📋 功能特性

### 🔐 認證系統
- 博客管理頁面密碼保護
- 政治傾向頁面密碼保護
- 基於配置文件的密碼管理

### 📝 博客管理
- 文章的CRUD操作（創建、讀取、更新、刪除）
- 文章狀態管理（草稿/已發佈）
- 分類和標籤系統
- 全文搜索功能
- 瀏覽次數統計
- 分頁查詢支持

### 💬 評論系統
- 用戶評論提交
- 評論審核機制
- 評論管理（批准/刪除）
- 回覆評論支持

### 🗄️ 數據持久化
- H2內存數據庫（開發環境）
- JPA/Hibernate ORM映射
- 自動數據庫初始化
- 示例數據預載入

## 🛠️ 技術棧

- **框架**: Spring Boot 3.5.3
- **Java版本**: 21
- **數據庫**: H2 Database (內存)
- **ORM**: Spring Data JPA
- **安全**: Spring Security
- **構建工具**: Maven
- **API文檔**: RESTful API

## 🚀 快速開始

### 環境要求
- Java 21 或更高版本
- Maven 3.6 或更高版本

### 啟動應用

1. **編譯項目**
   ```bash
   ./mvnw clean compile
   ```

2. **運行應用**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **驗證啟動**
   - 應用將在 `http://localhost:8080` 啟動
   - 訪問 `http://localhost:8080/api/blog/posts` 驗證API正常工作

### 數據庫訪問

H2數據庫控制台：
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- 用戶名: `sa`
- 密碼: `password`

## 📚 API文檔

### 基礎配置
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **響應格式**: 統一的ApiResponse包裝

### 響應格式
所有API響應都遵循統一格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {...}
}
```

### 🔐 認證API

#### 驗證博客管理密碼
```http
POST /api/auth/blog/validate
Content-Type: application/json

{
  "password": "blog123"
}
```

#### 驗證政治傾向頁面密碼
```http
POST /api/auth/political/validate
Content-Type: application/json

{
  "password": "political123"
}
```

### 📝 博客文章API

#### 獲取已發佈文章列表
```http
GET /api/blog/posts
```

#### 分頁獲取文章
```http
GET /api/blog/posts/page?page=0&size=10
```

#### 獲取所有文章（包括草稿）
```http
GET /api/blog/posts/all
```

#### 獲取單篇文章
```http
GET /api/blog/posts/{id}
```
> 注意：此API會自動增加文章的瀏覽次數

#### 按分類獲取文章
```http
GET /api/blog/posts/category/{category}
```

#### 按標籤獲取文章
```http
GET /api/blog/posts/tag/{tag}
```

#### 搜索文章
```http
GET /api/blog/posts/search?q={關鍵詞}
```

#### 創建新文章
```http
POST /api/blog/posts
Content-Type: application/json

{
  "title": "文章標題",
  "content": "文章內容",
  "excerpt": "文章摘要",
  "coverImage": "封面圖片URL（可選）",
  "category": "分類",
  "tags": ["標籤1", "標籤2"],
  "status": "PUBLISHED",
  "author": "作者名稱"
}
```

#### 更新文章
```http
PUT /api/blog/posts/{id}
Content-Type: application/json

{
  "title": "更新的標題",
  "content": "更新的內容",
  // ... 其他字段
}
```

#### 刪除文章
```http
DELETE /api/blog/posts/{id}
```

#### 獲取分類列表
```http
GET /api/blog/categories
```

#### 獲取標籤列表
```http
GET /api/blog/tags
```

### 💬 評論API

#### 獲取文章評論
```http
GET /api/comments/post/{postId}
```

#### 獲取文章所有評論（包括未審核）
```http
GET /api/comments/post/{postId}/all
```

#### 獲取待審核評論
```http
GET /api/comments/pending
```

#### 提交評論
```http
POST /api/comments
Content-Type: application/json

{
  "postId": 1,
  "author": "評論者姓名",
  "email": "email@example.com",
  "content": "評論內容",
  "parentId": null
}
```

#### 審核評論
```http
PUT /api/comments/{id}/approve
```

#### 刪除評論
```http
DELETE /api/comments/{id}
```

#### 獲取文章評論數量
```http
GET /api/comments/post/{postId}/count
```

## ⚙️ 配置說明

### 應用配置文件 (`application.properties`)

```properties
# 應用基本配置
spring.application.name=backend
server.port=8080

# 數據庫配置
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password

# JPA配置
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2控制台
spring.h2.console.enabled=true

# 自定義密碼配置
app.blog.admin.password=blog123
app.political.password=political123

# 文件上傳配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 修改密碼
要修改管理密碼，編輯 `application.properties` 文件：
```properties
# 博客管理密碼
app.blog.admin.password=your_new_blog_password

# 政治傾向頁面密碼
app.political.password=your_new_political_password
```

## 🗂️ 項目結構

```
backend/
├── src/main/java/com/luohengxu/backend/
│   ├── controller/          # REST API控制器
│   │   ├── AuthController.java
│   │   ├── BlogController.java
│   │   └── CommentController.java
│   ├── service/            # 業務邏輯服務
│   │   ├── AuthService.java
│   │   ├── BlogService.java
│   │   └── CommentService.java
│   ├── repository/         # 數據訪問層
│   │   ├── BlogPostRepository.java
│   │   └── CommentRepository.java
│   ├── entity/            # 數據實體
│   │   ├── BlogPost.java
│   │   └── Comment.java
│   ├── dto/               # 數據傳輸對象
│   │   ├── ApiResponse.java
│   │   ├── BlogPostRequest.java
│   │   ├── BlogPostResponse.java
│   │   ├── CommentRequest.java
│   │   └── PasswordRequest.java
│   ├── config/            # 配置類
│   │   ├── SecurityConfig.java
│   │   └── DataInitializer.java
│   └── BackendApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🔧 開發指南

### 添加新的API端點

1. **創建DTO類** (可選)
   ```java
   // src/main/java/com/luohengxu/backend/dto/NewRequest.java
   public class NewRequest {
       // 字段定義
   }
   ```

2. **在Service中添加業務邏輯**
   ```java
   // src/main/java/com/luohengxu/backend/service/SomeService.java
   public ResponseType newMethod(RequestType request) {
       // 業務邏輯實現
   }
   ```

3. **在Controller中添加端點**
   ```java
   // src/main/java/com/luohengxu/backend/controller/SomeController.java
   @PostMapping("/new-endpoint")
   public ResponseEntity<ApiResponse<ResponseType>> newEndpoint(
           @Valid @RequestBody RequestType request) {
       // 調用service並返回響應
   }
   ```

### 數據庫遷移

如果需要持久化數據，修改 `application.properties`：
```properties
# 改為文件數據庫
spring.datasource.url=jdbc:h2:file:./data/blogdb
# 保留數據結構
spring.jpa.hibernate.ddl-auto=update
```

### 添加新的實體

1. **創建實體類**
   ```java
   @Entity
   @Table(name = "new_entity")
   public class NewEntity {
       // 實體字段定義
   }
   ```

2. **創建Repository接口**
   ```java
   @Repository
   public interface NewEntityRepository extends JpaRepository<NewEntity, Long> {
       // 自定義查詢方法
   }
   ```

## 🔒 安全注意事項

1. **密碼管理**
   - 生產環境中請使用環境變量而非明文密碼
   - 定期更換管理密碼

2. **CORS配置**
   - 當前配置允許所有來源，生產環境請限制特定域名

3. **數據庫**
   - H2內存數據庫僅適用於開發環境
   - 生產環境請使用PostgreSQL、MySQL等持久化數據庫

## 🧪 測試

### 手動測試API
```bash
# 獲取文章列表
curl http://localhost:8080/api/blog/posts

# 驗證密碼
curl -X POST http://localhost:8080/api/auth/blog/validate \
  -H "Content-Type: application/json" \
  -d '{"password":"blog123"}'

# 創建文章
curl -X POST http://localhost:8080/api/blog/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "測試文章",
    "content": "這是測試內容",
    "excerpt": "測試摘要",
    "category": "技術",
    "tags": ["測試"],
    "status": "PUBLISHED",
    "author": "測試作者"
  }'
```

### 單元測試
```bash
./mvnw test
```

## 📈 性能優化建議

1. **數據庫查詢優化**
   - 為常用查詢字段添加索引
   - 使用分頁避免一次加載大量數據

2. **緩存策略**
   - 對熱門文章實施緩存
   - 緩存分類和標籤列表

3. **API限流**
   - 實施請求頻率限制
   - 防止惡意請求

## 🐛 故障排除

### 常見問題

1. **應用啟動失敗**
   - 檢查Java版本是否為21+
   - 確認8080端口未被占用

2. **數據庫連接錯誤**
   - 檢查H2數據庫配置
   - 查看應用日誌獲取詳細錯誤信息

3. **API返回404**
   - 確認請求URL正確
   - 檢查跨域配置

### 日誌查看
應用日誌會顯示詳細的錯誤信息和SQL查詢，有助於問題診斷。

## 📞 支持

如果遇到問題或需要功能改進，請：
1. 查看應用日誌
2. 檢查API文檔
3. 確認配置文件設置

## �� 許可證

此項目僅供個人學習和使用。 