package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.ArticleService;
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
                return defaultResult();
            }else {
                return articleService.lst(model);
            }
        }
        // 查询普通视图文章列表
        return articleService.indexLst(model);
    }

    // 获取指定话题下相关文章
    @RequestMapping("lstAbout")
    public String lstAbout(QueryModel model) {
        if (model.getTopic().getId() == null) {
            return defaultResult();
        }
        return articleService.lstAbout(model.getTopic().getId());
    }

    // 查询具体文章
    @RequestMapping("/find")
    public String find(QueryModel model){
        if (model.getArticle().getId() == null) {
            return defaultResult();
        }
        return articleService.find(model);
    }

    // 收藏文章
    @RequestMapping("/like")
    public String like(QueryModel model){
        String currentUserId = getRequest().getAttribute("uid").toString();
        if (StringUtils.isBlank(currentUserId) || model.getArticle().getId() == null) {
            return defaultResult();
        }
        return articleService.likOrNot(model.getArticle().getId(), Long.parseLong(currentUserId));
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
            return defaultResult();
        }
        Long topicid = model.getTopic().getId();
        return articleService.add(model, Long.parseLong(userid), topicid);
    }

    // 修改文章基础内容
    @RequestMapping("/update")
    public String update(QueryModel model) {
        if (model.getArticle() == null) {
            return defaultResult();
        }
        return articleService.update(model);
    }

    // 修改文章关联表信息
    @RequestMapping("/updateRelation")
    public String updateRelation(QueryModel model) {
        return articleService.updateRelation(model);
    }

}
