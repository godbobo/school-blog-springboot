package cn.bzeal.schoolblog.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonFilter("CommentFilter")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Timestamp upt;

    @Column(nullable = false)
    private Integer type = 0; // 0为文章，1为话题, 2为评论

    @ManyToOne
    @JoinColumn(name = "p_comment")
    private Comment pComment; // 父级评论

    @OneToMany(mappedBy = "pComment", cascade = CascadeType.ALL)
    private List<Comment> childs; // 子评论列表

    // 多对一配置
    @ManyToOne(optional = false)
    @JoinColumn(name="creator")
    private User creator; // 评论创建者

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

}
