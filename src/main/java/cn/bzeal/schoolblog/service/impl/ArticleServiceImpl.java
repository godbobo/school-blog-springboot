package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.ArticleService;
import cn.bzeal.schoolblog.util.CommonUtil;
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
import java.util.Collections;
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

    private final MessageRepository messageRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository, TopicRepository topicRepository, TagRepository tagRepository, CommentRepository commentRepository, MessageRepository messageRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public String lst(QueryModel model) {
        // 定义分页，获取全部文章
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
        Page<Article> page = articleRepository.findAll(pageable);
        HashMap<String, Object> data = new HashMap<>();
        data.put("lst", page.getContent());
        data.put("total", page.getTotalElements());
        return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
    }

    @Override
    public String lstAbout(Long topicid) {
        Topic topic = topicRepository.findById(topicid).orElse(null);
        if (topic != null) {
            HashMap<String, Object> data = new HashMap<>();
            // 获取相关列表
            List<Article> list = new ArrayList<>();
            Collections.shuffle(topic.getArticles()); // 打乱顺序
            for (int i = 0; i < topic.getArticles().size(); i++) {
                if (i == 4) {
                    break;
                }
                list.add(topic.getArticles().get(i)); // 获取话题内文章
            }
            data.put("aboutlst", list);
            return ResponseUtil.revertArticleList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.N_TOPIC_EMPTY_FIND);
    }

    // 首页包含多个文章数据 需一次性获取完毕后全部返回
    @Override
    public String indexLst(QueryModel model) {
        List<Article> topList = null;
        if (model.getPage() == 0) {
            // 第一页需要获取置顶文章
            topList = articleRepository.findByTop(1);
        }
        // 获取正文列表
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
        Page<Article> page = articleRepository.findByTopNot(1, pageable); // 获取非置顶文章
        // 获取推荐列表
        Pageable recommandPageable = PageRequest.of(0, 5, new Sort(Sort.Direction.DESC, "view"));
        List<Article> recommandList = articleRepository.findAll(recommandPageable).getContent();
        HashMap<String, Object> data = new HashMap<>();
        data.put("lst", page.getContent());
        data.put("toplst", topList);
        data.put("recommandlst", recommandList);
        data.put("total", page.getTotalElements());
        return ResponseUtil.revertArticleList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
    }

    @Override
    public String find(QueryModel model) {
        Article article = articleRepository.findById(model.getArticle().getId()).orElse(null);
        if (article != null) {
            User u = article.getAuthor();
            HashMap<String, Object> data = new HashMap<>();
            data.put("essay", article);
            data.put("isfav", u.getFavs().contains(article));
            if (model.getQueryType() == AppConst.ESSAY_FIND_INDEX) {
                // 普通视图下浏览次数+1
                article.setView(article.getView() + 1);
                articleRepository.save(article);
            }
            return ResponseUtil.revertArticleDetail(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_ESSAY_EMPTY_FIND);
    }

    @Override
    public String add(QueryModel model, Long userid, Long topicid) {
        User user = userRepository.findById(userid).orElse(null);
        if (user != null) {
            Article article = model.getArticle();
            article.setAuthor(user);
            article.setUpt(new Timestamp(System.currentTimeMillis()));
            topicRepository.findById(topicid).ifPresent(article::setTopic);
            List<Tag> tagList;
            if (StringUtils.isNotBlank(model.getJsonRest())) {
                tagList = tagRepository.findByIdIn(CommonUtil.getLongListFromJsonList(model.getJsonRest()));
                article.setTags(tagList);
                for (Tag t : tagList) {
                    t.getArticles().add(article);
                }
            }
            if (articleRepository.save(article) != null) {
                return ResponseUtil.getResult(ResponseCode.T_SUCCESS);
            } else {
                return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_SAVE);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_ESSAY_NO_AUTHOR);
    }

    @Override
    public String update(QueryModel model) {
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
                return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_UPDATE);
            } else {
                return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_UPDATE);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_ESSAY_EMPTY_FIND);
    }

    @Override
    public String updateRelation(QueryModel model) {
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
            ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_UPDATE);
        }
        return ResponseUtil.getResult(ResponseCode.T_ESSAY_EMPTY_FIND);
    }

    @Override
    public String likOrNot(Long articleId, Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElse(null);
        Article article = articleRepository.findById(articleId).orElse(null);
        if (user != null && article != null) {
            if (article.getLovers().contains(user)) {
                article.getLovers().remove(user);
            } else {
                article.getLovers().add(user);
            }
            if (articleRepository.save(article) != null) {
                // 保存成功后发送消息
                if (!user.getId().equals(article.getAuthor().getId())) {
                    sendMessage(user, article.getAuthor(), article.getTitle());
                }
                return ResponseUtil.getResult(ResponseCode.T_SUCCESS);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_NULL_CONTENT);
    }

    private void sendMessage(User creator, User target, String title) {
        Message message = new Message();
        message.setUpt(new Timestamp(System.currentTimeMillis()));
        message.setCreator(creator);
        message.setTarget(target);
        message.setType(AppConst.MESSAGE_TYPE_SYSTEM);
        message.setContent(creator.getName() + " 收藏了你的文章：《" + title + "》");
        messageRepository.save(message);
    }

}
