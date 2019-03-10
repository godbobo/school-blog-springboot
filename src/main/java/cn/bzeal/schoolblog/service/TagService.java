package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;

import javax.servlet.http.HttpServletRequest;

public interface TagService {

    // 插入新的标签
    GlobalResult add(QueryModel model, Long id);

}
