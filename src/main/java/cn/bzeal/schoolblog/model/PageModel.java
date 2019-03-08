package cn.bzeal.schoolblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 分页参数
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageModel {

    private int row = 10; // 每页显示条目数量

    private int page = 0; // 当前显示页数

}
