package cn.source.common.utils;

import cn.source.common.exception.ServiceException;
import cn.source.common.utils.bean.SpringBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author zhl
 * JSON 工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    private static final ObjectMapper objectMapper = SpringBean.getBean(ObjectMapper.class);

    public static String toJsonString(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }

        if (object instanceof MultipartFile[]) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (bytes.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static <T> Map<String, T> parseMap(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(text, new TypeReference<Map<String, T>>() {
            });
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static <T> List<T> parseArray(String text, Class<List> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}
