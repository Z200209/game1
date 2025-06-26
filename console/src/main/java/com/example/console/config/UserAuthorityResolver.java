package com.example.console.config;

import com.alibaba.fastjson.JSON;
import com.example.console.annotations.VerifiedUser;
import com.example.module.entity.Sign;
import com.example.module.entity.User;
import com.example.module.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 处理@VerifiedUser注解，从Cookie中解析用户身份
 */
@Slf4j
public class UserAuthorityResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserService userService;

    public UserAuthorityResolver(ApplicationArguments appArguments) {
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 检查参数类型是否为User并且有@VerifiedUser注解
        return parameter.getParameterType().equals(User.class) 
                && parameter.hasParameterAnnotation(VerifiedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest,
                                 WebDataBinderFactory binderFactory) {
        try {
            // 从请求中获取Cookie
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            Cookie[] cookies = request.getCookies();
            
            if (cookies == null) {
                log.warn("未找到任何Cookie");
                return null;
            }
            
            // 查找auth_token Cookie
            String authToken = null;
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    authToken = cookie.getValue();
                    break;
                }
            }
            
            if (authToken == null || authToken.isEmpty()) {
                log.warn("未找到auth_token Cookie");
                return null;
            }
            
            log.debug("解析用户身份，authToken: {}", authToken);
            
            // 解析token
            byte[] bytes = Base64.getUrlDecoder().decode(authToken);
            String json = new String(bytes, StandardCharsets.UTF_8);
            Sign parsedSign = JSON.parseObject(json, Sign.class);
            
            // 验证签名是否过期
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            if (parsedSign.getExpirationTime() < currentTime) {
                log.warn("登录已过期: expirationTime={}, currentTime={}", parsedSign.getExpirationTime(), currentTime);
                return null;
            }
            
            // 获取用户信息
            User user = userService.getUserById(parsedSign.getId());
            if (user == null) {
                log.warn("未找到用户: userId={}", parsedSign.getId());
                return null;
            }
            
            log.info("成功解析用户身份: userId={}, username={}", user.getId(), user.getName());
            return user;
        } catch (Exception e) {
            log.error("解析用户身份失败", e);
            return null;
        }
    }
} 