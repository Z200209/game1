package com.example.console.task;

import com.example.module.service.SmsTaskOptimisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 短信任务乐观锁调度器
 * 定时执行待处理的短信任务，使用乐观锁和Redis分布式锁机制
 */
@Slf4j
@Component
public class SmsTaskOptimisticScheduler {
    
    @Autowired
    private SmsTaskOptimisticService smsTaskOptimisticService;
    
    /**
     * 定时执行待处理的短信任务
     * 每30秒执行一次
     */
    @Scheduled(fixedRate = 30000)
    public void executeScheduledTasks() {
        try {
            log.info("开始执行定时短信任务调度");
            smsTaskOptimisticService.executePendingTasksWithOptimisticLock();
            log.info("定时短信任务调度执行完成");
        } catch (Exception e) {
            log.error("定时短信任务调度执行异常: {}", e.getMessage(), e);
        }
    }
    

    
    /**
     * 手动触发任务执行
     */
    public void manualExecute() {
        try {
            log.info("手动触发短信任务执行");
            smsTaskOptimisticService.executePendingTasksWithOptimisticLock();
            log.info("手动触发任务执行完成");
        } catch (Exception e) {
            log.error("手动触发任务执行异常: {}", e.getMessage(), e);
        }
    }
}