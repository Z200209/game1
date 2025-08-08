package com.example.console;

import com.example.module.entity.SmsTaskOptimistic;
import com.example.module.service.SmsTaskOptimisticService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 短信任务乐观锁测试类
 * 测试乐观锁在高并发场景下的表现
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class SmsTaskOptimisticTest {
    
    @Autowired
    private SmsTaskOptimisticService smsTaskOptimisticService;
    
    /**
     * 测试添加短信任务
     */
    @Test
    public void testAddSmsTask() {
        try {
            boolean result = smsTaskOptimisticService.addSmsTask("13800138000", "{\"code\":\"1234\"}");
            log.info("添加短信任务结果: {}", result);
            assert result;
        } catch (Exception e) {
            log.error("测试添加短信任务异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 测试并发执行任务（乐观锁机制）
     */
    @Test
    public void testConcurrentTaskExecution() {
        // 先添加一些测试任务
        for (int i = 0; i < 10; i++) {
            smsTaskOptimisticService.addSmsTask("1380013800" + i, "{\"code\":\"" + (1000 + i) + "\"}");
        }
        
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        // 模拟多线程并发执行任务
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    log.info("线程 {} 开始执行任务", threadId);
                    smsTaskOptimisticService.executePendingTasksWithOptimisticLock();
                    successCount.incrementAndGet();
                    log.info("线程 {} 执行完成", threadId);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("线程 {} 执行异常: {}", threadId, e.getMessage(), e);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
            log.info("并发测试完成，成功: {}, 失败: {}", successCount.get(), failCount.get());
        } catch (InterruptedException e) {
            log.error("等待线程完成时被中断: {}", e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }
    
    /**
     * 测试乐观锁版本冲突
     */
    @Test
    public void testOptimisticLockConflict() {
        // 添加一个测试任务
        boolean addResult = smsTaskOptimisticService.addSmsTask("13900139000", "{\"code\":\"9999\"}");
        log.info("添加测试任务结果: {}", addResult);
        
        if (addResult) {
            // 查询刚添加的任务
            var tasks = smsTaskOptimisticService.getSmsTasksByPhone("13900139000");
            if (!tasks.isEmpty()) {
                SmsTaskOptimistic task = tasks.get(0);
                log.info("查询到任务: ID={}, Version={}, Status={}", task.getId(), task.getVersion(), task.getStatus());
                
                // 模拟两个线程同时处理同一个任务
                ExecutorService executorService = Executors.newFixedThreadPool(2);
                CountDownLatch latch = new CountDownLatch(2);
                AtomicInteger successCount = new AtomicInteger(0);
                
                for (int i = 0; i < 2; i++) {
                    final int threadId = i;
                    executorService.submit(() -> {
                        try {
                            log.info("线程 {} 尝试处理任务 {}", threadId, task.getId());
                            smsTaskOptimisticService.executePendingTasksWithOptimisticLock();
                            successCount.incrementAndGet();
                            log.info("线程 {} 处理完成", threadId);
                        } catch (Exception e) {
                            log.error("线程 {} 处理异常: {}", threadId, e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                }
                
                try {
                    latch.await();
                    log.info("乐观锁冲突测试完成，成功处理次数: {}", successCount.get());
                    
                    // 验证任务最终状态
                    SmsTaskOptimistic finalTask = smsTaskOptimisticService.getSmsTaskById(task.getId());
                    if (finalTask != null) {
                        log.info("任务最终状态: ID={}, Version={}, Status={}", 
                                finalTask.getId(), finalTask.getVersion(), finalTask.getStatus());
                    }
                } catch (InterruptedException e) {
                    log.error("等待线程完成时被中断: {}", e.getMessage());
                } finally {
                    executorService.shutdown();
                }
            }
        }
    }
    
    /**
     * 测试查询功能
     */
    @Test
    public void testQueryFunctions() {
        try {
            // 测试按手机号查询
            var tasksByPhone = smsTaskOptimisticService.getSmsTasksByPhone("13800138000");
            log.info("按手机号查询结果数量: {}", tasksByPhone.size());
            
            if (!tasksByPhone.isEmpty()) {
                SmsTaskOptimistic task = tasksByPhone.get(0);
                log.info("查询到任务: ID={}, Phone={}, Status={}, Version={}", 
                        task.getId(), task.getPhone(), task.getStatus(), task.getVersion());
                
                // 测试按ID查询
                SmsTaskOptimistic taskById = smsTaskOptimisticService.getSmsTaskById(task.getId());
                if (taskById != null) {
                    log.info("按ID查询成功: ID={}, Phone={}", taskById.getId(), taskById.getPhone());
                } else {
                    log.warn("按ID查询失败");
                }
            }
        } catch (Exception e) {
            log.error("测试查询功能异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 测试删除功能
     */
    @Test
    public void testDeleteFunction() {
        try {
            // 先添加一个测试任务
            boolean addResult = smsTaskOptimisticService.addSmsTask("13700137000", "{\"code\":\"7777\"}");
            log.info("添加测试任务结果: {}", addResult);
            
            if (addResult) {
                // 查询任务
                var tasks = smsTaskOptimisticService.getSmsTasksByPhone("13700137000");
                if (!tasks.isEmpty()) {
                    SmsTaskOptimistic task = tasks.get(0);
                    log.info("准备删除任务: ID={}", task.getId());
                    
                    // 删除任务
                    boolean deleteResult = smsTaskOptimisticService.deleteSmsTask(task.getId());
                    log.info("删除任务结果: {}", deleteResult);
                    
                    // 验证删除结果
                    SmsTaskOptimistic deletedTask = smsTaskOptimisticService.getSmsTaskById(task.getId());
                    if (deletedTask == null) {
                        log.info("任务删除成功，查询不到已删除的任务");
                    } else {
                        log.warn("任务删除可能失败，仍能查询到任务: IsDeleted={}", deletedTask.getIsDeleted());
                    }
                }
            }
        } catch (Exception e) {
            log.error("测试删除功能异常: {}", e.getMessage(), e);
        }
    }
}