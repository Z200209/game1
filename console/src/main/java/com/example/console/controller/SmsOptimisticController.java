package com.example.console.controller;

import com.example.console.annotations.VerifiedUser;
import com.example.module.entity.SmsTaskOptimistic;
import com.example.module.entity.User;
import com.example.module.service.SmsTaskOptimisticService;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 短信任务乐观锁控制器
 * 提供基于乐观锁和Redis分布式锁的短信任务管理接口
 */
@Slf4j
@RestController
@RequestMapping("/console/sms/optimistic")
public class SmsOptimisticController {
    
    @Autowired
    private SmsTaskOptimisticService smsTaskOptimisticService;

    /**
     * 添加短信任务（乐观锁版本）
     */
    @RequestMapping("/task/add")
    public Response addSmsTask(@VerifiedUser User loginUser,
                              @RequestParam("phone") String phone,
                              @RequestParam("templateParam") String templateParam) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试添加短信任务");
            return new Response(1002); // 没有登录
        }

        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            log.info("手机号不能为空");
            return new Response(4005); // 请求参数错误
        }
        if (templateParam == null || templateParam.trim().isEmpty()) {
            log.info("模板参数不能为空");
            return new Response(4005); // 请求参数错误
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            log.info("手机号格式不正确");
            return new Response(4005); // 请求参数错误
        }

        String actualTemplateParam = templateParam;
        if (templateParam != null && templateParam.matches("^\\d{4,6}$")) {
            actualTemplateParam = "{\"code\":\"" + templateParam + "\"}";
        }
        
        log.info("用户 {} 添加短信任务，手机号: {}, 模板参数: {}",
                loginUser.getId(), phone, actualTemplateParam);
        
        try {
            boolean success = smsTaskOptimisticService.addSmsTask(phone, actualTemplateParam);
            if (success) {
                return new Response(1001); // 成功
            } else {
                return new Response(4004); // 操作失败
            }
        } catch (Exception e) {
            log.error("添加短信任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 查询短信任务记录（乐观锁版本）
     */
    @RequestMapping("/task/records")
    public Response getSmsTaskRecords(@VerifiedUser User loginUser,
                                     @RequestParam("phone") String phone) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试查询短信任务");
            return new Response(1002); // 没有登录
        }
        
        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            log.info("手机号不能为空");
            return new Response(4005); // 请求参数错误
        }
        
        try {
            List<SmsTaskOptimistic> taskList = smsTaskOptimisticService.getSmsTasksByPhone(phone);
            return new Response(1001, taskList); // 成功
        } catch (Exception e) {
            log.error("查询短信任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 根据ID查询短信任务（乐观锁版本）
     */
    @RequestMapping("/task/{id}")
    public Response getSmsTaskById(@VerifiedUser User loginUser,
                                  @PathVariable("id") BigInteger id) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试查询短信任务详情");
            return new Response(1002); // 没有登录
        }
        
        // 参数验证
        if (id == null) {
            return new Response(4005); // 请求参数错误
        }
        
        try {
            SmsTaskOptimistic task = smsTaskOptimisticService.getSmsTaskById(id);
            if (task != null) {
                return new Response(1001, task); // 成功
            } else {
                return new Response(4006); // 数据不存在
            }
        } catch (Exception e) {
            log.error("查询短信任务详情异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 删除短信任务（乐观锁版本）
     */
    @RequestMapping("/task/delete/{id}")
    public Response deleteSmsTask(@VerifiedUser User loginUser,
                                 @PathVariable("id") BigInteger id) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试删除短信任务");
            return new Response(1002); // 没有登录
        }
        
        // 参数验证
        if (id == null) {
            return new Response(4005); // 请求参数错误
        }
        
        log.info("用户 {} 删除短信任务，任务ID: {}", loginUser.getId(), id);
        
        try {
            boolean success = smsTaskOptimisticService.deleteSmsTask(id);
            if (success) {
                return new Response(1001); // 成功
            } else {
                return new Response(4006); // 数据不存在
            }
        } catch (Exception e) {
            log.error("删除短信任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 手动触发执行待处理任务（乐观锁版本）
     */
    @RequestMapping("/task/execute")
    public Response executePendingTasks(@VerifiedUser User loginUser) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试执行待处理任务");
            return new Response(1002); // 没有登录
        }
        
        log.info("用户 {} 手动触发执行待处理短信任务（乐观锁版本）", loginUser.getId());
        
        try {
            smsTaskOptimisticService.executePendingTasksWithOptimisticLock();
            return new Response(1001, "任务执行完成"); // 成功
        } catch (Exception e) {
            log.error("执行待处理任务异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 清理过期的Redis锁
     */
    @RequestMapping("/task/clean-locks")
    public Response cleanExpiredLocks(@VerifiedUser User loginUser) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试清理Redis锁");
            return new Response(1002); // 没有登录
        }

        log.info("用户 {} 手动触发清理Redis过期锁", loginUser.getId());

        try {
            smsTaskOptimisticService.cleanExpiredLocks();
            return new Response(1001, "锁清理完成"); // 成功
        } catch (Exception e) {
            log.error("清理Redis锁异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
    
    /**
     * 获取任务统计信息
     */
    @RequestMapping("/task/stats")
    public Response getTaskStats(@VerifiedUser User loginUser) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取任务统计");
            return new Response(1002); // 没有登录
        }
        
        try {
            // 这里可以添加统计逻辑，比如查询各种状态的任务数量
            // 暂时返回简单的成功响应
            return new Response(1001, "统计功能待实现"); // 成功
        } catch (Exception e) {
            log.error("获取任务统计异常: {}", e.getMessage(), e);
            return new Response(4004); // 操作失败
        }
    }
}