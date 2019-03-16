package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.ArticleModel;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.ArticleService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/essay")
public class ArticleController extends BaseController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 获取文章列表(管理员模式)
    @RequestMapping("/lst")
    public String lst(QueryModel model){
        if (model.getQueryType() == AppConst.ESSAY_LIST_ADMIN) { // 查询管理员视图文章列表
            String userid = getRequest().getAttribute("uid").toString();
            Integer role = (Integer) getRequest().getAttribute("role");
            if (StringUtils.isBlank(userid) || role == null || role < AppConst.USER_ADMIN) {
                return CommonUtil.response(new GlobalResult());
            }else {
                return CommonUtil.response(articleService.lst(model));
            }
        }
        // 查询普通视图文章列表
        return CommonUtil.response(articleService.indexLst(model));
    }

    // 查询具体文章
    @RequestMapping("/find")
    public String find(ArticleModel model){
        return CommonUtil.response(articleService.find(model));
    }

    // 新增文章
    @RequestMapping("/add")
    public String add(QueryModel model) {
        // 验证参数是否完整
        String title = model.getArticle().getTitle();
        String summary = model.getArticle().getSummary();
        String content = model.getArticle().getContent();
        String userid = getRequest().getAttribute("uid").toString();
        if(StringUtils.isAnyBlank(title, summary, content, userid)){
            return CommonUtil.response(new GlobalResult());
        }
        Long topicid = model.getTopic().getId();
        return CommonUtil.response(articleService.add(model, Long.parseLong(userid), topicid));
    }

    // 修改文章基础内容
    @RequestMapping("/update")
    public String update(QueryModel model) {
        if (model.getArticle() == null) {
            return CommonUtil.response(new GlobalResult());
        }
        return CommonUtil.response(articleService.update(model));
    }

    // 修改文章关联表信息
    @RequestMapping("/updateRelation")
    public String updateRelation(QueryModel model) {
        return CommonUtil.response(articleService.updateRelation(model));
    }

}
