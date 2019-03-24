package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    // 登录请求，查询数据库后生成 token 和 refresh_token
    String login(QueryModel model);

    // 获取用户信息
    String getInfo(Long username);

    // 统计用户信息
    String countUser(Long userid, Long currentUserId);

    /**
     * 传入新增用户的基本信息
     * Query List 顺序：name, college, tel, role
     */
    String insertUser(QueryModel model);

    // 关注或取消关注用户
    String followOrNot(QueryModel model, Long currentUserId);

    // 获取用户列表
    String lst(QueryModel model, Long userId);

    // 删除用户
    String deleteUser(Long userid);

}
