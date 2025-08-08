package com.example.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Console应用启动类
 * 启用配置属性绑定以支持@ConfigurationProperties注解
 * 开启定时任务调度功能
 */
@SpringBootApplication(scanBasePackages = "com.example")
@MapperScan("com.example.module.mapper")
@EnableScheduling
@EnableConfigurationProperties
public class ConsoleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }
}