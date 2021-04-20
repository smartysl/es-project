package com.service.Exceptions;

/**
 * @author yushilin
 * @date 2021/4/19 3:02 下午
 */
public class ClusterNotHealthException extends RuntimeException {
    public ClusterNotHealthException() {
        super("cluster not health");
    }
}
