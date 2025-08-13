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
     * 添加短信任务
     */
    @DataSource(DataSourceType.MASTER)
    public boolean addSmsTask(String phone, String templateParam) {
        try {
            SmsTaskOptimistic task = new SmsTaskOptimistic();
            task.setPhone(phone);
            task.setTemplateParam(templateParam);
            task.setStatus(0); // 初始状态
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


    /**
     * 使用Redis乐观锁执行待处理任务
     */
    @DataSource(DataSourceType.MASTER)
    public boolean executePendingTasksWithRedis() {
        try {
            // 1. list = select * from 任务表 where status = 0
            List<SmsTaskOptimistic> SmsList = smsTaskOptimisticMapper.findPendingTasks();

            if (SmsList.isEmpty()) {
                log.info("没有待处理的短信任务");
                return true;
            }

            log.info("找到 {} 个待处理任务，开始处理", SmsList.size());

            // 2. 循环 list
            for (SmsTaskOptimistic task : SmsList) {
                try {
                    // 3. res = update 任务表 set status = 100 where id = xxx and status = 0
                    String taskStatusKey = "task_status_" + task.getId();  // 任务状态锁
                    String lockKey = "task_lock_" + task.getId();          // 任务锁
                    String lockValue = "locked_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();  // 锁值

                    // 记录Redis锁创建日志
                    log.info(" 尝试获取任务锁 - TaskId: {}, StatusKey: {}, LockKey: {}, LockValue: {}",
                            task.getId(), taskStatusKey, lockKey, lockValue);

                    // 使用Redis乐观锁：期望状态为0，设置为100（锁定）
                    boolean res = redisOptimisticLockWithStatus(taskStatusKey, "0", "100", lockKey, lockValue, TASK_LOCK_EXPIRE_TIME);

                    // 4. if res = true
                    if (res) {
                        log.info("成功获取任务锁 - TaskId: {}, StatusKey: {}, 状态: 0->100",
                                task.getId(), taskStatusKey);

                        try {
                            // 发短信
                            boolean sendResult = sendSms(task.getPhone(), task.getTemplateParam());

                            // 输出日志 脚本名 + 短信的一些信息
                            log.info(" 任务ID: {}, 手机号: {}, 模板参数: {}, 发送结果: {}",
                                    task.getId(), task.getPhone(), task.getTemplateParam(), sendResult ? "成功" : "失败");

                            // 根据发送结果确定最终状态
                            int finalStatus = sendResult ? 1 : 2; // 1=成功，2=失败

                            // 更新数据库
                            int dbUpdateResult = smsTaskOptimisticMapper.updateStatusWithOptimisticLock(
                                    task.getId(), finalStatus, 0, BaseUtils.currentSeconds(), BaseUtils.currentSeconds());

                            if (dbUpdateResult > 0) {
                                log.info("数据库更新成功 - TaskId: {}, 状态: 0->{}", task.getId(), finalStatus);

                            } else {
                                log.error(" 数据库更新失败 - TaskId: {}, 期望状态: 0, 目标状态: {}, 影响行数: {}",
                                        task.getId(), finalStatus, dbUpdateResult);
                                // 数据库更新失败，可能是并发冲突，保持Redis锁定状态
                                log.info("数据库更新失败，可能存在并发冲突，保持Redis锁定状态 - TaskId: {}", task.getId());
                            }

                            // 延迟一定时间
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // 5. else 跳过
                        Object currentStatusObj = redisTemplate.opsForValue().get(taskStatusKey);
                        String currentStatus = currentStatusObj != null ? currentStatusObj.toString() : "null";
                        log.info(" 任务已被其他线程处理，跳过处理，任务ID: {}, 手机号: {}, 当前状态: {}", task.getId(), task.getPhone(), currentStatus);
                    }

                } catch (Exception e) {
                    log.error("处理任务异常，任务ID: {}, 手机号: {}, 错误: {}",
                            task.getId(), task.getPhone(), e.getMessage(), e);

                    // 异常时尝试释放锁（将状态从100改回0，允许重新处理）
                    try {
                        String taskStatusKey = "task_status_" + task.getId();
                        String lockKey = "task_lock_" + task.getId();
                        // 尝试将Redis状态从100改回0
                        boolean released = redisOptimisticLockWithStatus(taskStatusKey, "100", "0", lockKey, null, 60);

                        if (released) {
                            log.info(" 释放任务锁成功，任务ID: {}", task.getId());
                        } else {
                            log.error(" 释放任务锁失败，任务ID: {}", task.getId());
                        }
                    } catch (Exception ex) {
                        log.error(" 释放任务锁异常，任务ID: {}, 错误: {}",
                                task.getId(), ex.getMessage());
                    }
                }
            }

            log.info(" 本轮任务处理完成，共处理 {} 个任务", SmsList.size());
            return true;

        } catch (Exception e) {
            log.error("执行待处理任务异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Redis乐观锁实现
     * @param statusKey 状态键
     * @param expectedStatus 期望状态
     * @param newStatus 新状态
     * @param lockKey 分布式锁键
     * @param lockValue 分布式锁值
     * @param expireSeconds 过期时间
     * @return 是否更新成功
     */
    private boolean redisOptimisticLockWithStatus(String statusKey, String expectedStatus, String newStatus,
                                                  String lockKey, String lockValue, int expireSeconds) {
        try {
            // 记录操作日志
            log.info("状态更新尝试 - StatusKey: {}, Expected: {}, New: {}, LockKey: {}, LockValue: {}, TTL: {}秒",
                    statusKey, expectedStatus, newStatus, lockKey, lockValue, expireSeconds);

            // 使用Lua脚本实现原子性操作
            String luaScript =
                    "local currentStatus = redis.call('GET', KEYS[1])\n" +
                            "if currentStatus == ARGV[1] or (currentStatus == false and ARGV[1] == '0') then\n" +
                            "    redis.call('SET', KEYS[1], ARGV[2])\n" +
                            "    redis.call('EXPIRE', KEYS[1], ARGV[3])\n" +
                            "    if KEYS[2] and ARGV[4] then\n" +
                            "        redis.call('SET', KEYS[2], ARGV[4])\n" +
                            "        redis.call('EXPIRE', KEYS[2], ARGV[3])\n" +
                            "    end\n" +
                            "    return 1\n" +
                            "else\n" +
                            "    return 0\n" +
                            "end";

            Object result;
            if (lockKey != null && lockValue != null) {
                // 带分布式锁的操作
                result = redisTemplate.execute((RedisCallback<Object>) connection ->
                        connection.eval(luaScript.getBytes(), ReturnType.INTEGER, 2,
                                statusKey.getBytes(), lockKey.getBytes(),
                                expectedStatus.getBytes(), newStatus.getBytes(),
                                String.valueOf(expireSeconds).getBytes(), lockValue.getBytes()));
            } else {
                // 仅状态更新
                result = redisTemplate.execute((RedisCallback<Object>) connection ->
                        connection.eval(luaScript.getBytes(), ReturnType.INTEGER, 2,
                                statusKey.getBytes(), "".getBytes(),
                                expectedStatus.getBytes(), newStatus.getBytes(),
                                String.valueOf(expireSeconds).getBytes(), "".getBytes()));
            }

            boolean success = false;
            if (result instanceof Long) {
                success = ((Long) result) == 1L;
            } else if (result instanceof Integer) {
                success = ((Integer) result) == 1;
            }

            // 根据结果记录日志
            if (success) {
                log.info("状态更新成功 - StatusKey: {}, Status: {}→{}, LockKey: {}",
                        statusKey, expectedStatus, newStatus, lockKey);
            } else {
                Object currentStatusObj = redisTemplate.opsForValue().get(statusKey);
                String currentStatus = currentStatusObj != null ? currentStatusObj.toString() : "null";
                log.warn("状态更新失败 - StatusKey: {}, Expected: {}, Actual: {}, New: {}",
                        statusKey, expectedStatus, currentStatus, newStatus);
            }

            return success;

        } catch (Exception e) {
            log.error("状态更新异常 - StatusKey: {}, Expected: {}, New: {}, 错误: {}",
                    statusKey, expectedStatus, newStatus, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 释放Redis分布式锁
     * @param lockKey 锁键
     * @param lockValue 锁值
     */
    private void releaseRedisLock(String lockKey, String lockValue) {
        try {
            String luaScript =
                    "if redis.call('GET', KEYS[1]) == ARGV[1] then\n" +
                            "    return redis.call('DEL', KEYS[1])\n" +
                            "else\n" +
                            "    return 0\n" +
                            "end";

            Object result = redisTemplate.execute((RedisCallback<Object>) connection ->
                    connection.eval(luaScript.getBytes(), ReturnType.INTEGER, 1,
                            lockKey.getBytes(), lockValue.getBytes()));

            boolean released = false;
            if (result instanceof Long) {     //  判断是否为Long类型
                released = ((Long) result) == 1L;   // 判断是否为1L  1L 表示成功释放锁
            }

            if (released) {
                log.info("成功释放分布式锁 - LockKey: {}, LockValue: {}", lockKey, lockValue);
            } else {
                Object currentValueObj = redisTemplate.opsForValue().get(lockKey);
                String currentValue = currentValueObj != null ? currentValueObj.toString() : "null";
                log.warn("锁值不匹配，无法释放 - LockKey: {}, Expected: {}, Actual: {}",
                        lockKey, lockValue, currentValue);
            }

        } catch (Exception e) {
            log.error("释放锁异常 - LockKey: {}, LockValue: {}, 错误: {}",
                    lockKey, lockValue, e.getMessage(), e);
        }
    }
}
