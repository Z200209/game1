package com.example.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * App应用启动类
 * 启用配置属性绑定以支持@ConfigurationProperties注解
 */
@SpringBootApplication(scanBasePackages = "com.example")
@MapperScan("com.example")
@EnableConfigurationProperties
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}