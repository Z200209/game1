package com.example.module.entity;

import lombok.Data;

import java.math.BigInteger;

/**
 * 短信任务乐观锁实体类
 * 使用status字段实现乐观锁机制
 */
@Data
public class SmsTaskOptimistic {
    
    /**
     * 主键ID
     */
    private BigInteger id;
    
    /**
     * 手机号
     */
    private String phone;

    /**
     * 模板参数
     */
    private String templateParam;
    
    /**
     * 任务状态：0-初始状态，1-完成，2-失败，100-处理中（锁定状态）
     */
    private Integer status;

    /**
     * 执行时间戳
     */
    private Integer executeTime;
    
    /**
     * 创建时间戳
     */
    private Integer createTime;
    
    /**
     * 更新时间戳
     */
    private Integer updateTime;
    
    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    private Integer isDeleted;
}