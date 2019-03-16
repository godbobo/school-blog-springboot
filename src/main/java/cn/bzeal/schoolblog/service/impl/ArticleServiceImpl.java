package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.ArticleModel;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.ArticleService;
import cn.bzeal.schoolblog.util.JsonUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final UserRepository userRepository;

    private final TopicRepository topicRepository;

    private final TagRepository tagRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository, TopicRepository topicRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public GlobalResult lst(QueryModel model) {
        GlobalResult result = new GlobalResult();
        // 定义分页，获取全部文章
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
        Page<Article> page = articleRepository.findAll(pageable);
        if(page.getTotalElements() > 0){
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", page.getContent());
            data.put("total", page.getTotalElements());
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revertArticle(ResponseUtil.getSuccessResult(data)));
        }
        return result;
    }

    // 首页包含多个文章数据 需一次性获取完毕后全部返回
    @Override
    public GlobalResult indexLst(QueryModel model) {
        GlobalResult result = new GlobalResult();
        List<Article> topList = null;
        if (model.getPage() == 0) {
            // 第一页需要获取置顶文章
            topList = articleRepository.findByTop(1);
        }
        // 获取正文列表
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
        Page<Article> page = articleRepository.findAll(pageable);
        // 获取推荐列表
        Pageable recommandPageable = PageRequest.of(0, 5, new Sort(Sort.Direction.DESC, "view"));
        List<Article> recommandList = articleRepository.findAll(recommandPageable).getContent();
        HashMap<String, Object> data = new HashMap<>();
        data.put("lst", page.getContent());
        data.put("toplst", topList);
        data.put("recommandlst", recommandList);
        data.put("total", page.getTotalElements());
        result.setCode(AppConst.RES_SUCCESS);
        result.setMap(ResponseUtil.revertArticle(ResponseUtil.getSuccessResult(data)));
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
                HashMap<String, Object> map = ResponseUtil.getSuccessResult(data);
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
    public GlobalResult add(QueryModel model, Long userid, Long topicid) {
        GlobalResult result = new GlobalResult();
        User user = userRepository.findById(userid).orElse(null);
        if(user!=null){
            Article article = model.getArticle();
            article.setAuthor(user);
            article.setUpt(new Timestamp(System.currentTimeMillis()));
            // TODO 先在前端尝试只传tagid完成保存功能，如果不行则在后端循环查找数据获取tag后再保存
            topicRepository.findById(topicid).ifPresent(article::setTopic);
            List<Tag> tagList = new ArrayList<>();
            if(StringUtils.isNotBlank(model.getJsonRest())){
                tagList = tagRepository.findByIdIn(TopicServiceImpl.getLongListFromJsonList(model.getJsonRest()));
                article.setTags(tagList);
                for(Tag t: tagList){
                    t.getArticles().add(article);
                }
            }
            setResponse(result, articleRepository.save(article) != null && !TopicServiceImpl.isAnyNull(tagRepository.saveAll(tagList)));
        }
        return result;
    }

    @Override
    public GlobalResult update(QueryModel model) {
        GlobalResult  result = new GlobalResult();
        Article uat = model.getArticle();
        Article article = articleRepository.findById(uat.getId()).orElse(null);
        // 对文章基础内容的修改
        if (article != null) {
            if (StringUtils.isNotBlank(uat.getContent())) {
                article.setContent(uat.getContent());
            }
            if (uat.getHide() != null) {
                article.setHide(uat.getHide());
            }
            if (StringUtils.isNotBlank(uat.getSummary())) {
                article.setSummary(uat.getSummary());
            }
            if (uat.getTop() != null) {
                article.setTop(uat.getTop());
            }
            if (StringUtils.isNotBlank(uat.getTitle())) {
                article.setTitle(uat.getTitle());
            }
            if (uat.getView() != null) {
                article.setView(uat.getView());
            }
            // 修改更新时间
            article.setUpt(new Timestamp(System.currentTimeMillis()));
            if (articleRepository.save(article) != null) {
                result.setCode(AppConst.RES_SUCCESS);
                result.setMap(ResponseUtil.revert(ResponseUtil.getSuccessResult(null)));
            }
        }
        return result;
    }

    @Override
    public GlobalResult updateRelation(QueryModel model) {
        GlobalResult result = new GlobalResult();
        Article article = articleRepository.findById(model.getArticle().getId()).orElse(null);
        if (article != null) {
            // 判断是否更新所属话题
            if (model.getQueryType() == AppConst.ESSAY_UPDATE_TOPIC) {
                if (model.getTopic() != null) {
                    // 更新所属话题
                    topicRepository.findById(model.getTopic().getId()).ifPresent(article::setTopic);
                } else {
                    article.setTopic(null);
                }
            } else if (model.getQueryType() == AppConst.ESSAY_UPDATE_COMMENTS) {
                List<String> qls = model.getQueryList();
                if (qls != null && qls.size() > 0) {
                    List<Long> ids = new ArrayList<>();
                    for (String id : qls) {
                        ids.add(Long.parseLong(id));
                    }
                    article.setComments(commentRepository.findByIdIn(ids));
                }
            } else if (model.getQueryType() == AppConst.ESSAY_UPDATE_LOVERS) {
                List<String> qls = model.getQueryList();
                if (qls != null && qls.size() > 0) {
                    List<Long> ids = new ArrayList<>();
                    for (String id : qls) {
                        ids.add(Long.parseLong(id));
                    }
                    article.setLovers(userRepository.findByIdIn(ids));
                }
            } else if (model.getQueryType() == AppConst.ESSAY_UPDATE_TAGS) {
                List<String> qls = model.getQueryList();
                if (qls != null && qls.size() > 0) {
                    List<Long> ids = new ArrayList<>();
                    for (String id : qls) {
                        ids.add(Long.parseLong(id));
                    }
                    article.setTags(tagRepository.findByIdIn(ids));
                }
            }
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revert(ResponseUtil.getSuccessResult(null)));
        }
        return result;
    }

    static void setResponse(GlobalResult result, boolean b) {
        if(b){
            JsonUtil jsonUtil = new JsonUtil();
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(jsonUtil.toJson(ResponseUtil.getSuccessResult(null)));
        }
    }

}
