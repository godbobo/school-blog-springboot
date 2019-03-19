package cn.bzeal.schoolblog.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; // 时间戳转换时间格式设置
    private static final ObjectMapper mapper;

    JacksonJsonFilter jacksonFilter = new JacksonJsonFilter();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        // 允许对象忽略json中不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许出现单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 忽视为空的属性
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static JsonUtil me() {
        JsonUtil jsonUtil = new JsonUtil();
        return jsonUtil;
    }

    public void filter(Class<?> clazz, String include, String filter) {
        if (clazz == null)
            return;
        if (include != null && include.length() > 0) {
            jacksonFilter.include(clazz, include.split(","));
        }
        if (filter != null && filter.length() > 0) {
            jacksonFilter.filter(clazz, filter.split(","));
        }
        mapper.addMixIn(clazz, jacksonFilter.getClass());
    }

    public String toJson(Object obj) {
        try {
            mapper.setFilterProvider(jacksonFilter);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("转换json字符失败!");
        }
    }

    public <T> T toObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("将json字符转换为对象时失败!");
        }
    }

    public <T> T toObject(String json, TypeReference<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("将json字符转换为对象时失败!");

        }
    }

}