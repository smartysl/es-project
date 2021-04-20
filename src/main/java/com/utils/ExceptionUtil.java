package com.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author yushilin
 * @date 2021/4/20 3:29 下午
 */
public class ExceptionUtil {
    public static String getStackTrace(Throwable throwable) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        } catch (Exception e) {
            return "";
        }
    }
}
