package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface CommentService {

    // 新增评论
    String add(QueryModel model, Long currentUserId);

    // 查看某篇文章对应的评论列表（需带有分页功能）
    String lstById(QueryModel model);

    // 新增子评论
    String addSubComment(QueryModel model, Long currentUserId);

}
