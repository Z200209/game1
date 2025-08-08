package com.example.module.mapper;

import com.example.module.entity.SmsTaskOptimistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * 短信任务乐观锁Mapper接口
 * 基于version字段实现乐观锁机制
 */
@Mapper
public interface SmsTaskOptimisticMapper {
    
    /**
     * 插入短信任务
     */
    int insert(@Param("smsTaskOptimistic") SmsTaskOptimistic smsTaskOptimistic);
    
    /**
     * 乐观锁更新任务状态
     * 更新时会校验version字段，防止并发修改
     */
    int updateStatusWithOptimisticLock(@Param("id") BigInteger id, 
                                      @Param("status") Integer status, 
                                      @Param("executeTime") Integer executeTime, 
                                      @Param("updateTime") Integer updateTime,
                                      @Param("version") Integer version);
    
    /**
     * 乐观锁更新任务
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
    List<SmsTaskOptimistic> findPendingTasks(@Param("limit") Integer limit);
    
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
     * 使用乐观锁机制
     */
    int tryLockTask(@Param("id") BigInteger id, 
                   @Param("version") Integer version,
                   @Param("updateTime") Integer updateTime);
}