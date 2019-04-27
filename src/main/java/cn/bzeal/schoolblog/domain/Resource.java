package cn.bzeal.schoolblog.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 文件实体类，与文章、话题进行关联
 * Created by Godbobo on 2019/4/26.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@JsonFilter("ResourceFilter")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 文件名

    @Column(nullable = false)
    private String url; // 访问路径

    @Column(nullable = false)
    private Long size; // 文件大小

    @Column(nullable = false)
    private String path; // 实际路径

    @Column(nullable = false)
    private Timestamp upt; // 上传时间

    @OneToOne
    @JoinColumn(name = "uploader")
    private User uploader; // 上传者

}
