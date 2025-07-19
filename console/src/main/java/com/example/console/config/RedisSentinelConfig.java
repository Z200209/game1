package com.example.console.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Redis哨兵模式配置类 - Console模块
 * 实现一主一从一哨兵的高可用Redis集群
 * 
 * 配置说明：
 * - 主节点：localhost:6379
 * - 从节点：localhost:6380
 * - 哨兵节点：localhost:26379
 * - 主服务名称：mymaster
 */
@Configuration
public class RedisSentinelConfig {

    /**
     * 哨兵节点配置
     * 格式：host:port
     */
    @Value("${spring.redis.sentinel.nodes:localhost:26379}")
    private String sentinelNodes;

    /**
     * 主服务名称
     * 在哨兵配置中定义的master名称
     */
    @Value("${spring.redis.sentinel.master:mymaster}")
    private String masterName;

    /**
     * Redis密码（如果设置了密码）
     */
    @Value("${spring.redis.password:}")
    private String password;

    /**
     * 数据库索引
     */
    @Value("${spring.redis.database:0}")
    private int database;

    /**
     * 连接超时时间（毫秒）
     */
    @Value("${spring.redis.timeout:5000}")
    private int timeout;

    /**
     * 连接池最大连接数
     */
    @Value("${spring.redis.jedis.pool.max-active:8}")
    private int maxActive;

    /**
     * 连接池最大空闲连接数
     */
    @Value("${spring.redis.jedis.pool.max-idle:4}")
    private int maxIdle;

    /**
     * 连接池最小空闲连接数
     */
    @Value("${spring.redis.jedis.pool.min-idle:1}")
    private int minIdle;

    /**
     * 连接池最大等待时间（毫秒）
     */
    @Value("${spring.redis.jedis.pool.max-wait:2000}")
    private long maxWait;

    /**
     * 配置Redis哨兵
     * 设置哨兵节点和主服务名称
     * 
     * @return RedisSentinelConfiguration 哨兵配置对象
     */
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        
        // 设置主服务名称
        configuration.setMaster(masterName);
        
        // 解析并添加哨兵节点
        Set<String> sentinels = new HashSet<>();
        String[] nodes = sentinelNodes.split(",");
        for (String node : nodes) {
            sentinels.add(node.trim());
        }
        configuration.setSentinels(sentinels);
        
        // 设置密码（如果有）
        if (password != null && !password.isEmpty()) {
            configuration.setPassword(password);
        }
        
        // 设置数据库
        configuration.setDatabase(database);
        
        return configuration;
    }

    /**
     * 配置Jedis连接池
     * 设置连接池的各项参数以优化性能
     * 
     * @return JedisPoolConfig 连接池配置对象
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        
        // 设置最大连接数
        poolConfig.setMaxTotal(maxActive);
        
        // 设置最大空闲连接数
        poolConfig.setMaxIdle(maxIdle);
        
        // 设置最小空闲连接数
        poolConfig.setMinIdle(minIdle);
        
        // 设置最大等待时间
        poolConfig.setMaxWaitMillis(maxWait);
        
        // 设置连接测试参数
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        
        return poolConfig;
    }

    /**
     * 配置Redis连接工厂
     * 使用哨兵模式连接Redis集群
     * 
     * @param sentinelConfiguration 哨兵配置
     * @param poolConfig 连接池配置
     * @return JedisConnectionFactory Redis连接工厂
     */
    @Bean
    @Primary
    public JedisConnectionFactory jedisConnectionFactory(
            RedisSentinelConfiguration sentinelConfiguration,
            JedisPoolConfig poolConfig) {
        
        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);
        
        // 设置连接超时时间
        factory.setTimeout(timeout);
        
        return factory;
    }

    /**
     * 配置RedisTemplate
     * 设置序列化方式和连接工厂
     * 
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate Redis操作模板
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置key序列化方式为String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 设置value序列化方式为JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        // 初始化模板
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器
     * 设置缓存过期时间和序列化方式
     * 
     * @param connectionFactory Redis连接工厂
     * @return CacheManager 缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置缓存过期时间为30分钟
                .entryTtl(Duration.ofMinutes(30))
                // 设置key序列化方式
                .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value序列化方式
                .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}