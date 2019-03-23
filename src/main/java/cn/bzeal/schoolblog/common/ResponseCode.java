package cn.bzeal.schoolblog.common;

public final class ResponseCode {

    private ResponseCode(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    // 所有错误码必须按照规范定义，原则上要与前端对应，且划分模块
    // 成功返回码均为 0
    // 错误属性名：
    // N 表示无提示 T 表示前端需要向用户展示提示
    // 后面接模块名 具体错误类型
    // 错误码说明：
    // 第 1 位：0表示无提示，1表示有提示
    // 第 2-3 位：从1开始递增，表示具体模块
    // 第 4-7 位： 从1开始递增，表示具体错误

    // 全局定义
    public static final ResponseCode N_SUCCESS = new ResponseCode("00", "操作完成");
    public static final ResponseCode T_SUCCESS = new ResponseCode("10", "操作完成");
    public static final ResponseCode N_APP_NO_TOKEN = new ResponseCode("0010002", "未解析到token");
    public static final ResponseCode N_APP_EXPIRES_TOKEN = new ResponseCode("0010003", "token已过期");
    public static final ResponseCode N_APP_NO_PARAMS = new ResponseCode("0010004", "参数不完整");
    public static final ResponseCode T_APP_NO_PARAMS = new ResponseCode("1010004", "参数不完整");
    public static final ResponseCode T_APP_FAIL_SAVE = new ResponseCode("1010005", "保存失败");
    public static final ResponseCode T_APP_FAIL_UPDATE = new ResponseCode("1010006", "修改失败");
    public static final ResponseCode T_APP_FAIL_DELETE = new ResponseCode("1010007", "删除失败");
    public static final ResponseCode T_APP_NULL_CONTENT = new ResponseCode("1010008", "未找到指定内容");
    public static final ResponseCode T_APP_EMPTY_RESULT = new ResponseCode("1010009", "查询结果为空");
    public static final ResponseCode N_APP_EMPTY_RESULT = new ResponseCode("0010009", "查询结果为空");
    public static final ResponseCode T_APP_SUCCESS_UPDATE = new ResponseCode("10", "修改成功");
    public static final ResponseCode T_APP_SUCCESS_ADD = new ResponseCode("10", "添加成功");
    public static final ResponseCode T_APP_SUCCESS_DELETE = new ResponseCode("10", "删除成功");

    // 文章模块
    public static final ResponseCode N_ESSAY_EMPTY_LIST = new ResponseCode("0020001", "列表为空");
    public static final ResponseCode T_ESSAY_EMPTY_FIND = new ResponseCode("1020002", "未找到指定文章");
    public static final ResponseCode T_ESSAY_FAIL_LIKE = new ResponseCode("1020003", "收藏失败");
    public static final ResponseCode T_ESSAY_NO_AUTHOR = new ResponseCode("1020004", "未查找到作者信息");

    // 用户模块
    public static final ResponseCode T_USER_EMPTY_FIND = new ResponseCode("1030001", "未找到指定用户");
    public static final ResponseCode T_USER_SUCCESS_LOGIN = new ResponseCode("10", "登录成功");
    public static final ResponseCode T_USER_FAIL_LOGIN = new ResponseCode("1030003", "登录失败");
    public static final ResponseCode T_USER_CONFLICT_FOLLOW = new ResponseCode("1030004", "无法关注自己");
    public static final ResponseCode T_USER_SUCCESS_FOLLOW = new ResponseCode("10", "关注成功");

    // 话题模块
    public static final ResponseCode N_TOPIC_EMPTY_FIND = new ResponseCode("0040001", "未找到指定话题");

    // 评论模块
    public static final ResponseCode T_COMMENT_NO_AUTHOR = new ResponseCode("1050001", "未查找到创建者信息");
    public static final ResponseCode T_COMMENT_FAIL_ADD = new ResponseCode("1050002", "评论失败");

    // 标签模块
    public static final ResponseCode T_TAG_DUPLICATE_NAME = new ResponseCode("1060001", "已存在相同标签");


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
