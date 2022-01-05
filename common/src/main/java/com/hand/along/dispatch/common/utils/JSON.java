package com.hand.along.dispatch.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hand.along.dispatch.common.exceptions.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhilong.deng@hand-china.com
 */
@Slf4j
@Component
public final class JSON {
    private static ObjectMapper objectMapper;

    private JSON() {
    }

    public static <T> T toObj(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException var3) {
            throw new CommonException("dispatch.err.jackson.read", var3);
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (IOException var3) {
            throw new CommonException("dispatch.err.jackson.read", var3);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (!StringUtils.isBlank(json) && clazz != null) {
            try {
                return getObjectMapper().readValue(json, clazz);
            } catch (Exception var3) {
                log.error(var3.getMessage(), var3);
                return null;
            }
        } else {
            return null;
        }
    }

    public static ArrayNode toArray(String json) {
        try {
            return objectMapper.readValue(json, ArrayNode.class);
        } catch (IOException var3) {
            throw new CommonException("dispatch.err.jackson.read", var3);
        }
    }

    public static <T> String toJson(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException var2) {
            throw new CommonException("error.jackson.write", var2);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(json, valueTypeRef);
        } catch (IOException var3) {
            throw new CommonException("dispatch.err.jackson.read", var3);
        }
    }

    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(List.class, new Class[]{clazz});

        try {
            return (List) getObjectMapper().readValue(json, javaType);
        } catch (IOException var4) {
            log.error(var4.getMessage(), var4);
            return new ArrayList();
        }
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSON.objectMapper = objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

