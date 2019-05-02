package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface TopicService {

    // 新增话题
    String add(QueryModel model, Long userid);

    // 获取话题列表
    String lstById(Long userid);

    // 根据创建者获取话题列表
    String lstByCreator(QueryModel model);

    // 加入话题
    String follow(Long topicId, Long userId);

    // 根据用户id获取加入的话题列表
    String lstByFollower(QueryModel model);

    // 获取全部话题
    String lst(QueryModel model);

    // 获取用户相关话题
    String lstAboutId(Long userid);

    // 根据id查询话题
    String find(Long topicId, Long userId);

}
