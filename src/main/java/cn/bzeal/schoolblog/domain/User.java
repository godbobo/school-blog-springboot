package cn.bzeal.schoolblog.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer role = 0; // 0为学生，1为教师，2为管理员

    @Column(nullable = false)
    private String name;

    @Column
    private String college;

    @Column(length = 11)
    private String tel;

    @Column
    private String headimg; // 头像

    @Column(nullable = false)
    private Timestamp reg; // 注册日期

    // 一对多关系配置
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Article> articles; // 文章列表

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Comment> comments; // 评论列表

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Topic> topics; // 话题列表

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Tag> tags; // 标签列表

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL)
    private List<Message> messages; // 消息列表

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Message> sendMessages; // 已发送消息列表

    // 多对多关系配置
    @ManyToMany(mappedBy = "lovers")
    private List<Article> favs; // 收藏文章列表

    @ManyToMany(mappedBy = "followers")
    private List<Topic> follows; // 加入话题列表

    // 用户关注列表
    @ManyToMany
    @JoinTable(name = "user_follow", joinColumns = @JoinColumn(name = "tofws_id"), inverseJoinColumns = @JoinColumn(name = "befws_id"))
    private List<User> tofws;
    // 关注者列表
    @ManyToMany(mappedBy = "tofws")
    private List<User> befws;
}
