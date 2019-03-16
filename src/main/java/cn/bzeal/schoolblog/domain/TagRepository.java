package cn.bzeal.schoolblog.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 查询指定id的列表
    List<Tag> findByIdIn(List<Long> ids);

}
