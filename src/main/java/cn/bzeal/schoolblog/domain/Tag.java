package cn.bzeal.schoolblog.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color = "#6CF8C9"; // 标签颜色

    // 多对一配置
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator")
    private User creator; // 创建者

    // 多对多配置
    @ManyToMany(mappedBy = "tags")
    private List<Article> articles;

    @ManyToMany(mappedBy = "tags")
    private List<Topic> topics;
}
