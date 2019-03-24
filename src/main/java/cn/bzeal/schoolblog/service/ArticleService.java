package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;

public interface ArticleService {

    String lst(QueryModel model);

    String lstAbout(Long topicid);

    // 根据话题id获取文章
    String lstByTopicId(QueryModel model);

    // 根据用户id获取收藏文章
    String lstByLover(QueryModel model);

    String lstByAuthorId(QueryModel model, Long userid);

    String indexLst(QueryModel model);

    String find(QueryModel model, Long userId);

    String add(QueryModel model, Long userid, Long topicid);

    String update(QueryModel model);

    String updateRelation(QueryModel model);

    String likOrNot(Long articleId, Long currentUserId);

}
