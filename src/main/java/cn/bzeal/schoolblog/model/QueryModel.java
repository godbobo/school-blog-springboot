package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.domain.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryModel {

    private int row = 10; // 每页显示数量
    private int page = 0; // 当前页
    // TODO 删掉queryList 使用实体类属性来代替此中请求方式
    private List<String> queryList; // 请求参数
    private int queryType = 0; // 请求类型

    private User user; // 用户相关信息
    private Tag tag; // 标签相关信息
    private Topic topic; // 话题相关信息
    private Article article; // 文章相关信息
    private Comment comment; // 评论相关信息

    private String jsonRest; // 复杂数据使用json串接收

}
