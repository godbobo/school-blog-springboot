package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;

public interface TopicService {

    // 新增话题
    GlobalResult add(QueryModel model, Long userid);

}
