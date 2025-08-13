package com.example.module.mapper;

import com.example.module.entity.SmsTaskOptimistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * 短信任务乐观锁Mapper接口
 * 基于status字段实现乐观锁机制
 */
@Mapper
public interface SmsTaskOptimisticMapper {
    
    /**
     * 插入短信任务
     */
    int insert(@Param("smsTaskOptimistic") SmsTaskOptimistic smsTaskOptimistic);
    
    /**
     * 乐观锁更新任务状态
     */
    int updateStatusWithOptimisticLock(@Param("id") BigInteger id, 
                                      @Param("newStatus") Integer newStatus, 
                                      @Param("oldStatus") Integer oldStatus,
                                      @Param("executeTime") Integer executeTime, 
                                      @Param("updateTime") Integer updateTime);
    
    /**
     * 更新任务
     */
    int updateWithOptimisticLock(@Param("smsTaskOptimistic") SmsTaskOptimistic smsTaskOptimistic);
    
    /**
     * 逻辑删除任务
     */
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);
    
    /**
     * 查询待执行的任务（按创建时间顺序）
     * 状态为0的任务
     */
    List<SmsTaskOptimistic> findPendingTasks();
    
    /**
     * 根据ID查询任务
     */
    SmsTaskOptimistic findById(@Param("id") BigInteger id);
    
    /**
     * 根据手机号查询任务
     */
    List<SmsTaskOptimistic> findByPhone(@Param("phone") String phone);
    
    /**
     * 尝试锁定任务（将状态从0改为100）
     * 使用基于status的乐观锁机制
     */
    int tryLockTask(@Param("id") BigInteger id, 
                   @Param("updateTime") Integer updateTime);
}