package cn.bzeal.schoolblog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局结果处理
 * service 返回该信息
 * controller 判断 rescode 是否正常，如果正常则返回 map 数据，否则包装错误信息返回
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalResult {

    private int code = AppConst.RES_FAIL_UNKNOWN;

    private String map;

}
