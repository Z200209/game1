package com.example.app.interceptor;


import com.alibaba.fastjson.JSON;
import com.example.module.entity.Sign;
import com.example.module.entity.User;
import com.example.module.service.UserService;
import com.example.module.utils.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;

@Component
@Slf4j
public class AppInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //从Cookie中获取token
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 验证token
        if (token == null || token.isEmpty()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            log.info("未找到auth_token Cookie");
            response.getWriter().write(JSON.toJSONString(new Response(1002)));
            return false;
        }

        Sign sign;
        try {
            // 解析token - 这部分可能会抛出Base64解码或JSON解析异常
            String jsonStr = new String(Base64.getUrlDecoder().decode(token));
            sign = JSON.parseObject(jsonStr, Sign.class);
        } catch (Exception e) {
            // 处理token解析异常
            log.error("Token解析失败", e);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new Response<>(4004)));
            return false;
        }

        // 验证token是否过期
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        if (sign.getExpirationTime() < currentTime) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new Response(1002)));
            log.info("token已过期: expirationTime={}, currentTime={}", sign.getExpirationTime(), currentTime);
            return false;
        }

        try {
            User user = userService.getUserById(sign.getId());
            if (user == null) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(new Response(1002)));
                log.info("token中的用户ID不存在: {}", sign.getId());
                return false;
            }
        } catch (Exception e) {
            // 处理数据库操作异常
            log.error("用户验证失败", e);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new Response<>(4004)));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("postHandle: " + request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("afterCompletion: " + request.getRequestURI());
    }

}