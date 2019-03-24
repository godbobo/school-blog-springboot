package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 根据已读状态获取消息列表
    Page<Message> findByIsreadAndTarget(Integer isRead, User target, Pageable pageable);

    // 根据创建者和接收者获取消息列表
    // JPQL 不支持 UNION 声明，因此使用子查询的方式
    @Query("from Message m where (m.target = creator.id and m.creator = target.id) or (m.target = target.id and m.creator = creator.id)")
    Page<Message> findByCreatorAndTarget(User creator, User target, Pageable pageable);

}
