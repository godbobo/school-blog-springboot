package cn.bzeal.schoolblog.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 查询指定id的评论列表
    List<Comment> findByIdIn(List<Long> ids);

}
