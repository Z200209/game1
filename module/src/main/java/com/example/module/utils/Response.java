package com.example.module.utils;

import lombok.Getter;

@Getter
public class Response<T> {
    private final ResponseStatus status = new ResponseStatus();
    private final T result;

    public Response(int status) {
        this.status.setCode(status);
        this.status.setMsg(ResponseCode.getMsg(status));
        this.result = null;
    }

    public Response(int status, T result) {
        this.status.setCode(status);
        this.status.setMsg(ResponseCode.getMsg(status));
        this.result = result;
    }
    
    public Response(int status, String customMsg) {
        this.status.setCode(status);
        this.status.setMsg(customMsg);
        this.result = null;
    }
} 