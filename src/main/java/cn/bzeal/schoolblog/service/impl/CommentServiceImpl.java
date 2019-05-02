package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.CommentVo;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.CommentService;
import cn.bzeal.schoolblog.util.MessagePushService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;
    private final MessagePushService pushService;
    private final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository,
                              ArticleRepository articleRepository, TopicRepository topicRepository,
                              MessageRepository messageRepository, MessagePushService pushService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
        this.pushService = pushService;
    }


    @Override
    public String add(QueryModel model, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser != null) {
            Comment comment = model.getComment();
            comment.setUpt(new Timestamp(System.currentTimeMillis()));
            // 一条评论与用户表、文章表、话题表进行关联
            comment.setCreator(currentUser);
            if (comment.getType() == AppConst.COMMENT_ADD_ESSAY) {
                articleRepository.findById(comment.getArticle().getId()).ifPresent(comment::setArticle);
            } else if (comment.getType() == AppConst.COMMENT_ADD_TOPIC) {
                topicRepository.findById(comment.getTopic().getId()).ifPresent(comment::setTopic);
            }
            Comment cmt = commentRepository.save(comment); // 评论表会管理其他列表
            // 插入成功后向作者发送通知消息
            if (cmt != null) {
                Article article = cmt.getArticle();
                Topic topic = cmt.getTopic();
                User targetUser;
                if (article != null) {
                    targetUser = article.getAuthor();
                    sendMessage(currentUser, targetUser, cmt.getType(), article, null);
                } else if (topic != null) {
                    targetUser = topic.getCreator();
                    sendMessage(currentUser, targetUser, cmt.getType(), null, topic);
                }
                return ResponseUtil.getResult(ResponseCode.T_COMMENT_SUCCESS_ADD);
            } else {
                return ResponseUtil.getResult(ResponseCode.T_COMMENT_FAIL_ADD);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_COMMENT_NO_AUTHOR);
    }

    @Override
    public String lstById(QueryModel model) {
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
        HashMap<String, Object> data = new HashMap<>();
        Page<Comment> page = null;
        if (model.getQueryType() == AppConst.COMMENT_LST_ESSAY) {
            Article article = articleRepository.findById(model.getArticle().getId()).orElse(null);
            if (article != null) {
                page = commentRepository.findByArticle(article, pageable);
            }
        } else if (model.getQueryType() == AppConst.COMMENT_LST_TOPIC) {
            Topic topic = topicRepository.findById(model.getTopic().getId()).orElse(null);
            if (topic != null) {
                page = commentRepository.findByTopic(topic, pageable);
            }
        }
        // 处理结果
        if (page != null) {
            List<Map<String, Object>> commentList = new ArrayList<>();
            for(Comment c : page.getContent()){
                // 第一层循环，获取直属评论列表
                Map<String, Object> d = new HashMap<>();
                d.put("id", c.getId());
                d.put("content", c.getContent());
                d.put("upt", c.getUpt());
                d.put("creator", c.getCreator());
                // 读取子评论下的所有评论
                List<CommentVo> subCommentList = new ArrayList<>();
                loopComment(subCommentList, c.getChilds());
                d.put("childs", subCommentList);
                commentList.add(d);
            }
            data.put("commentlst", commentList);
            data.put("total", page.getTotalElements());
            return ResponseUtil.revertCommentList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.N_SUCCESS);
    }

    @Override
    public String addSubComment(QueryModel model, Long currentUserId) {
        // 找到父级评论的创建者
        User pUser = userRepository.findById(model.getComment().getCreator().getId()).orElse(null);
        // 找到父级评论
        Comment pComment = commentRepository.findById(model.getComment().getId()).orElse(null);
        // 找到当前用户
        User user = userRepository.findById(currentUserId).orElse(null);
        if (pUser != null && pComment != null && user!= null){
            Comment comment = new Comment();
            comment.setCreator(user);
            comment.setPComment(pComment);
            comment.setType(AppConst.COMMENT_ADD_COMMENT);
            comment.setContent(model.getComment().getContent());
            comment.setUpt(new Timestamp(System.currentTimeMillis()));
            if (commentRepository.save(comment) != null) {
                if (model.getTopic()!= null) {
                    Topic topic = topicRepository.findById(model.getTopic().getId()).orElse(null);
                    sendMessage(user, pUser, AppConst.COMMENT_ADD_COMMENT, null, topic);
                }else {
                    Article article = articleRepository.findById(model.getArticle().getId()).orElse(null);
                    sendMessage(user, pUser, AppConst.COMMENT_ADD_COMMENT, article, null);
                }
                return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS,null));
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_SAVE);
    }

    // 发送消息
    private void sendMessage(User creator, User target, int type, Article article, Topic topic) {
        Message message = new Message();
        message.setCreator(creator);
        message.setTarget(target);
        message.setType(AppConst.MESSAGE_TYPE_SYSTEM);
        message.setUpt(new Timestamp(System.currentTimeMillis()));
        if (type == AppConst.COMMENT_ADD_ESSAY) {
            message.setContent(" 评论了你的文章:《<a style=\"color:rgb(64, 158, 255)\" href=\"/#/browser/essay/detail/" + article.getId() + "\">" + article.getTitle() + "</a>》");
        } else if (type == AppConst.COMMENT_ADD_TOPIC) {
            message.setContent(" 评论了你的话题:《<a style=\"color:rgb(64, 158, 255)\" href=\"/#/browser/topic/detail/" + topic.getId() + "\">" + topic.getName() + "</a>》");
        } else if (type == AppConst.COMMENT_ADD_COMMENT) {
            String link = "";
            if (article != null) {
                link = "<a style=\"color:rgb(64, 158, 255)\" href=\"/#/browser/topic/detail/" + article.getId() + "\">" + article.getTitle() + "</a>》";
            }else if (topic!= null) {
                link =  "<a style=\"color:rgb(64, 158, 255)\" href=\"/#/browser/topic/detail/" + topic.getId() + "\">" + topic.getName() + "</a>";
            }
            message.setContent("回复了你在" + link + "下的评论");
        }
        Message msg = messageRepository.save(message);
        if (msg!=null) {
            HashMap<String,Object> data = new HashMap<>();
            data.put("msg", msg);
            String res = ResponseUtil.revertMessageList(ResponseUtil.getResultMap(ResponseCode.T_MESSAGE_SUCCESS_SEND, data));
            try {
                this.pushService.sendMsg(res, target.getId());
            } catch (IOException e) {
                this.logger.error("发送socket消息失败");
                e.printStackTrace();
            }
        }
    }

    // 遍历树结构评论
    private void loopComment(List<CommentVo> res, List<Comment> list) {
        for(Comment c : list) {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(c, vo);
            vo.setTargetUser(c.getPComment().getCreator());
            res.add(vo);
            if (c.getChilds().size()>0){
                loopComment(res, c.getChilds());
            }
        }
    }

}
