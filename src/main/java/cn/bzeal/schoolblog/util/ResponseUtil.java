package cn.bzeal.schoolblog.util;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.*;

import java.util.HashMap;

// 与响应相关的工具类
public class ResponseUtil {

    /**
     * 返回成功信息
     * @param data 特定接口的数据
     * @return
     */
    public static HashMap<String, Object> getSuccessResult(HashMap<String, Object> data) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", AppConst.RES_SUCCESS);
        map.put("msg", AppConst.RES_SUCCESS_MSG);
        map.put("data", data);
        return map;
    }

    /**
     * 转换Map为Json，不使用任何过滤
     * @param data
     * @return
     */
    public static String revert(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        return jsonUtil.toJson(data);
    }

    /**
     * 转换Json时对话题的属性进行筛选
     * @param data 相应体
     * @return
     */
    public static String revertTopic(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Topic.class, "id,name,summary", null);
        return jsonUtil.toJson(data);
    }

    /**
     * 转换json时对问藏的属性进行筛选
     * @param data
     * @return
     */
    public static String revertArticleList(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Article.class, "id,title,summary,view,upt,top,hide,author,topic,tags,lovers", null);
        jsonUtil.filter(User.class, "id,name,headimg", null);
        jsonUtil.filter(Topic.class, "id,name", null);
        jsonUtil.filter(Tag.class, "id,name,color,background", null);
        return jsonUtil.toJson(data);
    }


    public static String revertArticleDetail(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Article.class, "id,title,summary,content,view,upt,hide,author,tags,topic,comments", null);
        jsonUtil.filter(User.class, "id,college,headimg,name", null);
        jsonUtil.filter(Tag.class, "id,name", null);
        jsonUtil.filter(Topic.class, "id,name", null);
        jsonUtil.filter(Comment.class, "id,content,upt,creator", null);
        return jsonUtil.toJson(data);
    }

    /**
     * 转换Json时对标签的属性进行筛选
     * @param data 相应体
     * @return
     */
    public static String revertTag(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Tag.class, "id,name,color,background", null);
        return jsonUtil.toJson(data);
    }

    /**
     * 转换json时对话题等对象的属性进行筛选
     * @param data
     * @return
     */
    public static String revertTopicTagAuthorArticle(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Tag.class, "id,name,color,background", null);
        jsonUtil.filter(Topic.class, "id,name,upt,articles,creator,tags,followers", null);
        jsonUtil.filter(User.class, "id,name", null);
        jsonUtil.filter(Article.class, "id", null);
        return jsonUtil.toJson(data);
    }

    public static String revertCommentList(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Comment.class, "id,content,upt,creator", null);
        jsonUtil.filter(User.class, "id,name,headimg", null);
        return jsonUtil.toJson(data);
    }

}
