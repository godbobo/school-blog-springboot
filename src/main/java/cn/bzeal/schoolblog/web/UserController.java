package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.model.UserModel;
import cn.bzeal.schoolblog.service.UserService;
import cn.bzeal.schoolblog.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    // controller 负责解析 request 及封装 response，
    // service 负责处理业务逻辑
    // dao 负责与数据库交互

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login(UserModel model){
        return CommonUtil.response(userService.login(model));
    }

    @RequestMapping("/add")
    public String add(QueryModel model){
        return CommonUtil.response(userService.insertUser(model));
    }

    @RequestMapping("/getInfo")
    public String getInfo(){
        return CommonUtil.response(userService.getInfo((Long)getRequest().getAttribute("uid")));
    }

    @RequestMapping("/lst")
    public String lst(QueryModel model){
        return CommonUtil.response(userService.lst(model, getRequest()));
    }

    @RequestMapping("/lstTopic")
    public String lstTopic(UserModel model){
        return CommonUtil.response(userService.lstTopic(model));
    }

    @RequestMapping("/lstEssay")
    public String lstEssay(UserModel model){
        return CommonUtil.response(userService.lstEssay(model));
    }

    @RequestMapping("/lstFav")
    public String lstFav(UserModel model){
        return CommonUtil.response(userService.lstFav(model));
    }

    @RequestMapping("/lstMessage")
    public String lstMessage(UserModel model){
        return CommonUtil.response(userService.lstMessage(model));
    }

    @RequestMapping("/delete")
    public String delete(QueryModel model) {
        // 验证登录用户身份
        String id = getRequest().getAttribute("uid").toString();
        Integer role = (Integer) getRequest().getAttribute("role");
        Long deleteid = null;
        if (model.getUser()!=null){
            deleteid = model.getUser().getId();
        }
        if(StringUtils.isBlank(id) || role == null || role != 2) {
            return CommonUtil.response(new GlobalResult());
        }
        return CommonUtil.response(userService.deleteUser(deleteid));
    }
}
