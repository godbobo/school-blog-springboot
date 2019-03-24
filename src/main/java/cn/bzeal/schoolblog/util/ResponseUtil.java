package cn.bzeal.schoolblog.util;

import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.*;

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
        JsonUtil jsonUtil = new JsonUtil();
        return jsonUtil.toJson(data);
    }

    /**
     * 转换Json时对话题的属性进行筛选
     */
    public static String revertTopic(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Topic.class, "id,name,summary,upt,creator");
        jsonUtil.filter(User.class, "id,name,headimg");
        return jsonUtil.toJson(data);
    }

    /**
     * 转换json时对问藏的属性进行筛选
     */
    public static String revertArticleList(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Article.class, "id,title,summary,view,upt,top,hide,author,topic,tags,lovers");
        jsonUtil.filter(User.class, "id,name,headimg");
        jsonUtil.filter(Topic.class, "id,name");
        jsonUtil.filter(Tag.class, "id,name,color,background");
        return jsonUtil.toJson(data);
    }


    public static String revertArticleDetail(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Article.class, "id,title,summary,content,view,upt,hide,author,tags,topic,comments");
        jsonUtil.filter(User.class, "id,college,headimg,name");
        jsonUtil.filter(Tag.class, "id,name");
        jsonUtil.filter(Topic.class, "id,name");
        jsonUtil.filter(Comment.class, "id,content,upt,creator");
        return jsonUtil.toJson(data);
    }

    /**
     * 转换Json时对标签的属性进行筛选
     */
    public static String revertTag(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Tag.class, "id,name,color,background");
        return jsonUtil.toJson(data);
    }

    public static String revertTopicList(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Tag.class, "id,name,color,background");
        jsonUtil.filter(Topic.class, "id,name,upt,summary,articles,creator,tags,followers");
        jsonUtil.filter(User.class, "id,name");
        jsonUtil.filter(Article.class, "id");
        return jsonUtil.toJson(data);
    }

    public static String revertCommentList(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Comment.class, "id,content,upt,creator");
        jsonUtil.filter(User.class, "id,name,headimg");
        return jsonUtil.toJson(data);
    }

    public static String revertUser(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(User.class, "id,role,name,college,tel,headimg,reg");
        return jsonUtil.toJson(data);
    }

    public static String revertMessageList(HashMap<String, Object> data) {
        JsonUtil jsonUtil  = new JsonUtil();
        jsonUtil.filter(Message.class, "id,type,content,upt,isread,creator");
        jsonUtil.filter(User.class, "id,name,headimg");
        return jsonUtil.toJson(data);
    }

}
