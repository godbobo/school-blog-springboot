package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.UserService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
        // 验证登录用户是否为管理员
        Integer role = (Integer) getRequest().getAttribute("role");
        User user = model.getUser();
        if (role != null && role >= AppConst.USER_TEACHER) { // 只有教师和管理员有权增加用户
            if (user != null && !StringUtils.isAnyBlank(user.getRealName(), user.getCollege(), user.getLoginname()) && user.getRole() != null) {
                // 教师只能增加学生用户
                if (role == AppConst.USER_TEACHER && user.getRole() >= role) {
                    return ResponseUtil.getResult(ResponseCode.T_APP_NO_POWER);
                }
                return userService.insertUser(model);
            } else {
                return defaultResult();
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_NO_POWER);

    }

    @RequestMapping("/getInfo")
    public String getInfo() {
        String userid = getRequest().getAttribute("uid").toString();
        return userService.getInfo(Long.parseLong(userid));
    }

    @RequestMapping("/lst")
    public String lst(QueryModel model) {
        Integer role = (Integer) getRequest().getAttribute("role");
        if (role == null || role == AppConst.USER_STU) { // 不允许学生访问
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
        if (StringUtils.isBlank(id)) {
            return defaultResult();
        } else if (role == null || role == AppConst.USER_STU) {
            return noPowerResult();
        }
        return userService.deleteUser(deleteid, role);
    }

    // 批量删除用户
    @RequestMapping("/batchDelete")
    public String batchDelete(QueryModel model) {
        // 验证登录用户身份
        String id = getRequest().getAttribute("uid").toString();
        Integer role = (Integer) getRequest().getAttribute("role");
        if (StringUtils.isBlank(id) || role == null || role != 2) {
            return defaultResult();
        }
        return userService.batchDelete(model.getQueryList(), role);
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
        if (img == null || img.isEmpty() || StringUtils.isBlank(id)) {
            return defaultResult();
        }
        return userService.uploadAvatar(img, req, Long.parseLong(id));
    }

    // 用户excel文件上传
    @PostMapping("/excelUpload")
    public String excelUpload(MultipartFile excel, HttpServletRequest req) {
        if (excel == null || excel.isEmpty()) {
            return defaultResult();
        }
        Integer role = (Integer) getRequest().getAttribute("role");
        if (role == null || role < AppConst.USER_TEACHER) { // 只有教师和管理员有权增加用户
            return ResponseUtil.getResult(ResponseCode.T_APP_NO_POWER);
        }
        return userService.uploadExcel(excel, req, role);
    }

    // 修改用户密码
    @RequestMapping("/changePwd")
    public String changePwd(@Param("oldPwd") String oldPwd, @Param("newPwd") String newPwd) {
        String currentUserId = getRequest().getAttribute("uid").toString();
        if (StringUtils.isAnyBlank(oldPwd, newPwd)) {
            return defaultResult();
        }
        return userService.changePwd(oldPwd, newPwd, Long.parseLong(currentUserId));
    }

    // 重置用户密码
    @RequestMapping("/resetPwd")
    public String resetPwd(QueryModel model) {
        Integer role = (Integer) getRequest().getAttribute("role");
        if (role == null || role == 0 || model.getUser() == null || model.getUser().getId() == null) {
            return defaultResult();
        }
        return userService.resetPwd(model.getUser().getId(), role);
    }

}
