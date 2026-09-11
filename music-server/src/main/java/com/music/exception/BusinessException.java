package com.music.exception;

import lombok.Getter;

@Getter//自动生成get方法
public class BusinessException extends RuntimeException {//继承RuntimeException，表示这是一个运行时异常(自定义异常)
    private final int code;

    public BusinessException(String message) {
        super(message);//调用父类 RuntimeException 的构造方法。
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
