package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface TagService {

    // 插入新的标签
    String add(QueryModel model, Long id);

    // 获取用户创建的标签
    String lstByUser(Long id);

    // 删除标签
    String delete(Long tagid, Long userid);

}
