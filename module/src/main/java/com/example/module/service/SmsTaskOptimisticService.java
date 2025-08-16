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
     * 使用数据库乐观锁执行待处理任务
     */
    @DataSource(DataSourceType.MASTER)
    public boolean executePendingTasks() {
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
                    // 3. 使用乐观锁：update 任务表 set status = 100 where id = xxx and status = 0
                    log.info("尝试获取任务锁 - TaskId: {}", task.getId());

                    // 使用数据库乐观锁：期望状态为0，设置为100（处理中）
                    int lockResult = smsTaskOptimisticMapper.updateStatusWithOptimisticLock(
                            task.getId(), 100, 0, BaseUtils.currentSeconds(), BaseUtils.currentSeconds());

                    // 4. if lockResult > 0 (成功获取锁)
                    if (lockResult > 0) {
                        log.info("成功获取任务锁 - TaskId: {}, 状态: 0->100", task.getId());

                        try {
                            // 发短信
                            boolean sendResult = sendSms(task.getPhone(), task.getTemplateParam());

                            // 输出日志 脚本名 + 短信的一些信息
                            log.info(" 任务ID: {}, 手机号: {}, 模板参数: {}, 发送结果: {}",
                                    task.getId(), task.getPhone(), task.getTemplateParam(), sendResult ? "成功" : "失败");

                            // 根据发送结果确定最终状态
                            int finalStatus = sendResult ? 1 : 2; // 1=成功，2=失败

                            // 更新数据库状态：从100（处理中）更新为最终状态
                            int dbUpdateResult = smsTaskOptimisticMapper.updateStatusWithOptimisticLock(
                                    task.getId(), finalStatus, 100, BaseUtils.currentSeconds(), BaseUtils.currentSeconds());

                            if (dbUpdateResult > 0) {
                                log.info("数据库更新成功 - TaskId: {}, 状态: 100->{}", task.getId(), finalStatus);
                            } else {
                                log.error("数据库更新失败 - TaskId: {}, 期望状态: 100, 目标状态: {}, 影响行数: {}",
                                        task.getId(), finalStatus, dbUpdateResult);
                                log.info("数据库更新失败，可能存在并发冲突 - TaskId: {}", task.getId());
                            }

                            // 延迟一定时间
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // 5. else 跳过（未能获取到锁，说明任务已被其他线程处理）
                        log.info("任务已被其他线程处理，跳过处理，任务ID: {}, 手机号: {}", task.getId(), task.getPhone());
                    }

                } catch (Exception e) {
                    log.error("处理任务异常，任务ID: {}, 手机号: {}, 错误: {}",
                            task.getId(), task.getPhone(), e.getMessage(), e);

                    // 异常时尝试回滚状态（将状态从100改回0，允许重新处理）
                    try {
                        int rollbackResult = smsTaskOptimisticMapper.updateStatusWithOptimisticLock(
                                task.getId(), 0, 100, BaseUtils.currentSeconds(), BaseUtils.currentSeconds());

                        if (rollbackResult > 0) {
                            log.info("状态回滚成功，任务ID: {}, 状态: 100->0", task.getId());
                        } else {
                            log.error("状态回滚失败，任务ID: {}", task.getId());
                        }
                    } catch (Exception ex) {
                        log.error("状态回滚异常，任务ID: {}, 错误: {}",
                                task.getId(), ex.getMessage());
                    }
                }
            }

            log.info("本轮任务处理完成，共处理 {} 个任务", SmsList.size());
            return true;

        } catch (Exception e) {
            log.error("执行待处理任务异常: {}", e.getMessage(), e);
            return false;
        }
    }
}
