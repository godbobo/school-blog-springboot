package cn.bzeal.schoolblog.util;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.Article;
import cn.bzeal.schoolblog.domain.Tag;
import cn.bzeal.schoolblog.domain.Topic;
import cn.bzeal.schoolblog.domain.User;

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
    public static String revertArticle(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Article.class, "id,title,summary,view,upt,top,hide,author,topic", null);
        jsonUtil.filter(User.class, "id,name,headimg", null);
        jsonUtil.filter(Topic.class, "id,name", null);
        return jsonUtil.toJson(data);
    }

    /**
     * 转换Json时对话题的属性进行筛选
     * @param data 相应体
     * @return
     */
    public static String revertTag(HashMap<String, Object> data) {
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Tag.class, "id,name,color,background", null);
        return jsonUtil.toJson(data);
    }

}
