package cn.bzeal.schoolblog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 查询指定id的列表
    List<Tag> findByIdIn(List<Long> ids);

}
