# 後台管理系統使用指南

## 🚀 快速開始

### 啟動系統

1. **啟動後端**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **啟動前端**:
   ```bash
   cd frontend
   npm run dev
   ```

3. **或使用一鍵啟動腳本**:
   ```bash
   ./start-dev.sh
   ```

### 訪問地址

- **前端主頁**: http://localhost:5173
- **後台管理**: http://localhost:5173/admin
- **數據庫控制台**: http://localhost:8080/h2-console

## 🔐 管理密碼

- **博客管理密碼**: `blog123`
- **政治觀點頁面密碼**: `political123`

## 📱 後台管理功能

### 進入後台管理

1. 打開瀏覽器訪問: http://localhost:5173/admin
2. 系統會要求輸入博客管理密碼: `blog123`
3. 驗證成功後即可進入管理界面

### 管理界面概覽

後台管理系統包含以下模塊：

#### 📊 統計概覽
- 博客文章統計（總數、已發布、草稿）
- 媒體庫統計（書籍、電影、電視劇、動漫）
- 最新文章和最新媒體項目

#### 📝 博客管理
- **查看文章列表**: 顯示所有博客文章，支持分頁
- **創建新文章**: 
  - 填寫標題、內容、摘要
  - 選擇分類和標籤
  - 設置文章狀態（草稿/已發布）
  - 上傳封面圖片
- **編輯文章**: 修改現有文章的所有信息
- **刪除文章**: 永久刪除不需要的文章
- **文章搜索**: 根據標題、內容、分類、標籤搜索

#### 🎬 媒體庫管理
- **查看媒體列表**: 顯示所有媒體項目（書籍、電影、電視劇、動漫）
- **添加新媒體**:
  - 基本信息：標題、原始標題、創作者
  - 分類：選擇媒體類型（書籍/電影/電視劇/動漫）
  - 狀態：已完成/觀看中/計劃觀看
  - 評分和評論
  - 外部鏈接：豆瓣、IMDB、MAL等
- **編輯媒體**: 修改現有媒體項目信息
- **刪除媒體**: 移除不需要的媒體項目
- **篩選功能**: 按類型、狀態、評分篩選

#### 👤 個人資料管理
- **基本信息**:
  - 姓名、頭像、職業標題
  - 個人簡介、郵箱、所在地
- **技能標籤**: 添加或修改技能列表
- **社交鏈接**:
  - GitHub、Twitter、LinkedIn等
  - 支持添加自定義平台
  - 設置圖標和鏈接

### 常用操作指南

#### 創建新博客文章

1. 在後台管理頁面點擊「博客管理」
2. 點擊「新增文章」按鈕
3. 填寫必要信息：
   - **標題**: 文章標題
   - **內容**: 使用Markdown或HTML格式
   - **摘要**: 文章簡介（可選）
   - **分類**: 選擇或創建新分類
   - **標籤**: 添加相關標籤
   - **狀態**: 選擇「草稿」或「已發布」
4. 點擊「保存」完成創建

#### 添加媒體項目

1. 在後台管理頁面點擊「媒體庫管理」
2. 點擊「新增媒體」按鈕
3. 填寫媒體信息：
   - **標題**: 中文標題
   - **原始標題**: 原文標題（可選）
   - **類型**: 書籍/電影/電視劇/動漫
   - **創作者**: 作者、導演或製作方
   - **狀態**: 完成/進行中/計劃中
   - **評分**: 1-10分評分（可選）
   - **評論**: 個人評價（可選）
4. 添加外部鏈接（可選）
5. 點擊「保存」完成添加

#### 更新個人資料

1. 在後台管理頁面點擊「個人資料」
2. 修改相應信息
3. 在「技能」部分添加或刪除技能標籤
4. 在「社交鏈接」部分管理社交媒體鏈接
5. 點擊「保存」更新資料

## 🛠️ 高級功能

### 批量操作

- **博客文章**: 支持批量發布/取消發布、批量刪除
- **媒體項目**: 支持批量狀態更新、批量刪除

### 搜索和篩選

- **博客**: 按標題、內容、分類、標籤、狀態搜索
- **媒體**: 按標題、類型、狀態、評分範圍篩選

### 數據統計

- 實時統計各類內容數量
- 查看最受歡迎的文章（根據瀏覽量）
- 媒體庫完成度統計

## 🔧 故障排除

### 前端頁面空白

1. 檢查前端服務是否啟動：
   ```bash
   lsof -i :5173
   ```

2. 重新啟動前端服務：
   ```bash
   cd frontend
   pkill -f "npm run dev"
   npm run dev
   ```

3. 檢查瀏覽器控制台是否有JavaScript錯誤

### 後台管理無法訪問

1. 確認密碼正確：`blog123`
2. 檢查後端API是否正常：
   ```bash
   curl -X POST http://localhost:8080/api/auth/blog/validate \
     -H "Content-Type: application/json" \
     -d '{"password": "blog123"}'
   ```

### 數據丟失

- 本系統使用H2內存數據庫，重啟後數據會重置
- 生產環境建議使用MySQL或PostgreSQL持久化數據

### API錯誤

1. 檢查後端服務狀態：
   ```bash
   curl http://localhost:8080/api/blog/posts
   ```

2. 查看後端日誌檢查錯誤信息

3. 確認CORS設置正確

## 📚 常見問題

**Q: 如何修改管理密碼？**
A: 修改 `backend/src/main/resources/application.properties` 中的 `app.blog.admin.password` 和 `app.political.password`

**Q: 如何添加新的分類或標籤？**
A: 在創建或編輯文章時直接輸入新的分類或標籤，系統會自動創建

**Q: 如何備份數據？**
A: 可以通過 H2 控制台 (http://localhost:8080/h2-console) 導出數據，或修改配置使用文件數據庫

**Q: 如何自定義界面樣式？**
A: 修改 `frontend/src/assets/main.css` 或各個Vue組件中的style部分

**Q: 如何部署到生產環境？**
A: 
1. 前端：運行 `npm run build` 生成靜態文件
2. 後端：運行 `./mvnw package` 生成JAR文件
3. 配置生產環境數據庫和域名

## 🔗 相關文檔

- [API 文檔](./API_DOCUMENTATION.md)
- [前端 README](./frontend/README.md)
- [後端 README](./backend/README.md)

## 💡 提示

- 定期保存工作，避免數據丟失
- 使用草稿功能預覽文章效果
- 合理使用標籤和分類便於管理
- 定期檢查鏈接有效性
- 備份重要配置和數據 