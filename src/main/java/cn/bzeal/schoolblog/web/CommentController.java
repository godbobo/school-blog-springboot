package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.domain.Comment;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 新增评论
    @RequestMapping("/add")
    public String add(QueryModel model) {
        String currentUserId = getRequest().getAttribute("uid").toString(); // 当前用户
        Comment comment = model.getComment();
        if (comment == null || StringUtils.isBlank(comment.getContent()) || comment.getType() == null || ((comment.getArticle() == null || comment.getArticle().getId() == null) && (comment.getTopic() == null || comment.getTopic().getId() == null))) {
            return defaultResult();
        }
        return commentService.add(model, Long.parseLong(currentUserId));
    }

    // 查看评论列表
    @RequestMapping("/lst")
    public String lst(QueryModel model) {
        if ((model.getArticle() == null || model.getArticle().getId() == null) && (model.getTopic() == null || model.getTopic().getId() == null)){
            return defaultResult();
        }
        return commentService.lstById(model);
    }

}
