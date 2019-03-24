package cn.bzeal.schoolblog.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonFilter("TopicFilter")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column
    private String summary; // 话题说明

    @Column(nullable = false)
    private Timestamp upt; // 创建时间

    // 一对多配置
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Article> articles; // 话题列表

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Comment> comments; // 评论列表

    // 多对一配置
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator")
    private User creator; // 创建者

    // 多对多配置
    @ManyToMany
    @JoinTable(name = "topic_tag", joinColumns = @JoinColumn(name = "topic_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags; // 标签列表

    @ManyToMany
    @JoinTable(name = "topic_user", joinColumns = @JoinColumn(name = "topic_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> followers; // 关注者列表
}
