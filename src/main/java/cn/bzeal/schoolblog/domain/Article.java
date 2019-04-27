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
@JsonFilter("ArticleFilter")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String summary; // 文章简介，客户端负责生成

    @Column(nullable = false)
    private Long view = 0L;

    @Column(nullable = false)
    private Timestamp upt;

    @Column(nullable = false)
    private Integer top = 0; // 1为置顶

    @Column(nullable = false)
    private Integer hide = 0; // 1为隐藏

    // 多对一配置
    @ManyToOne(optional = false)
    @JoinColumn(name="author")
    private User author;

    @ManyToOne
    @JoinColumn(name="topic_id")
    private Topic topic;

    // 一对多配置
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "article")
    private List<Resource> files; // 文件列表

    // 多对多配置
    @ManyToMany
    @JoinTable(name = "article_tag", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @ManyToMany
    @JoinTable(name = "article_user", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> lovers;

}
