package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    // controller 负责解析 request、验证参数完整性
    // service 负责处理业务逻辑
    // dao 负责与数据库交互

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login(QueryModel model) {
        if (model.getUser() == null || StringUtils.isAnyBlank(model.getUser().getPassword(), model.getUser().getLoginname())) {
            return defaultResult();
        }
        return userService.login(model);
    }

    @RequestMapping("/add")
    public String add(QueryModel model) {
        if (model.getQueryList() == null || model.getQueryList().size() == 0) {
            return defaultResult();
        }
        return userService.insertUser(model);
    }

    @RequestMapping("/getInfo")
    public String getInfo() {
        String userid = getRequest().getAttribute("uid").toString();
        return userService.getInfo(Long.parseLong(userid));
    }

    @RequestMapping("/lst")
    public String lst(QueryModel model) {
        Integer role = (Integer) getRequest().getAttribute("role");
        if (role == null || role < AppConst.USER_ADMIN){
            return noPowerResult();
        }
        String userId = getRequest().getAttribute("uid").toString();
        return userService.lst(model, Long.parseLong(userId));
    }

    @RequestMapping("/delete")
    public String delete(QueryModel model) {
        // 验证登录用户身份
        String id = getRequest().getAttribute("uid").toString();
        Integer role = (Integer) getRequest().getAttribute("role");
        Long deleteid = null;
        if (model.getUser() != null) {
            deleteid = model.getUser().getId();
        }
        if (StringUtils.isBlank(id) || role == null || role != 2) {
            return defaultResult();
        }
        return userService.deleteUser(deleteid);
    }

    // 统计用户信息
    @RequestMapping("/countUser")
    public String countUser(QueryModel model) {
        String currentUser = getRequest().getAttribute("uid").toString();
        if (model.getUser() == null || model.getUser().getId() == null || StringUtils.isBlank(currentUser)) {
            return defaultResult();
        }
        return userService.countUser(model.getUser().getId(), Long.parseLong(currentUser));
    }

    // 关注或取消关注用户
    @RequestMapping("/follow")
    public String follow(QueryModel model) {
        String currentUserId = getRequest().getAttribute("uid").toString();
        if (StringUtils.isBlank(currentUserId) || model.getUser() == null || model.getUser().getId() == null) {
            return defaultResult();
        }
        return userService.followOrNot(model, Long.parseLong(currentUserId));
    }

    // 头像上传
    @PostMapping("/avatarUpload")
    public String avatarUpload(MultipartFile img, HttpServletRequest req) {
        String id = getRequest().getAttribute("uid").toString();
        if (img== null || img.isEmpty() || StringUtils.isBlank(id)){
            return defaultResult();
        }
        return userService.uploadAvatar(img, req, Long.parseLong(id));
    }

    // 用户excel文件上传
    @PostMapping("/excelUpload")
    public String excelUpload(MultipartFile excel, HttpServletRequest req) {
        if (excel == null|| excel.isEmpty()) {
            return  defaultResult();
        }
        return userService.uploadExcel(excel, req);
    }

}
