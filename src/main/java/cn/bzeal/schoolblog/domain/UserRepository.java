package cn.bzeal.schoolblog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名和密码查询用户
    User findByLoginnameAndPassword(String loginname, String password);

    // 根据id查询用户列表
    List<User> findByIdIn(List<Long> ids);

    // 查询用户列表
    Page<User> findAllByIdNot(Long id, Pageable pageable);

    // 按条件查询用户列表
    Page<User> findAllByNameLikeAndCollegeLike(String name, String college, Pageable pageable);

}
