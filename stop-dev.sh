#!/bin/bash

# 個人網站開發環境停止腳本
echo "🛑 停止個人網站開發環境..."

# 從PID文件讀取進程ID
BACKEND_PID=""
FRONTEND_PID=""

if [ -f ".backend.pid" ]; then
    BACKEND_PID=$(cat .backend.pid)
fi

if [ -f ".frontend.pid" ]; then
    FRONTEND_PID=$(cat .frontend.pid)
fi

# 停止後端服務
if [ ! -z "$BACKEND_PID" ]; then
    if ps -p $BACKEND_PID > /dev/null 2>&1; then
        echo "🟡 停止後端服務 (PID: $BACKEND_PID)..."
        kill $BACKEND_PID
        
        # 等待進程結束
        for i in {1..10}; do
            if ! ps -p $BACKEND_PID > /dev/null 2>&1; then
                echo "✅ 後端服務已停止"
                break
            fi
            if [ $i -eq 10 ]; then
                echo "⚠️  強制停止後端服務..."
                kill -9 $BACKEND_PID 2>/dev/null
            fi
            sleep 1
        done
    else
        echo "ℹ️  後端服務未運行"
    fi
else
    echo "ℹ️  未找到後端服務PID文件"
fi

# 停止前端服務
if [ ! -z "$FRONTEND_PID" ]; then
    if ps -p $FRONTEND_PID > /dev/null 2>&1; then
        echo "🟡 停止前端服務 (PID: $FRONTEND_PID)..."
        kill $FRONTEND_PID
        
        # 等待進程結束
        for i in {1..10}; do
            if ! ps -p $FRONTEND_PID > /dev/null 2>&1; then
                echo "✅ 前端服務已停止"
                break
            fi
            if [ $i -eq 10 ]; then
                echo "⚠️  強制停止前端服務..."
                kill -9 $FRONTEND_PID 2>/dev/null
            fi
            sleep 1
        done
    else
        echo "ℹ️  前端服務未運行"
    fi
else
    echo "ℹ️  未找到前端服務PID文件"
fi

# 查找並停止其他相關進程
echo "🔍 檢查其他相關進程..."

# 停止Spring Boot進程
SPRING_PIDS=$(pgrep -f "spring-boot:run")
if [ ! -z "$SPRING_PIDS" ]; then
    echo "🟡 發現Spring Boot進程，正在停止..."
    echo $SPRING_PIDS | xargs kill 2>/dev/null
    sleep 2
    # 檢查是否還在運行
    SPRING_PIDS=$(pgrep -f "spring-boot:run")
    if [ ! -z "$SPRING_PIDS" ]; then
        echo "⚠️  強制停止Spring Boot進程..."
        echo $SPRING_PIDS | xargs kill -9 2>/dev/null
    fi
fi

# 停止Vite開發服務器
VITE_PIDS=$(pgrep -f "vite")
if [ ! -z "$VITE_PIDS" ]; then
    echo "🟡 發現Vite開發服務器，正在停止..."
    echo $VITE_PIDS | xargs kill 2>/dev/null
    sleep 2
    # 檢查是否還在運行
    VITE_PIDS=$(pgrep -f "vite")
    if [ ! -z "$VITE_PIDS" ]; then
        echo "⚠️  強制停止Vite進程..."
        echo $VITE_PIDS | xargs kill -9 2>/dev/null
    fi
fi

# 清理PID文件
rm -f .backend.pid .frontend.pid

echo ""
echo "✅ 個人網站開發環境已停止"
echo ""
echo "📁 日誌文件已保留在 logs/ 目錄中"
echo "🔄 重新啟動請運行: ./start-dev.sh"
echo "" 