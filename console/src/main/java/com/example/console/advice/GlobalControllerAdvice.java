package com.example.console.advice;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalControllerAdvice {   // 全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    Response handleControllerException(Exception exception) {
        return new Response(4004, exception.getStackTrace());
    }

}


