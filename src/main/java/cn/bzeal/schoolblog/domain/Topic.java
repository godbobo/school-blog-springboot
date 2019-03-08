package cn.bzeal.schoolblog.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column
    private String summary; // 话题说明

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
    private List<Tag> tags;
}
