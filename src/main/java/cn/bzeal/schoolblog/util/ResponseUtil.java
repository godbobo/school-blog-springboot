package cn.bzeal.schoolblog.util;

import cn.bzeal.schoolblog.common.ResponseCode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

// 与响应相关的工具类
public class ResponseUtil {

    // 直接设置响应请求时，应该是没有获取到 token
    public static void response(HttpServletResponse response, ResponseCode code) {
        response.setContentType("application/json;utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().print(getResult(code));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 返回错误码，不附加额外数据
     * @param code 错误码类
     * @return 返回json串
     */
    public static String getResult(ResponseCode code) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("state", code);
        return revert(result);
    }

    /**
     * 返回附加额外数据的错误码
     * @param code 错误码类
     * @param data 额外数据
     * @return 返回待转换的HashMap，因为额外数据的json转换很可能有不同需求，不可统一完成
     */
    public static HashMap<String, Object> getResultMap(ResponseCode code, HashMap<String, Object> data) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("state", code);
        result.put("data", data);
        return result;
    }


    /**
     * 转换Map为Json，不使用任何过滤
     */
    public static String revert(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        return jacksonUtil.toJson(data);
    }

    /**
     * 转换Json时对话题的属性进行筛选
     */
    public static String revertTopic(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("TopicFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "summary", "upt", "creator"));
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    /**
     * 转换json时对文章的属性进行筛选
     */
    public static String revertArticleList(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("ArticleFilter", SimpleBeanPropertyFilter.serializeAllExcept("comments"));
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg"));
        filterProvider.addFilter("TopicFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name"));
        filterProvider.addFilter("TagFilter", SimpleBeanPropertyFilter.serializeAllExcept("creator", "articles", "topics"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    public static String revertArticleDetail(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("ArticleFilter", SimpleBeanPropertyFilter.serializeAll());
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg", "college"));
        filterProvider.addFilter("TagFilter", SimpleBeanPropertyFilter.serializeAllExcept("creator", "articles", "topics"));
        filterProvider.addFilter("TopicFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name"));
        filterProvider.addFilter("CommentFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "content", "upt", "creator"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    /**
     * 转换Json时对标签的属性进行筛选
     */
    public static String revertTag(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("TagFilter", SimpleBeanPropertyFilter.serializeAllExcept("creator", "articles", "topics"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    public static String revertTopicList(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("TopicFilter", SimpleBeanPropertyFilter.serializeAllExcept("comments"));
        filterProvider.addFilter("TagFilter", SimpleBeanPropertyFilter.serializeAllExcept("creator", "articles", "topics"));
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg"));
        filterProvider.addFilter("ArticleFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    public static String revertCommentList(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("CommentFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "content", "upt", "creator"));
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    public static String revertUser(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg", "college", "tel", "reg", "role", "sex", "pro", "hobby", "sign", "mail"));
        return jacksonUtil.toJson(filterProvider, data);
    }

    public static String revertMessageList(HashMap<String, Object> data) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("MessageFilter", SimpleBeanPropertyFilter.serializeAllExcept("target"));
        filterProvider.addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "headimg"));
        return jacksonUtil.toJson(filterProvider, data);
    }

}
