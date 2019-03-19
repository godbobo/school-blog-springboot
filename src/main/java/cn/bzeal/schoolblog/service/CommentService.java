package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;

public interface CommentService {

    // 新增评论
    GlobalResult add(QueryModel model, Long currentUserId);

    // 查看某篇文章对应的评论列表（需带有分页功能）
    GlobalResult lstById(QueryModel model);

}
