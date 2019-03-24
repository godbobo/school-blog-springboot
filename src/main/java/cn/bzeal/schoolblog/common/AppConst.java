package cn.bzeal.schoolblog.common;

public class AppConst {

    public static final String APP_TOKEN_HEADER = "Authorization";

    /**
     * 用户角色判定
     */
    public static final int USER_STU = 0; // 学生
    public static final int USER_TEACHER = 1; // 教师
    public static final int USER_ADMIN = 2; // 管理员

    /**
     * 查询文章方式
     */
    public static final int ESSAY_Q_NORMAL = 0; // 普通查询
    public static final int ESSAY_Q_KEY = 1; // 根据关键字 获取文章
    public static final int ESSAY_Q_AUTHOR = 2; // 根据用户 id 查询拥有文章
    public static final int ESSAY_Q_LOVER = 3; // 根据用户 id 查询收藏文章

    public static final int ESSAY_LIST_INDEX = 0; // 普通文章列表
    public static final int ESSAY_LIST_ADMIN = 1; // 管理员视图文章列表

    public static final int ESSAY_FIND_INDEX = 0; // 普通用户浏览文章，浏览次数+1
    public static final int ESSAY_FIND_ADMIN = 1; // 管理员浏览文章

    public static final int ESSAY_UPDATE_TOPIC = 0; // 更新所属话题
    public static final int ESSAY_UPDATE_COMMENTS = 1; // 更新评论列表
    public static final int ESSAY_UPDATE_TAGS = 2; // 更新评论列表
    public static final int ESSAY_UPDATE_LOVERS = 3; // 更新收藏者列表

    /**
     * 查询用户列表
     */
    public static final int QUERY_USERLIST_NORMAL = 0; // 普通查询
    public static final int QUERY_USERLIST_USERNAME = 1; // 根据用户 id 进行查询
    public static final int QUERY_USERLIST_Q = 2; // 根据关键字查询

    public static final int USER_FOLLOW = 0; // 关注用户
    public static final int USER_FOLLOW_CANCEL = 1; // 取消关注用户

    /**
     * 评论相关
     */
    public static final int COMMENT_ADD_ESSAY = 0; // 添加评论到文章
    public static final int COMMENT_ADD_TOPIC = 1; // 添加评论到话题
    public static final int COMMENT_ADD_COMMENT = 2; // 添加评论到评论

    public static final int COMMENT_LST_ESSAY = 0; // 文章对应的评论列表
    public static final int COMMENT_LST_TOPIC = 1; // 话题对应的评论列表


    /**
     * 消息相关
     */
    public static final int MESSAGE_TYPE_SYSTEM = 0; // 系统消息
    public static final int MESSAGE_TYPE_USER = 1; // 用户消息
    public static final int MESSAGE_TYPE_QUERY = 2; // 请求消息

    public static final int MESSAGE_LIST_READ = 0; // 已读消息列表
    public static final int MESSAGE_LIST_UNREAD = 1; // 未读消息列表

}
