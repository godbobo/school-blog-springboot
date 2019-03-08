package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.model.UserModel;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    // 登录请求，查询数据库后生成 token 和 refresh_token
    GlobalResult login(UserModel model);

    // 获取用户信息
    GlobalResult getInfo(Long username);

    /**
     * 传入新增用户的基本信息
     * Query List 顺序：name, college, tel, role
     */
    GlobalResult insertUser(QueryModel model);

    // 获取用户列表
    GlobalResult lst(QueryModel model, HttpServletRequest request);

    // 获取用户创建的话题列表
    GlobalResult lstTopic(UserModel model);

    // 获取用户创建的文章列表
    GlobalResult lstEssay(UserModel model);

    // 获取用户收藏的文章列表
    GlobalResult lstFav(UserModel model);

    // 获取用户收到的消息列表
    GlobalResult lstMessage(UserModel model);

}
