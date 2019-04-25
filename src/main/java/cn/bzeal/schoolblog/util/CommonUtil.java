package cn.bzeal.schoolblog.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    /**@param source 需要加密的字符串
     * @param hashType  加密类型 （MD5 和 SHA）
     * @return
     */
    public static String getHash(String source, String hashType) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(hashType);
            md5.update(source.getBytes());
            for (byte b : md5.digest()) {
                sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
