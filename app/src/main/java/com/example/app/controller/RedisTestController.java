package com.example.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis哨兵模式测试控制器
 * 用于验证Redis哨兵模式是否正常工作
 */
@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试Redis连接
     * 验证哨兵模式是否正常工作
     * 
     * @return 测试结果
     */
    @GetMapping("/test")
    public Map<String, Object> testRedis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试写入数据
            String testKey = "sentinel:test:" + System.currentTimeMillis();
            String testValue = "Hello Redis Sentinel!";
            
            redisTemplate.opsForValue().set(testKey, testValue, 60, TimeUnit.SECONDS);
            
            // 测试读取数据
            Object retrievedValue = redisTemplate.opsForValue().get(testKey);
            
            // 测试删除数据
            Boolean deleted = redisTemplate.delete(testKey);
            
            result.put("success", true);
            result.put("message", "Redis哨兵模式连接成功");
            result.put("testKey", testKey);
            result.put("testValue", testValue);
            result.put("retrievedValue", retrievedValue);
            result.put("deleted", deleted);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Redis连接失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        
        return result;
    }

    /**
     * 设置缓存数据
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 过期时间（秒）
     * @return 操作结果
     */
    @PostMapping("/set")
    public Map<String, Object> setCache(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(defaultValue = "300") long ttl) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
            
            result.put("success", true);
            result.put("message", "缓存设置成功");
            result.put("key", key);
            result.put("value", value);
            result.put("ttl", ttl);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "缓存设置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取缓存数据
     * 
     * @param key 缓存键
     * @return 缓存值
     */
    @GetMapping("/get")
    public Map<String, Object> getCache(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Object value = redisTemplate.opsForValue().get(key);
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            
            result.put("success", true);
            result.put("key", key);
            result.put("value", value);
            result.put("ttl", ttl);
            result.put("exists", value != null);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取缓存失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除缓存数据
     * 
     * @param key 缓存键
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteCache(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Boolean deleted = redisTemplate.delete(key);
            
            result.put("success", true);
            result.put("key", key);
            result.put("deleted", deleted);
            result.put("message", deleted ? "缓存删除成功" : "缓存不存在或删除失败");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除缓存失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取Redis连接信息
     * 
     * @return Redis连接信息
     */
    @GetMapping("/info")
    public Map<String, Object> getRedisInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取连接工厂信息
            result.put("success", true);
            result.put("message", "Redis哨兵模式运行正常");
            result.put("connectionFactory", redisTemplate.getConnectionFactory().getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
            
            // 测试基本操作
            String pingKey = "ping:" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(pingKey, "pong", 10, TimeUnit.SECONDS);
            String pingResult = (String) redisTemplate.opsForValue().get(pingKey);
            redisTemplate.delete(pingKey);
            
            result.put("pingTest", "pong".equals(pingResult) ? "成功" : "失败");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取Redis信息失败: " + e.getMessage());
        }
        
        return result;
    }
}