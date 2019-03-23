package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface ArticleService {

    String lst(QueryModel model);

    String lstAbout(Long topicid);

    String indexLst(QueryModel model);

    String find(QueryModel model);

    String add(QueryModel model, Long userid, Long topicid);

    String update(QueryModel model);

    String updateRelation(QueryModel model);

    String likOrNot(Long articleId, Long currentUserId);
}
