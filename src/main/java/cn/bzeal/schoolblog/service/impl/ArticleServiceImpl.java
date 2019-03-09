package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.Article;
import cn.bzeal.schoolblog.domain.ArticleRepository;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.ArticleModel;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.ArticleService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final UserRepository userRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GlobalResult lst(ArticleModel model) {
        GlobalResult result = new GlobalResult();
        // 定义分页，获取全部文章
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow());
        Page<Article> page = articleRepository.findAll(pageable);
        if(page.getTotalElements() > 0){
            // 封装结果
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", AppConst.RES_SUCCESS);
            map.put("msg", AppConst.RES_SUCCESS_MSG);
            HashMap<String, Object> data = new HashMap<>();
            // 裁剪文章数据
            data.put("lst", page.getContent());
            map.put("data", data);
            // 转为 json
            JsonUtil jsonUtil = new JsonUtil();
            jsonUtil.filter(Article.class, "id,title,summary,view,upt,top,hide,author", null);
            jsonUtil.filter(User.class, "id,name,headimg", null);
            String res = jsonUtil.toJson(map);
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(res);
        }
        return result;
    }

    @Override
    public GlobalResult find(ArticleModel model) {
        GlobalResult result = new GlobalResult();
        if(model.getId()!=null){
            Article article = articleRepository.findById(model.getId()).orElse(null);
            if(article != null){
                // 封装结果信息
                HashMap<String, Object> data = new HashMap<>();
                data.put("essay", article);
                HashMap<String, Object> map = CommonUtil.getSuccessResult(data);
                // 转为 json
                JsonUtil jsonUtil = new JsonUtil();
                jsonUtil.filter(Article.class, "id,title,content,summary,view,upt,top,hide,author", null);
                jsonUtil.filter(User.class, "id,name,headimg", null);
                String res = jsonUtil.toJson(map);
                result.setCode(AppConst.RES_SUCCESS);
                result.setMap(res);
                // 阅读次数 +1
                article.setView(article.getView()+1);
                articleRepository.save(article);
            }
        }
        return result;
    }

    @Override
    public GlobalResult insert(QueryModel model, HttpServletRequest request) {
        return null;
    }
}
