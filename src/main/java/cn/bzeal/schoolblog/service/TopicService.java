package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface TopicService {

    // 新增话题
    String add(QueryModel model, Long userid);

    // 获取话题列表
    String lstById(Long userid);

    // 获取全部话题
    String lst(QueryModel model);

    // 获取用户相关话题
    String lstAboutId(Long userid);

}
