package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.ArticleModel;

public interface ArticleService {

    GlobalResult lst(ArticleModel model);

    GlobalResult find(ArticleModel model);
}
