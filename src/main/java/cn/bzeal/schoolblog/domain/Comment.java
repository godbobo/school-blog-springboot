package cn.bzeal.schoolblog.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    private Integer type = 0; // 0为文章，1为话题

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
