package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;

import javax.servlet.http.HttpServletRequest;

public interface TagService {

    // 插入新的标签
    GlobalResult add(QueryModel model, Long id);

    // 获取用户创建的标签
    GlobalResult lstByUser(Long id);

    // 删除标签
    GlobalResult delete(Long tagid, Long userid);

}
