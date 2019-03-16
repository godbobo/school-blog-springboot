package cn.bzeal.schoolblog.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer type; // 0为系统通知，1为用户消息，2为申请

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Timestamp upt; // 创建时间

    // 多个消息发送给一个用户
    @ManyToOne
    @JoinColumn(name = "target", nullable = false)
    private User target; // 接收消息的人

    // 一个用户发送多条消息
    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator; // 创建者

}
