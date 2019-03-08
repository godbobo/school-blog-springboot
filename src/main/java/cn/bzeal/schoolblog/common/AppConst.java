package cn.bzeal.schoolblog.common;

public class AppConst {

    public static final String APP_TOKEN_HEADER = "Authorization";

    /**
     * 查询文章方式
     */
    public static final int ESSAY_Q_NORMAL = 0; // 普通查询
    public static final int ESSAY_Q_KEY = 1; // 根据关键字 获取文章
    public static final int ESSAY_Q_AUTHOR = 2; // 根据用户 id 查询拥有文章
    public static final int ESSAY_Q_LOVER = 3; // 根据用户 id 查询收藏文章

    /**
     * 查询用户列表
     */
    public static final int QUERY_USERLIST_NORMAL = 0; // 普通查询
    public static final int QUERY_USERLIST_USERNAME = 1; // 根据用户 id 进行查询
    public static final int QUERY_USERLIST_Q = 2; // 根据关键字查询

    /**
     * 返回结果常量
     */
    public static final int RES_SUCCESS = 0;
    public static final String RES_SUCCESS_MSG = "执行成功";

    public static final int RES_FAIL_UNKNOWN = 1000;
    public static final String RES_FAIL_UNKNOWN_MSG = "未知错误";

    public static final int RES_FAIL_NO_TOKEN = 1001;
    public static final String RES_FAIL_NO_TOKEN_MSG = "未解析到token";

    public static final int RES_FAIL_NO_PARAMS = 1002;
    public static final String RES_FAIL_NO_PARAMS_MSG = "未解析到请求参数";

    public static final int RES_FAIL_USER_ERROR = 1011;
    public static final String RES_FAIL_USER_ERROR_MSG = "用户名或密码错误";

}
