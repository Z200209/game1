package com.example.app.config;


import com.example.app.interceptor.AppInterceptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {


    private final ApplicationArguments appArguments;

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
    registry.addInterceptor(new AppInterceptor())
            .addPathPatterns("/console/**")
            .excludePathPatterns(
                    "/app/user/login",
                    "/app/user/register",
                    "/app/user/logout"
            );
}


}
