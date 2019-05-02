package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 根据已读状态获取消息列表
    List<Message> findByIsreadAndTarget(Integer isRead, User target, Sort sort);

    // 根据创建者和接收者获取消息列表
    List<Message> findByCreatorAndTarget(User creator, User target);

    // 获取聊天记录
    // JPQL 不支持 UNION 声明，因此使用子查询的方式
    @Query("from Message m where (m.target.id = :creator and m.creator.id = :target) or (m.creator.id = :creator and m.target = :target)")
    List<Message> findRecord(Long creator, Long target, Sort sort);

}
