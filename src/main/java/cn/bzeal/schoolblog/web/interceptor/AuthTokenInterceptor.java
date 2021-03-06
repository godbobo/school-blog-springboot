package cn.bzeal.schoolblog.web.interceptor;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.util.JwtTokenUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AuthTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean handleResult = false;

        // 对非登录请求执行检查
        if(request.getServletPath().equals("/user/login")){
            handleResult = true;
        }else{
            // 获取 token 后判断是否存在
            String token = request.getHeader(AppConst.APP_TOKEN_HEADER);
            if(StringUtils.isNotBlank(token)){
                try {
                    Map<String, Claim> claimMap= JwtTokenUtil.verifyToken(token);
                    if(claimMap.get("id") != null){
                        handleResult = true;
                        request.setAttribute("uid", claimMap.get("id").asString());
                        request.setAttribute("name", claimMap.get("name").asString());
                        request.setAttribute("role", claimMap.get("role").asInt());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseUtil.response(response, ResponseCode.N_APP_EXPIRES_TOKEN);
                }
            } else {
                ResponseUtil.response(response, ResponseCode.N_APP_NO_TOKEN);
            }
        }
        return handleResult;
    }
}
