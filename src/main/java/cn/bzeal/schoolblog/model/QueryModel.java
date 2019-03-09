package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.domain.User;
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
    private int queryType; // 请求类型

    private User user; // 用户相关信息

}
