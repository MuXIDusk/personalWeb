# 個人網站 API 文檔

## 基本信息

- **後端地址**: http://localhost:8080
- **前端地址**: http://localhost:5173
- **API基礎路徑**: http://localhost:8080/api

## 認證

### 博客管理密碼驗證
```http
POST /api/auth/blog/validate
Content-Type: application/json

{
  "password": "blog123"
}
```

**響應示例:**
```json
{
  "success": true,
  "message": "密碼驗證成功",
  "data": true
}
```

### 政治觀點密碼驗證
```http
POST /api/auth/political/validate
Content-Type: application/json

{
  "password": "political123"
}
```

## 博客 API

### 獲取所有博客文章
```http
GET /api/blog/posts
```

**查詢參數:**
- `page` (int): 頁碼，從0開始
- `size` (int): 每頁大小
- `category` (string): 分類篩選
- `tag` (string): 標籤篩選
- `search` (string): 搜索關鍵詞
- `status` (string): 狀態篩選 (PUBLISHED, DRAFT)

**響應示例:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "我的第一篇博客",
      "content": "這是博客內容...",
      "excerpt": "這是摘要",
      "coverImage": null,
      "category": "生活",
      "tags": ["日常", "隨筆"],
      "status": "PUBLISHED",
      "createdAt": "2025-01-17T14:06:22.379",
      "updatedAt": "2025-01-17T14:06:22.379",
      "publishedAt": "2025-01-17T14:06:22.379",
      "viewCount": 0,
      "author": "羅恆旭"
    }
  ]
}
```

### 獲取單篇博客文章
```http
GET /api/blog/posts/{id}
```

### 創建博客文章
```http
POST /api/blog/posts
Content-Type: application/json

{
  "title": "文章標題",
  "content": "文章內容",
  "excerpt": "文章摘要",
  "coverImage": "封面圖片URL",
  "category": "分類",
  "tags": ["標籤1", "標籤2"],
  "status": "PUBLISHED"
}
```

### 更新博客文章
```http
PUT /api/blog/posts/{id}
Content-Type: application/json

{
  "title": "更新後的標題",
  "content": "更新後的內容",
  "excerpt": "更新後的摘要",
  "category": "新分類",
  "tags": ["新標籤"],
  "status": "PUBLISHED"
}
```

### 刪除博客文章
```http
DELETE /api/blog/posts/{id}
```

### 獲取博客統計信息
```http
GET /api/blog/stats
```

**響應示例:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "totalPosts": 2,
    "publishedPosts": 2,
    "draftPosts": 0,
    "totalViews": 0,
    "categories": ["生活", "技術"],
    "tags": ["日常", "隨筆", "Vue3", "Spring Boot", "架構設計"]
  }
}
```

## 媒體庫 API

### 獲取所有媒體項目
```http
GET /api/media
```

**查詢參數:**
- `page` (int): 頁碼
- `size` (int): 每頁大小
- `type` (string): 類型篩選 (BOOK, MOVIE, TV, ANIME)
- `status` (string): 狀態篩選 (COMPLETED, WATCHING, WANT_TO_WATCH)
- `search` (string): 搜索關鍵詞

**響應示例:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "進擊的巨人",
      "originalTitle": "進撃の巨人",
      "type": "ANIME",
      "creator": "諫山創",
      "cover": "https://example.com/cover.jpg",
      "genres": ["動作", "劇情"],
      "tags": ["熱血", "戰鬥"],
      "status": "COMPLETED",
      "rating": 9.5,
      "review": "非常精彩的作品...",
      "dateAdded": "2025-01-17",
      "dateWatched": "2025-01-16",
      "doubanUrl": "https://douban.com/...",
      "imdbUrl": "https://imdb.com/...",
      "malUrl": "https://myanimelist.net/...",
      "createdAt": "2025-01-17T14:11:42.045",
      "updatedAt": "2025-01-17T14:11:42.045"
    }
  ]
}
```

### 獲取單個媒體項目
```http
GET /api/media/{id}
```

### 創建媒體項目
```http
POST /api/media
Content-Type: application/json

{
  "title": "媒體標題",
  "originalTitle": "原始標題",
  "type": "ANIME",
  "creator": "創作者",
  "cover": "封面URL",
  "genres": ["類型1", "類型2"],
  "tags": ["標籤1", "標籤2"],
  "status": "COMPLETED",
  "rating": 9.0,
  "review": "評價內容",
  "dateAdded": "2025-01-17",
  "dateWatched": "2025-01-16",
  "doubanUrl": "豆瓣鏈接",
  "imdbUrl": "IMDB鏈接",
  "malUrl": "MAL鏈接"
}
```

### 更新媒體項目
```http
PUT /api/media/{id}
Content-Type: application/json
```

### 刪除媒體項目
```http
DELETE /api/media/{id}
```

### 獲取媒體統計信息
```http
GET /api/media/stats
```

## 用戶資料 API

### 獲取用戶資料
```http
GET /api/profile
```

**響應示例:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "羅恆旭",
    "avatar": "/src/assets/logo.svg",
    "title": "研究生 & 半個程序員",
    "bio": "歡迎來到我的個人網站！我是一名對技術充滿熱情的研究生...",
    "email": "luohengxu@163.com",
    "location": "北京",
    "skills": ["Vue.js", "Java", "Spring Boot", "Python"],
    "socialLinks": [
      {
        "id": 1,
        "platform": "GitHub",
        "url": "https://github.com/luohengxu",
        "icon": "github"
      },
      {
        "id": 2,
        "platform": "Email",
        "url": "mailto:luohengxu@163.com",
        "icon": "email"
      }
    ],
    "createdAt": "2025-01-17T14:11:42.033",
    "updatedAt": "2025-01-17T14:11:42.033"
  }
}
```

### 更新用戶資料
```http
PUT /api/profile
Content-Type: application/json

{
  "name": "新名字",
  "avatar": "新頭像URL",
  "title": "新標題",
  "bio": "新簡介",
  "email": "新郵箱",
  "location": "新位置",
  "skills": ["技能1", "技能2"],
  "socialLinks": [
    {
      "platform": "GitHub",
      "url": "https://github.com/username",
      "icon": "github"
    }
  ]
}
```

## 評論 API

### 獲取文章評論
```http
GET /api/comments/post/{postId}
```

### 創建評論
```http
POST /api/comments
Content-Type: application/json

{
  "postId": 1,
  "author": "評論者",
  "email": "email@example.com",
  "content": "評論內容",
  "parentId": null
}
```

### 審核評論
```http
PUT /api/comments/{id}/approve
```

### 刪除評論
```http
DELETE /api/comments/{id}
```

## 錯誤響應格式

所有API錯誤都遵循統一格式：

```json
{
  "success": false,
  "message": "錯誤描述",
  "data": null
}
```

## HTTP狀態碼

- `200` - 成功
- `400` - 客戶端錯誤（參數錯誤等）
- `401` - 未授權
- `403` - 禁止訪問
- `404` - 資源不存在
- `500` - 服務器內部錯誤

## 數據庫控制台

可以通過以下地址訪問H2數據庫控制台：
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **用戶名**: sa
- **密碼**: (空) 