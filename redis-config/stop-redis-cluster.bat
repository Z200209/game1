@echo off
echo ========================================
echo Redis哨兵模式集群停止脚本
echo ========================================
echo.

echo 正在停止Redis集群...
echo.

:: 停止Redis哨兵
echo [1/3] 停止Redis哨兵...
taskkill /f /im redis-sentinel.exe 2>nul
if %errorlevel% == 0 (
    echo 哨兵已停止
) else (
    echo 哨兵未运行或已停止
)
echo.

:: 停止Redis从节点
echo [2/3] 停止Redis从节点...
for /f "tokens=2" %%i in ('netstat -ano ^| findstr :6380') do (
    taskkill /f /pid %%i 2>nul
)
echo 从节点已停止
echo.

:: 停止Redis主节点
echo [3/3] 停止Redis主节点...
for /f "tokens=2" %%i in ('netstat -ano ^| findstr :6379') do (
    taskkill /f /pid %%i 2>nul
)
echo 主节点已停止
echo.

:: 清理进程
echo 清理Redis相关进程...
taskkill /f /im redis-server.exe 2>nul
taskkill /f /im redis-sentinel.exe 2>nul
echo.

echo ========================================
echo Redis哨兵模式集群已完全停止！
echo ========================================
echo.
echo 按任意键退出...
pause > nul