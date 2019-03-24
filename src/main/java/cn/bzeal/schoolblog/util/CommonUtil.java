package cn.bzeal.schoolblog.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    // 转换json数组为List<Long>类型
    public static List<Long> getLongListFromJsonList(String jsonRest) {
        JacksonUtil jacksonUtil = new JacksonUtil();
        List<Integer> res = JacksonUtil.readValue(jsonRest, new TypeReference<List<Integer>>() {});
        // 由 json 转换的list 中存的实际是Integer对象，且没有办法通过强制转换转为Long，因此使用该笨方法将其转换为Long之后继续操作
        // 否则会在sql查询时报类型不匹配异常
        List<Long> tags = new ArrayList<>();
        for (Integer i : res) {
            tags.add(new Long(i));
        }
        return tags;
    }

    // 判断数组中是否存在为空的元素
    public static boolean isAnyNull(List<?> list) {
        for (Object o : list) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }

}
