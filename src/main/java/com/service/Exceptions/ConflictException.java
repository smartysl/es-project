package com.service.Exceptions;

/**
 * @author yushilin
 * @date 2021/4/19 11:33 上午
 */
public class ConflictException extends RuntimeException {
    public ConflictException() {
        super("conflict happens");
    }
}
