package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.util.JsonUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class BaseController {

    HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Controller中只对参数完整性进行判定，默认Result为参数缺失错误
     * @return 返回转换后的结果
     */
    String defaultResult() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("state", ResponseCode.T_APP_NO_PARAMS);
        JsonUtil jsonUtil = new JsonUtil();
        return jsonUtil.toJson(result);
    }


}
