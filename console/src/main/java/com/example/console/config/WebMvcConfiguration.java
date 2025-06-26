package com.example.console.config;

import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.example.console.interceptor.ConsoleInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.example.module.service.UserService;

import java.util.List;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ApplicationArguments appArguments;
    
    @Resource
    private ConsoleInterceptor consoleInterceptor;

    public WebMvcConfiguration(ApplicationArguments appArguments) {
        this.appArguments = appArguments;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(newUserAuthResolver());
    }

    @Bean
    public UserAuthorityResolver newUserAuthResolver() {
        return new UserAuthorityResolver(appArguments);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(consoleInterceptor)
                .addPathPatterns("/console/**")
                .excludePathPatterns(
                        "/console/user/login",
                        "/console/user/register",
                        "/console/user/logout",
                        "/console/game/create",
                        "/console/game/update"

                );
    }
}
