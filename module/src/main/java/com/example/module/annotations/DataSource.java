package com.example.module.annotations;

import com.example.module.config.data.DataSourceType;

import java.lang.annotation.*;

/**
 * 数据源注解
 * 用于标记方法使用哪个数据源
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    
    /**
     * 数据源类型
     * @return 数据源类型，默认为主库
     */
    DataSourceType value() default DataSourceType.MASTER;
}