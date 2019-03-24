package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface MessageService {

    // 发送消息
    String send(QueryModel model, Long userId);

    // 获取消息列表
    String lst(QueryModel model, Long userId);

    // 获取和某人的消息记录
    String lstFromTarget(QueryModel model, Long userId);

}
