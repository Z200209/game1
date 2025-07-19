@echo off
echo ========================================
echo Redis哨兵模式集群状态检查
echo ========================================
echo.

:: 设置Redis安装路径（请根据实际情况修改）
set REDIS_PATH="d:\Program Files (x86)\redis"

:: 检查Redis是否已安装
if not exist %REDIS_PATH%\redis-cli.exe (
    echo 错误：未找到Redis CLI工具，请检查REDIS_PATH变量设置
    echo 当前设置：%REDIS_PATH%
    pause
    exit /b 1
)

echo 正在检查Redis集群状态...
echo.

:: 检查主节点状态（端口6379）
echo [1/4] 检查Redis主节点状态 (端口6379):
echo ----------------------------------------
%REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6379 ping 2>nul
if %errorlevel% == 0 (
    echo ✓ 主节点运行正常
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6379 info replication | findstr "role:master"
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6379 info replication | findstr "connected_slaves"
) else (
    echo ✗ 主节点未运行或连接失败
)
echo.

:: 检查从节点状态（端口6380）
echo [2/4] 检查Redis从节点状态 (端口6380):
echo ----------------------------------------
%REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6380 ping 2>nul
if %errorlevel% == 0 (
    echo ✓ 从节点运行正常
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6380 info replication | findstr "role:slave"
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6380 info replication | findstr "master_host"
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 6380 info replication | findstr "master_port"
) else (
    echo ✗ 从节点未运行或连接失败
)
echo.

:: 检查哨兵状态（端口26379）
echo [3/4] 检查Redis哨兵状态 (端口26379):
echo ----------------------------------------
%REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 26379 ping 2>nul
if %errorlevel% == 0 (
    echo ✓ 哨兵运行正常
    echo 主节点信息：
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 26379 sentinel masters | findstr "name\|ip\|port\|flags"
    echo 从节点信息：
    %REDIS_PATH%\redis-cli.exe -h 127.0.0.1 -p 26379 sentinel slaves mymaster | findstr "name\|ip\|port\|flags"
) else (
    echo ✗ 哨兵未运行或连接失败
)
echo.

:: 检查端口占用情况
echo [4/4] 检查端口占用情况:
echo ----------------------------------------
echo 端口6379 (主节点):
netstat -ano | findstr :6379
echo 端口6380 (从节点):
netstat -ano | findstr :6380
echo 端口26379 (哨兵):
netstat -ano | findstr :26379
echo.

echo ========================================
echo 状态检查完成！
echo ========================================
echo.
echo 说明：
echo ✓ 表示服务正常运行
echo ✗ 表示服务未运行或有问题
echo.
echo 按任意键退出...
pause > nul