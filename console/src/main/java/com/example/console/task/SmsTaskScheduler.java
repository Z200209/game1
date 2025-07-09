package com.example.console.task;

import com.example.module.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 短信任务定时调度器
 * 周期性扫描短信任务表，执行到期的短信发送任务
 */
@Slf4j
@Component
public class SmsTaskScheduler {
    
    @Autowired
    private SmsService smsService;
    
    /**
     * 定时扫描并执行待处理的短信任务
     * 任务完成后60秒再执行下一次
     */
    @Scheduled(fixedDelay = 60000)
    public void scanAndSendSmsTask() {
        log.info("开始定时扫描短信任务表并执行发送");
        try {
            // 调用Service层的方法：扫描任务表、循环发送、更新状态、记录结果
            smsService.executePendingTasks();
            log.info("定时短信任务扫描发送完成");
        } catch (Exception e) {
            log.error("定时短信任务扫描发送异常: {}", e.getMessage(), e);
        }
    }
}