package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    // 根据创建者查找话题列表
    Page<Topic> findByCreator(User creator, Pageable pageable);

    // 根据用户id查找加入的话题列表
    Page<Topic> findByFollowers(User user, Pageable pageable);

    // 根据标题查找文章列表
    Page<Topic> findByNameLike(String name, Pageable pageable);
    // 根据内容查找文章列表
    Page<Topic> findBySummaryLike(String summary, Pageable pageable);

}
