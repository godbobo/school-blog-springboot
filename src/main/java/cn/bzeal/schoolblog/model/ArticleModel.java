package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.common.AppConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class ArticleModel extends PageModel {

    private int type = AppConst.ESSAY_Q_NORMAL; // 指定查询文章方式

    private Long id; // 根据文章 id 获取具体文章
    private String q; // 根据关键字 获取文章

    // 外键属性
    private Long username; // 根据用户 id 查询收藏文章

}
