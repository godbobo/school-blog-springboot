package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.model.ArticleModel;
import cn.bzeal.schoolblog.service.ArticleService;
import cn.bzeal.schoolblog.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/essay")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // 获取文章列表
    @RequestMapping("/lst")
    public String lst(ArticleModel model){
        return CommonUtil.response(articleService.lst(model));
    }

    // 查询具体文章
    @RequestMapping("/find")
    public String find(ArticleModel model){
        return CommonUtil.response(articleService.find(model));
    }

}
