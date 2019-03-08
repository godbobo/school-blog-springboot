package cn.bzeal.schoolblog.model;

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
    private List<String> queryList; // 请求参数
    private int queryType; // 请求类型

}
