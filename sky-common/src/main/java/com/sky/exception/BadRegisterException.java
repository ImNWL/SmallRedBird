package com.sky.exception;

/**
 * 账号被锁定异常
 */
public class BadRegisterException extends BaseException {

    public BadRegisterException() {
    }

    public BadRegisterException(String msg) {
        super(msg);
    }

}
