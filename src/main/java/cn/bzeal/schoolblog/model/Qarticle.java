package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.domain.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Qarticle extends Article {

    private Integer lovercount; // 收藏者数量
    private int type = AppConst.QUERY_ESSAY_ALL; // 过滤条件

}
