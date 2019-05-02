package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.domain.Topic;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Qtopic extends Topic {

    private Integer usercount = 1; // 用户数量（包含创建者和加入者）
    private Integer essaycount = 0; // 文章数量
    private boolean isfollow = false; // 是否已加入话题

}
