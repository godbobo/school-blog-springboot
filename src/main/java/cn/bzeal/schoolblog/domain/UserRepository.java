package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名和密码查询用户
    User findByIdAndPassword(Long id, String password);


    // 查询用户列表
    Page<User> findAllByIdNot(Long id, Pageable pageable);

    // 按条件查询用户列表
    Page<User> findAllByNameLikeAndCollegeLike(String name, String college, Pageable pageable);

}
