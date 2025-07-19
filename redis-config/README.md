# Redis哨兵模式部署指南

本项目配置了Redis哨兵模式，实现一主一从一哨兵的高可用Redis集群架构。

## 架构说明

### 集群架构
- **主节点 (Master)**: 127.0.0.1:6379
- **从节点 (Slave)**: 127.0.0.1:6380
- **哨兵节点 (Sentinel)**: 127.0.0.1:26379
- **主服务名称**: mymaster

### 功能特性
- **高可用性**: 主节点故障时自动切换到从节点
- **自动故障转移**: 哨兵监控主节点状态，自动处理故障转移
- **读写分离**: 主节点处理写操作，从节点处理读操作
- **数据持久化**: 支持RDB和AOF双重持久化

## 部署步骤

### 1. 环境准备

确保已安装Redis服务器，默认安装路径为：
```
d:\Program Files (x86)\redis
```

如果Redis安装在其他路径，请修改启动脚本中的`REDIS_PATH`变量。

### 2. 配置文件说明

#### 主节点配置 (redis-master.conf)
- 端口：6379
- 角色：主节点，处理读写操作
- 持久化：启用RDB和AOF
- 日志文件：redis-master.log

#### 从节点配置 (redis-slave.conf)
- 端口：6380
- 角色：从节点，只读模式
- 主节点：127.0.0.1:6379
- 持久化：启用RDB和AOF
- 日志文件：redis-slave.log

#### 哨兵配置 (sentinel.conf)
- 端口：26379
- 监控主节点：mymaster 127.0.0.1:6379
- 故障检测时间：30秒
- 故障转移超时：180秒
- 日志文件：sentinel.log

### 3. 启动集群

#### 方法一：使用启动脚本（推荐）
```bash
# 启动Redis集群
start-redis-cluster.bat

# 检查集群状态
check-redis-status.bat

# 停止Redis集群
stop-redis-cluster.bat
```

#### 方法二：手动启动
```bash
# 1. 启动主节点
redis-server.exe redis-master.conf

# 2. 启动从节点
redis-server.exe redis-slave.conf

# 3. 启动哨兵
redis-sentinel.exe sentinel.conf
```

### 4. 验证部署

#### 检查服务状态
```bash
# 检查主节点
redis-cli -h 127.0.0.1 -p 6379 ping
redis-cli -h 127.0.0.1 -p 6379 info replication

# 检查从节点
redis-cli -h 127.0.0.1 -p 6380 ping
redis-cli -h 127.0.0.1 -p 6380 info replication

# 检查哨兵
redis-cli -h 127.0.0.1 -p 26379 ping
redis-cli -h 127.0.0.1 -p 26379 sentinel masters
```

#### 测试主从同步
```bash
# 在主节点写入数据
redis-cli -h 127.0.0.1 -p 6379 set test "hello world"

# 在从节点读取数据
redis-cli -h 127.0.0.1 -p 6380 get test
```

## 应用程序配置

### Spring Boot配置

项目已配置好Spring Boot的Redis哨兵模式，配置文件位于：
- `app/src/main/resources/application-dev.properties`
- `console/src/main/resources/application-dev.properties`

关键配置项：
```properties
# Redis哨兵模式配置
spring.redis.sentinel.nodes=localhost:26379
spring.redis.sentinel.master=mymaster
spring.redis.password=
spring.redis.database=0
spring.redis.timeout=5000

# Jedis连接池配置
spring.redis.jedis.pool.max-active=8
spring.redis.jedis.pool.max-idle=4
spring.redis.jedis.pool.min-idle=1
spring.redis.jedis.pool.max-wait=2000
```

### Java代码配置

项目包含了完整的Redis哨兵配置类：
- `app/src/main/java/com/example/app/config/RedisSentinelConfig.java`
- `console/src/main/java/com/example/console/config/RedisSentinelConfig.java`

## 故障转移测试

### 模拟主节点故障
```bash
# 1. 停止主节点
redis-cli -h 127.0.0.1 -p 6379 shutdown

# 2. 观察哨兵日志，查看故障转移过程
tail -f sentinel.log

# 3. 检查新的主节点
redis-cli -h 127.0.0.1 -p 26379 sentinel masters
```

### 恢复原主节点
```bash
# 1. 重新启动原主节点
redis-server.exe redis-master.conf

# 2. 原主节点会自动成为新的从节点
```

## 监控和维护

### 日志文件
- `redis-master.log`: 主节点日志
- `redis-slave.log`: 从节点日志
- `sentinel.log`: 哨兵日志

### 常用监控命令
```bash
# 查看哨兵状态
redis-cli -h 127.0.0.1 -p 26379 sentinel masters
redis-cli -h 127.0.0.1 -p 26379 sentinel slaves mymaster
redis-cli -h 127.0.0.1 -p 26379 sentinel sentinels mymaster

# 查看复制状态
redis-cli -h 127.0.0.1 -p 6379 info replication
redis-cli -h 127.0.0.1 -p 6380 info replication
```

## 注意事项

1. **防火墙设置**: 确保端口6379、6380、26379未被防火墙阻止
2. **内存配置**: 根据实际需求调整maxmemory设置
3. **持久化**: 定期备份RDB和AOF文件
4. **网络延迟**: 哨兵检测时间应根据网络环境调整
5. **密码安全**: 生产环境建议设置Redis密码

## 故障排除

### 常见问题

1. **连接被拒绝**
   - 检查Redis服务是否启动
   - 检查端口是否被占用
   - 检查防火墙设置

2. **主从同步失败**
   - 检查网络连接
   - 检查主节点配置
   - 查看从节点日志

3. **哨兵无法连接主节点**
   - 检查哨兵配置文件
   - 检查主节点是否可访问
   - 查看哨兵日志

### 日志分析
```bash
# 查看错误日志
findstr "ERROR" redis-master.log
findstr "ERROR" redis-slave.log
findstr "ERROR" sentinel.log
```

## 扩展配置

### 多哨兵部署
如需部署多个哨兵节点以提高可靠性，可以：
1. 复制sentinel.conf并修改端口
2. 更新quorum值（建议设置为哨兵数量的一半+1）
3. 在应用配置中添加多个哨兵节点地址

### 读写分离
应用程序可以配置读写分离：
- 写操作连接主节点
- 读操作连接从节点
- 使用哨兵获取当前主从节点信息

---

**部署完成后，Redis哨兵模式集群即可为Spring Boot应用提供高可用的缓存服务！**