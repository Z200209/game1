package com.example.console;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {
        private CorsConfiguration buildConfig() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowCredentials(true);

            //允许访问的客户端域名
            List<String> allowedOrigins = new ArrayList<>();

            //for web dev
            allowedOrigins.add("http://localhost:8083");
            //for web prod
            allowedOrigins.add("https://www.example.com");


            corsConfiguration.setAllowedOrigins(allowedOrigins);

            //允许访问的客户端请求方法列表
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedMethod("*");
            return corsConfiguration;

        }
        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", buildConfig());
            return new CorsFilter(source);
        }

}