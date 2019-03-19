package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.ArticleModel;
import cn.bzeal.schoolblog.model.QueryModel;

import javax.servlet.http.HttpServletRequest;

public interface ArticleService {

    GlobalResult lst(QueryModel model);

    GlobalResult lstAbout(Long topicid);

    GlobalResult indexLst(QueryModel model);

    GlobalResult find(QueryModel model);

    GlobalResult add(QueryModel model, Long userid, Long topicid);

    GlobalResult update(QueryModel model);

    GlobalResult updateRelation(QueryModel model);

    GlobalResult likOrNot(Long articleId, Long currentUserId);
}
