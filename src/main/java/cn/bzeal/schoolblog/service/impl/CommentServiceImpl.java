package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.CommentService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final ArticleRepository articleRepository;

    private final TopicRepository topicRepository;

    private final MessageRepository messageRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, ArticleRepository articleRepository, TopicRepository topicRepository, MessageRepository messageRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
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
                    sendMessage(currentUser, targetUser, cmt.getType(), article.getTitle());
                } else if (topic != null) {
                    targetUser = topic.getCreator();
                    sendMessage(currentUser, targetUser, cmt.getType(), topic.getName());
                }
                return ResponseUtil.getResult(ResponseCode.T_SUCCESS);
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
        if (model.getQueryType() == AppConst.COMMENT_LST_ESSAY) {
            Article article = articleRepository.findById(model.getArticle().getId()).orElse(null);
            if (article != null) {
                Page<Comment> page = commentRepository.findByArticle(article, pageable);
                data.put("commentlst", page.getContent());
                data.put("total", page.getTotalElements());
                return ResponseUtil.revertCommentList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
            }
        } else if (model.getQueryType() == AppConst.COMMENT_LST_TOPIC) {
            Topic topic = topicRepository.findById(model.getTopic().getId()).orElse(null);
            if (topic != null) {
                Page<Comment> page = commentRepository.findByTopic(topic, pageable);
                data.put("commentlst", page.getContent());
                data.put("total", page.getTotalElements());
                return ResponseUtil.revertCommentList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
            }
        }
        return ResponseUtil.getResult(ResponseCode.N_SUCCESS);
    }

    // 发送消息
    private void sendMessage(User creator, User target, int type, String title) {
        Message message = new Message();
        message.setCreator(creator);
        message.setTarget(target);
        message.setType(AppConst.MESSAGE_TYPE_SYSTEM);
        message.setUpt(new Timestamp(System.currentTimeMillis()));
        if (type == AppConst.COMMENT_ADD_ESSAY) {
            message.setContent(creator.getName() + " 评论了你的文章:《" + title + "》");
        } else if (type == AppConst.COMMENT_ADD_TOPIC) {
            message.setContent(creator.getName() + " 评论了你的话题:《" + title + "》");
        }
        messageRepository.save(message);
    }

}
