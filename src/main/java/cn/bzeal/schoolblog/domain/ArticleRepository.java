package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 获取置顶文章
    List<Article> findByTop(Integer top);

    // 获取非置顶文章
    Page<Article> findByTopNot(int top, Pageable pageable);

    // 根据用户id查找文章列表
    Page<Article> findByAuthor(User user, Pageable pageable);

    // 根据用户id查找收藏列表
    Page<Article> findByLovers(User user, Pageable pageable);

    // 根据话题查找文章列表
    Page<Article> findByTopic(Topic topic, Pageable pageable);

}
