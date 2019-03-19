package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class BaseController {

    public HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    // 直接设置响应请求时，应该是没有获取到 token
    public void response(HttpServletResponse response, int errortype) {
        response.setContentType("application/json;utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().print(getErrorResult(errortype));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String response(GlobalResult result) {
        if (result.getCode() != AppConst.RES_SUCCESS) {
            return getErrorResult(result.getCode());
        }
        return result.getMap();
    }

    /**
     * 获取错误信息
     *
     * @param code 错误代码
     * @return json 格式响应体
     */
    private String getErrorResult(int code) {
        // 生成错误信息并返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        switch (code) {
            case AppConst.RES_FAIL_NO_TOKEN:
                map.put("msg", AppConst.RES_FAIL_NO_TOKEN_MSG);
                break;
            case AppConst.RES_EXPIRES_TOKEN:
                map.put("msg", AppConst.RES_EXPIRES_TOKEN_MSG);
                break;
            case AppConst.RES_FAIL_UNKNOWN:
                map.put("msg", AppConst.RES_FAIL_UNKNOWN_MSG);
                break;
            case AppConst.RES_FAIL_NO_PARAMS:
                map.put("msg", AppConst.RES_FAIL_NO_PARAMS_MSG);
                break;
            case AppConst.RES_FAIL_USER_ERROR:
                map.put("msg", AppConst.RES_FAIL_USER_ERROR_MSG);
                break;
            default:
                map.put("msg", AppConst.RES_FAIL_UNKNOWN_MSG);
        }
        map.put("data", null);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "json 转换失败";
        }
    }

}
