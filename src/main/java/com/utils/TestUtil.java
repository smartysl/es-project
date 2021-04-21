package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yushilin
 * @date 2021/4/19 8:10 下午
 */
public class TestUtil {

    public static final Logger log = LoggerFactory.getLogger(TestUtil.class);

    public static void sleep(long time) {
        log.info("[模拟并发] 停顿时间={}", time);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void injectException() {
        log.info("[注入故障]");
        throw new RuntimeException();
    }

}
