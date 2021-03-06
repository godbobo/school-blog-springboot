package cn.bzeal.schoolblog.service;

import cn.bzeal.schoolblog.model.QueryModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {

    // 登录请求，查询数据库后生成 token 和 refresh_token
    String login(QueryModel model);

    // 获取用户信息
    String getInfo(Long username);

    // 统计用户信息
    String countUser(Long userid, Long currentUserId);

    /**
     * 传入新增用户的基本信息
     * Query List 顺序：name, college, tel, role
     */
    String insertUser(QueryModel model);

    // 关注或取消关注用户
    String followOrNot(QueryModel model, Long currentUserId);

    // 获取用户列表
    String lst(QueryModel model, Long userId);

    // 修改用户密码
    String changePwd(String oldPwd, String newPwd, Long userId);

    // 重置密码
    String resetPwd(Long userId, int power);

    // 删除用户
    String deleteUser(Long userid, int role);

    // 批量删除用户
    String batchDelete(List<String> Ids, int role);

    // 上传头像
    String uploadAvatar(MultipartFile file, HttpServletRequest req, Long userId);

    // 上传表格
    String uploadExcel(MultipartFile excel, HttpServletRequest req, int role);

}
