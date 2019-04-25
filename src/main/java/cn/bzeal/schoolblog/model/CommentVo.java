package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.domain.Comment;
import cn.bzeal.schoolblog.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 返回结果附加其他属性
 * Created by Godbobo on 2019/4/25.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVo extends Comment {

    private User targetUser; // 评论目标

}
