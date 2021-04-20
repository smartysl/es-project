package com.service.Exceptions;

/**
 * @author yushilin
 * @date 2021/4/20 7:55 下午
 */
public class KafkaSendException extends RuntimeException {
    public KafkaSendException() {
        super("MQ send fail");
    }
}
