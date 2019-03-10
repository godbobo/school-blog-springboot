package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.ArticleModel;
import cn.bzeal.schoolblog.model.QueryModel;

import javax.servlet.http.HttpServletRequest;

public interface ArticleService {

    GlobalResult lst(ArticleModel model);

    GlobalResult find(ArticleModel model);

    GlobalResult add(QueryModel model, Long userid, Long topicid);
}
