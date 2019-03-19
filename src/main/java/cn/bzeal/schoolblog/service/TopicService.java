package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;

public interface TopicService {

    // 新增话题
    GlobalResult add(QueryModel model, Long userid);

    // 获取话题列表
    GlobalResult lstById(Long userid);

    // 获取全部话题
    GlobalResult lst(QueryModel model);

    // 获取用户相关话题
    GlobalResult lstAboutId(Long userid);

}
