@echo off
echo ========================================
echo Redis哨兵模式集群启动脚本
echo 配置：一主一从一哨兵
echo ========================================
echo.

:: 设置Redis安装路径（请根据实际情况修改）
set REDIS_PATH="d:\Program Files (x86)\redis"

:: 检查Redis是否已安装
if not exist %REDIS_PATH%\redis-server.exe (
    echo 错误：未找到Redis安装目录，请检查REDIS_PATH变量设置
    echo 当前设置：%REDIS_PATH%
    pause
    exit /b 1
)

echo 正在启动Redis集群...
echo.

:: 启动Redis主节点（端口6379）
echo [1/3] 启动Redis主节点 (端口6379)...
start "Redis Master" %REDIS_PATH%\redis-server.exe redis-master.conf
ping 127.0.0.1 -n 3 > nul
echo 主节点启动完成
echo.

:: 启动Redis从节点（端口6380）
echo [2/3] 启动Redis从节点 (端口6380)...
start "Redis Slave" %REDIS_PATH%\redis-server.exe redis-slave.conf
ping 127.0.0.1 -n 3 > nul
echo 从节点启动完成
echo.

:: 启动Redis哨兵（端口26379）
echo [3/3] 启动Redis哨兵 (端口26379)...
start "Redis Sentinel" %REDIS_PATH%\redis-sentinel.exe sentinel.conf
ping 127.0.0.1 -n 3 > nul
echo 哨兵启动完成
echo.

echo ========================================
echo Redis哨兵模式集群启动完成！
echo ========================================
echo 主节点：127.0.0.1:6379
echo 从节点：127.0.0.1:6380
echo 哨兵：  127.0.0.1:26379
echo 主服务名称：mymaster
echo ========================================
echo.
echo 提示：
echo 1. 应用程序应连接到哨兵地址：127.0.0.1:26379
echo 2. 使用主服务名称：mymaster
echo 3. 哨兵会自动处理主从切换
echo.
echo 按任意键退出...
pause > nul