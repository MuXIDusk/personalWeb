#!/bin/bash

# 個人網站開發環境啟動腳本
echo "🚀 啟動個人網站開發環境..."

# 檢查是否安裝了所需的工具
if ! command -v java &> /dev/null; then
    echo "❌ 錯誤: Java 未安裝或不在 PATH 中"
    echo "請安裝 Java 21 或更高版本"
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo "❌ 錯誤: Node.js 未安裝或不在 PATH 中"
    echo "請安裝 Node.js 18 或更高版本"
    exit 1
fi

# 檢查Java版本
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' | head -1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ 錯誤: Java 版本過低 (當前: $JAVA_VERSION, 需要: 21+)"
    exit 1
fi

echo "✅ Java 版本檢查通過 (版本: $JAVA_VERSION)"

# 檢查Node.js版本
NODE_VERSION=$(node -v | sed 's/v//' | cut -d. -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo "❌ 錯誤: Node.js 版本過低 (當前: $NODE_VERSION, 需要: 18+)"
    exit 1
fi

echo "✅ Node.js 版本檢查通過 (版本: $(node -v))"

# 檢查前端依賴
if [ ! -d "frontend/node_modules" ]; then
    echo "📦 安裝前端依賴..."
    cd frontend && npm install && cd ..
fi

# 創建日誌目錄
mkdir -p logs

echo "🔧 正在啟動服務..."

# 啟動後端服務 (後台運行)
echo "🟡 啟動後端服務..."
cd backend
nohup ./mvnw spring-boot:run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
cd ..

echo "後端服務 PID: $BACKEND_PID"

# 等待後端啟動
echo "⏳ 等待後端服務啟動..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/blog/posts > /dev/null 2>&1; then
        echo "✅ 後端服務啟動成功!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ 後端服務啟動超時"
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
    sleep 2
    echo "   等待中... ($i/30)"
done

# 啟動前端服務 (後台運行)
echo "🟡 啟動前端服務..."
cd frontend
nohup npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo "前端服務 PID: $FRONTEND_PID"

# 等待前端啟動
echo "⏳ 等待前端服務啟動..."
for i in {1..15}; do
    if curl -s http://localhost:5173 > /dev/null 2>&1; then
        echo "✅ 前端服務啟動成功!"
        break
    fi
    if [ $i -eq 15 ]; then
        echo "❌ 前端服務啟動超時"
        kill $FRONTEND_PID 2>/dev/null
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
    sleep 2
    echo "   等待中... ($i/15)"
done

# 保存PID到文件
echo $BACKEND_PID > .backend.pid
echo $FRONTEND_PID > .frontend.pid

echo ""
echo "🎉 個人網站開發環境啟動成功!"
echo ""
echo "📱 前端地址: http://localhost:5173"
echo "🖥️  後端API: http://localhost:8080/api"
echo "🗄️  數據庫控制台: http://localhost:8080/h2-console"
echo ""
echo "📝 後端日誌: logs/backend.log"
echo "📝 前端日誌: logs/frontend.log"
echo ""
echo "🔐 默認密碼:"
echo "   博客管理: blog123"
echo "   政治傾向: political123"
echo ""
echo "⏹️  停止服務請運行: ./stop-dev.sh"
echo ""
echo "💡 開發提示:"
echo "   - 修改後端代碼會自動重載"
echo "   - 修改前端代碼會自動熱更新"
echo "   - 數據庫使用內存模式，重啟後會重置"
echo "" 