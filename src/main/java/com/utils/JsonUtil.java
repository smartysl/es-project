package com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author yushilin
 * @date 2021/4/20 4:52 下午
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String toJson(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    public static <O> O fromJson(String json, Class<O> oClass) {
        try {
            return objectMapper.readValue(json, oClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
