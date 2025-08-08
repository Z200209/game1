package com.example.module.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.example.module.annotations.DataSource;
import com.example.module.config.data.DataSourceType;
import com.example.module.entity.Sms;
import com.example.module.entity.SmsTaskOptimistic;
import com.example.module.mapper.SmsMapper;
import com.example.module.mapper.SmsTaskOptimisticMapper;
import com.example.module.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 短信任务乐观锁服务类
 */
@Slf4j
@Service
public class SmsTaskOptimisticService {

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Autowired
    private SmsMapper smsMapper;

    @Autowired
    private SmsTaskOptimisticMapper smsTaskOptimisticMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "sms_task_lock:";
    private static final String PROCESSING_PREFIX = "sms_task_processing:";
    private static final int GLOBAL_LOCK_EXPIRE_TIME = 60; // 全局锁过期时间：1分钟
    private static final int TASK_LOCK_EXPIRE_TIME = 300; // 任务锁过期时间：5分钟
    private static final int MAX_RETRY_TIMES = 3; // 最大重试次数

    String templateCode = "SMS_154950909";
    String templateName = "阿里云短信测试";

    /**
     * 创建阿里云短信客户端
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        return new Client(config);
    }

    /**
     * 发送短信
     */
    private boolean sendSms(String phone, String templateParam) {
        try {
            Client client = createClient();
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName("阿里云短信测试")
                    .setTemplateCode("SMS_154950909")
                    .setTemplateParam(templateParam);

            SendSmsResponse response = client.sendSms(request);
            String code = response.getBody().getCode();
            if (!"OK".equals(code)) {
                log.error("短信发送失败，响应码: {}, 消息: {}, 手机号: {}", code, response.getBody().getMessage(), phone);
            }

            // 记录发送结果
            Sms sms = new Sms();
            sms.setPhone(phone);
            sms.setContent(templateParam);
            sms.setTemplateCode(templateCode);
            sms.setTemplateParam(templateParam);
            sms.setStatus("OK".equals(code) ? 1 : 2);
            sms.setBizId(response.getBody().getBizId());
            sms.setIsDeleted(0);
            Integer currentTime = BaseUtils.currentSeconds();
            sms.setSendTime(currentTime);
            sms.setCreateTime(currentTime);
            sms.setUpdateTime(currentTime);
            smsMapper.insert(sms);
            return "OK".equals(code);
        } catch (Exception e) {
            log.error("短信发送异常，手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 添加短信任务（乐观锁版本）
     */
    @DataSource(DataSourceType.MASTER)
    public boolean addSmsTask(String phone, String templateParam) {
        try {
            SmsTaskOptimistic task = new SmsTaskOptimistic();
            task.setPhone(phone);
            task.setTemplateParam(templateParam);
            task.setStatus(0); // 初始状态
            task.setVersion(1); // 初始版本号
            Integer currentTime = BaseUtils.currentSeconds();
            task.setIsDeleted(0);
            task.setCreateTime(currentTime);
            task.setUpdateTime(currentTime);

            int result = smsTaskOptimisticMapper.insert(task);
            log.info("添加短信任务成功，手机号: {}", phone);
            return result > 0;
        } catch (Exception e) {
            log.error("添加短信任务失败，手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取Redis分布式锁
     */
    private boolean acquireDistributedLock(String lockKey, String lockValue) {
        return acquireDistributedLock(lockKey, lockValue, TASK_LOCK_EXPIRE_TIME);
    }

    /**
     * 获取Redis分布式锁（指定过期时间）
     */
    private boolean acquireDistributedLock(String lockKey, String lockValue, int expireTimeSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTimeSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("获取分布式锁失败，lockKey: {}, 错误: {}", lockKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 释放Redis分布式锁
     */
    private void releaseDistributedLock(String lockKey, String lockValue) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                return connection.eval(script.getBytes(), ReturnType.INTEGER, 1, lockKey.getBytes(), lockValue.getBytes());
            });
            if (result != null && result > 0) {
                log.debug("成功释放分布式锁，lockKey: {}", lockKey);
            } else {
                log.warn("释放分布式锁失败，锁可能已过期或被其他线程释放，lockKey: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放分布式锁失败，lockKey: {}, 错误: {}", lockKey, e.getMessage(), e);
        }
    }

    /**
     * 清理指定的Redis锁
     */
    public boolean clearLock(String lockKey) {
        try {
            Boolean result = redisTemplate.delete(lockKey);
            if (Boolean.TRUE.equals(result)) {
                log.info("成功清理锁，lockKey: {}", lockKey);
                return true;
            } else {
                log.info("锁不存在或已过期，lockKey: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            log.error("清理锁失败，lockKey: {}, 错误: {}", lockKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 清理全局锁
     */
    public boolean clearGlobalLock() {
        String globalLockKey = LOCK_PREFIX + "global";
        return clearLock(globalLockKey);
    }

    /**
     * 清理所有SMS任务相关的锁
     */
    public void clearAllSmsTaskLocks() {
        try {
            Set<String> lockKeys = redisTemplate.keys(LOCK_PREFIX + "*");
            if (lockKeys != null && !lockKeys.isEmpty()) {
                Long deletedCount = redisTemplate.delete(lockKeys);
                log.info("清理SMS任务锁完成，共清理 {} 个锁", deletedCount != null ? deletedCount : 0);
            } else {
                log.info("没有找到需要清理的SMS任务锁");
            }
        } catch (Exception e) {
            log.error("清理SMS任务锁失败，错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 检查锁状态
     */
    public String checkLockStatus(String lockKey) {
        try {
            String lockValue = (String) redisTemplate.opsForValue().get(lockKey);
            if (lockValue != null) {
                Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
                return String.format("锁存在，值: %s, 剩余TTL: %d秒", lockValue, ttl != null ? ttl : -1);
            } else {
                return "锁不存在";
            }
        } catch (Exception e) {
            log.error("检查锁状态失败，lockKey: {}, 错误: {}", lockKey, e.getMessage(), e);
            return "检查失败: " + e.getMessage();
        }
    }

    /**
     * 检查全局锁状态
     */
    public String checkGlobalLockStatus() {
        String globalLockKey = LOCK_PREFIX + "global";
        return checkLockStatus(globalLockKey);
    }
    
    /**
     * 定时清理过期的Redis锁
     * 清理策略：
     * 1. 查找所有SMS任务相关的锁
     * 2. 检查每个锁的TTL，如果TTL <= 0 或锁已不存在，则认为是过期锁
     * 3. 清理这些过期锁
     */
    public void cleanExpiredLocks() {
        try {
            Set<String> lockKeys = redisTemplate.keys(LOCK_PREFIX + "*");
            if (lockKeys == null || lockKeys.isEmpty()) {
                log.debug("没有找到SMS任务相关的锁");
                return;
            }
            
            int expiredCount = 0;
            int totalCount = lockKeys.size();
            
            for (String lockKey : lockKeys) {
                try {
                    Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
                    // TTL为-2表示键不存在，TTL为-1表示键存在但没有过期时间，TTL为0或负数表示已过期
                    if (ttl != null && ttl <= 0 && ttl != -1) {
                        Boolean deleted = redisTemplate.delete(lockKey);
                        if (Boolean.TRUE.equals(deleted)) {
                            expiredCount++;
                            log.debug("清理过期锁: {}", lockKey);
                        }
                    }
                } catch (Exception e) {
                    log.warn("检查锁过期状态失败，lockKey: {}, 错误: {}", lockKey, e.getMessage());
                }
            }
            
            if (expiredCount > 0) {
                log.info("清理过期Redis锁完成，总锁数: {}, 清理过期锁数: {}", totalCount, expiredCount);
            } else {
                log.debug("没有发现过期的Redis锁，总锁数: {}", totalCount);
            }
            
        } catch (Exception e) {
            log.error("清理过期Redis锁失败，错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 执行待处理的短信任务（使用分布式锁+乐观锁）
     */
    @DataSource(DataSourceType.MASTER)
    public void executePendingTasksWithOptimisticLock() {
        String globalLockKey = LOCK_PREFIX + "global";
        String lockValue = Thread.currentThread().getName() + ":" + System.currentTimeMillis();

        // 获取全局分布式锁，防止多个实例同时处理
        if (!acquireDistributedLock(globalLockKey, lockValue, GLOBAL_LOCK_EXPIRE_TIME)) {
            log.info("其他实例正在处理短信任务，跳过本次执行");
            return;
        }

        try {
            List<SmsTaskOptimistic> pendingTasks = smsTaskOptimisticMapper.findPendingTasks(100);
            log.info("开始执行待处理短信任务，共 {} 个任务", pendingTasks.size());

            for (SmsTaskOptimistic task : pendingTasks) {
                processTaskWithOptimisticLock(task);
            }
        } finally {
            releaseDistributedLock(globalLockKey, lockValue);
        }
    }

    /**
     * 使用乐观锁处理单个任务
     */
    private void processTaskWithOptimisticLock(SmsTaskOptimistic task) {
        String taskLockKey = LOCK_PREFIX + task.getId();
        String lockValue = Thread.currentThread().getName() + ":" + System.currentTimeMillis();

        // 获取任务级别的分布式锁
        if (!acquireDistributedLock(taskLockKey, lockValue)) {
            log.info("任务 {} 正在被其他线程处理，跳过", task.getId());
            return;
        }

        try {
            // 重新查询任务，获取最新状态和版本号
            SmsTaskOptimistic latestTask = smsTaskOptimisticMapper.findById(task.getId());
            if (latestTask == null || latestTask.getStatus() != 0) {
                log.info("任务 {} 状态已变更或不存在，跳过处理", task.getId());
                return;
            }

            // 尝试使用乐观锁锁定任务（状态改为100-处理中）
            Integer currentTime = BaseUtils.currentSeconds();
            int lockResult = smsTaskOptimisticMapper.tryLockTask(latestTask.getId(), latestTask.getVersion(), currentTime);

            if (lockResult == 0) {
                log.info("任务 {} 乐观锁锁定失败，可能已被其他线程处理", task.getId());
                return;
            }

            log.info("任务 {} 锁定成功，开始处理", task.getId());

            // 执行短信发送
            boolean success = false;
            int retryCount = 0;

            while (retryCount < MAX_RETRY_TIMES && !success) {
                try {
                    success = sendSms(latestTask.getPhone(), latestTask.getTemplateParam());
                    if (!success) {
                        retryCount++;
                        if (retryCount < MAX_RETRY_TIMES) {
                            Thread.sleep(1000 * retryCount); // 递增延迟重试
                        }
                    }
                } catch (Exception e) {
                    log.error("任务 {} 执行异常，重试次数: {}, 错误: {}", task.getId(), retryCount, e.getMessage());
                    retryCount++;
                    if (retryCount < MAX_RETRY_TIMES) {
                        try {
                            Thread.sleep(1000 * retryCount);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            // 更新任务最终状态
            Integer executeTime = BaseUtils.currentSeconds();
            Integer finalStatus = success ? 1 : 2; // 1-完成 2-失败

            // 使用乐观锁更新最终状态（版本号+1）
            int updateResult = smsTaskOptimisticMapper.updateStatusWithOptimisticLock(
                    latestTask.getId(), finalStatus, executeTime, executeTime, latestTask.getVersion() + 1);

            if (updateResult > 0) {
                log.info("任务 {} 处理完成，手机号: {}, 结果: {}, 重试次数: {}",
                        task.getId(), latestTask.getPhone(), success ? "成功" : "失败", retryCount);
            } else {
                log.error("任务 {} 最终状态更新失败，可能存在并发问题", task.getId());
            }

        } catch (Exception e) {
            log.error("处理任务 {} 时发生异常: {}", task.getId(), e.getMessage(), e);
            // 异常情况下尝试将任务状态重置为初始状态
            try {
                Integer currentTime = BaseUtils.currentSeconds();
                smsTaskOptimisticMapper.updateStatusWithOptimisticLock(
                        task.getId(), 0, null, currentTime, task.getVersion() + 1);
            } catch (Exception resetException) {
                log.error("重置任务 {} 状态失败: {}", task.getId(), resetException.getMessage());
            }
        } finally {
            releaseDistributedLock(taskLockKey, lockValue);
        }
    }

    /**
     * 根据手机号查询短信任务
     */
    @DataSource(DataSourceType.SLAVE)
    public List<SmsTaskOptimistic> getSmsTasksByPhone(String phone) {
        return smsTaskOptimisticMapper.findByPhone(phone);
    }

    /**
     * 根据ID查询短信任务
     */
    @DataSource(DataSourceType.SLAVE)
    public SmsTaskOptimistic getSmsTaskById(BigInteger id) {
        return smsTaskOptimisticMapper.findById(id);
    }

    /**
     * 删除短信任务（逻辑删除）
     */
    @DataSource(DataSourceType.MASTER)
    public boolean deleteSmsTask(BigInteger id) {
        try {
            Integer currentTime = BaseUtils.currentSeconds();
            int result = smsTaskOptimisticMapper.delete(id, currentTime);
            return result > 0;
        } catch (Exception e) {
            log.error("删除短信任务失败，任务ID: {}, 错误: {}", id, e.getMessage(), e);
            return false;
        }
    }

}