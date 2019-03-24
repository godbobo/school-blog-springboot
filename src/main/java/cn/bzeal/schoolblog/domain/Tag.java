package cn.bzeal.schoolblog.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonFilter("TagFilter")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color = "#669EFF"; // 标签文字颜色

    @Column(nullable = false)
    private String background ="#ECF5FF";

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
