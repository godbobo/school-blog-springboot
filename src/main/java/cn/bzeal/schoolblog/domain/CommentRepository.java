package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 查询指定id的评论列表
    List<Comment> findByIdIn(List<Long> ids);

    // 查询指定文章下的评论列表
    Page<Comment> findByArticle(Article article, Pageable pageable);

    // 查询指定话题下的评论列表
    Page<Comment> findByTopic(Topic topic, Pageable pageable);

}
