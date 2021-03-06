package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.UserService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.ExcelUtil;
import cn.bzeal.schoolblog.util.JwtTokenUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ExcelUtil excelUtil;

    @Value("${upload.root}")
    private String uploadRoot; // 上传文件根目录路径

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ExcelUtil excelUtil) {
        this.userRepository = userRepository;
        this.excelUtil = excelUtil;
    }

    @Override
    public String login(QueryModel model) {
        User user = userRepository.findByLoginnameAndPassword(model.getUser().getLoginname(), CommonUtil.getHash(model.getUser().getPassword(), "MD5"));
        // 判断用户信息 有则生成 token 返回
        if (user != null) {
            try {
                // 仅在登录时使用登录名，其他情况下依旧使用id来确定用户身份
                String token = JwtTokenUtil.createToken(user.getId().toString(), user.getRole(), user.getRealName());
                HashMap<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user);
                data.put("expires", 60 * 60 * 24 * 14); // 超时时间为两周
                return ResponseUtil.revertUser(ResponseUtil.getResultMap(ResponseCode.T_USER_SUCCESS_LOGIN, data));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.getResult(ResponseCode.T_USER_FAIL_LOGIN);
            }
        } else {
            return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
        }
    }

    // 查询用户信息
    @Override
    public String getInfo(Long username) {
        User user = userRepository.findById(username).orElse(null);
        // 判断用户信息 有则生成 token 返回
        if (user != null) {
            try {
                HashMap<String, Object> data = new HashMap<>();
                data.put("user", user);
                return ResponseUtil.revertUser(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String countUser(Long userid, Long currentUserId) {
        User user = userRepository.findById(userid).orElse(null);
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (user != null && currentUser != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("wzcount", user.getArticles().size());
            data.put("sccount", user.getFavs().size());
            data.put("htcount", user.getTopics().size() + user.getFollows().size());
            data.put("fscount", user.getBefws().size());
            data.put("isfollow", user.getBefws().contains(currentUser));
            return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String insertUser(QueryModel model) {
        User user = model.getUser();
        user.setReg(new Timestamp(System.currentTimeMillis()));
        user.setPassword(CommonUtil.getHash("111111", "MD5"));
        if (userRepository.save(user) != null) {
            return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_ADD);
        } else {
            return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_SAVE);
        }
    }

    @Override
    public String followOrNot(QueryModel model, Long currentUserId) {
        // 不允许自己关注自己
        if (model.getUser().getId().equals(currentUserId)) {
            return ResponseUtil.getResult(ResponseCode.T_USER_CONFLICT_FOLLOW);
        }
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        User targetUser = userRepository.findById(model.getUser().getId()).orElse(null);
        if (currentUser != null && targetUser != null) {
            if (currentUser.getTofws().contains(targetUser)) {
                if (model.getQueryType() == AppConst.USER_FOLLOW_CANCEL) {
                    currentUser.getTofws().remove(targetUser);
                    targetUser.getBefws().remove(currentUser);
                }
            } else if (model.getQueryType() == AppConst.USER_FOLLOW) {
                currentUser.getTofws().add(targetUser);
                targetUser.getBefws().add(currentUser);
            }
            userRepository.save(currentUser);
            userRepository.save(targetUser);
            return ResponseUtil.getResult(ResponseCode.N_SUCCESS);
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lst(QueryModel model, Long userId) {
        // 定义分页，获取全部用户
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow());
        List<User> list = new ArrayList<>();
        long totalPage = 0L;
        if (model.getQueryType() == AppConst.QUERY_USERLIST_NORMAL) {
            Page<User> page = userRepository.findAllByIdNot(userId, pageable);
            totalPage = page.getTotalElements();
            list = page.getContent();
        } else if (model.getQueryType() == AppConst.QUERY_USERLIST_USERNAME) {
            // 此处一定只有一个数据或者没有数据
            String id = model.getUser().getLoginname();
            User user = userRepository.findByLoginname(id);
            if (user != null) {
                list.add(user);
            }
            totalPage = (long) list.size();
        } else if (model.getQueryType() >= AppConst.QUERY_USERLIST_Q) {
            User user = model.getUser();
            if (user.getRealName() != null) {
                user.setRealName("%" + user.getRealName() + "%");
                Page<User> page = userRepository.findByRealNameLike(user.getRealName(), pageable);
                totalPage = page.getTotalElements();
                list = page.getContent();
            } else if (user.getCollege() != null) {
                user.setCollege("%" + user.getCollege() + "%");
                Page<User> page = userRepository.findByCollegeLike(user.getCollege(), pageable);
                totalPage = page.getTotalElements();
                list = page.getContent();
            }
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("lst", list);
        data.put("total", totalPage);
        return ResponseUtil.revertUser(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
    }

    @Override
    public String changePwd(String oldPwd, String newPwd, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user!= null){
            // 验证旧密码是否正确
            if(user.getPassword().equals(CommonUtil.getHash(oldPwd, "MD5"))) {
                user.setPassword(CommonUtil.getHash(newPwd, "MD5"));
                if (userRepository.save(user)!= null){
                    return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_UPDATE);
                }
            }else{
                return ResponseUtil.getResult(ResponseCode.T_USER_FAIL_PWD_INVALID);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_UPDATE);
    }

    @Override
    public String resetPwd(Long userId, int power) {
        // 重置密码功能仅用于上级改变下级
        User user = userRepository.findById(userId).orElse(null);
        if (user!=null){
            if (user.getRole() >= power) {
                return ResponseUtil.getResult(ResponseCode.T_APP_NO_POWER);
            }else{
                user.setPassword(CommonUtil.getHash("111111", "MD5"));
                if (userRepository.save(user)!=null) {
                    return ResponseUtil.getResult(ResponseCode.N_SUCCESS);
                }
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_UPLOAD);
    }

    @Override
    public String deleteUser(Long userid, int role) {
        User user = userRepository.findById(userid).orElse(null);
        if (user!= null) {
            if (role == AppConst.USER_TEACHER && user.getRole() >AppConst.USER_STU) { // 教师只能操作学生信息
                return ResponseUtil.getResult(ResponseCode.T_APP_NO_POWER);
            }
            userRepository.delete(user);
            return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_DELETE);
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String batchDelete(List<String> Ids, int role) {
        List<Long> rs = new ArrayList<>();
        for(String s : Ids){
            rs.add(Long.parseLong(s));
        }
        List<User> users = userRepository.findByIdIn(rs);
        for(User user : users) { // 移除不允许操作的用户信息
            if (role == AppConst.USER_TEACHER && user.getRole() >AppConst.USER_STU) { // 教师只能操作学生信息
                users.remove(user);
            }
        }
        userRepository.deleteInBatch(users);
        return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_DELETE);
    }

    @Override
    public String uploadAvatar(MultipartFile file, HttpServletRequest req, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.T_USER_EMPTY_FIND, null));
        }
        String realPath = uploadRoot + "avatar/";
        String format = simpleDateFormat.format(new Date());
        File folder = new File(realPath + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        String oldName = file.getOriginalFilename();
        String newName = "/" + UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));
        try {
            file.transferTo(new File(folder, newName));

            String filePath = req.getScheme() + "://" + req.getServerName() + "/upload/avatar/" + format + newName;
            user.setHeadimg(filePath);
            if (userRepository.save(user) != null) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("url", filePath);
                return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.T_APP_SUCCESS_UPLOAD, data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.T_APP_FAIL_UPLOAD, null));
    }

    @Override
    public String uploadExcel(MultipartFile excel, HttpServletRequest req, int role) {
        try {
            Map<Integer, Map<String, Object>> map = excelUtil.getExcelContent(excel);
            // 构建User对象
            List<User> users = new ArrayList<>();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String defaultPwd = CommonUtil.getHash("111111", "MD5"); // 默认密码，采用md5加密
            for (Map<String, Object> u : map.values()) {
                User user = new User();
                // 判断是否有必填字段
                if (u.containsKey("loginname") && u.containsKey("role") && u.containsKey("name")) {
                    user.setLoginname(String.valueOf(u.get("loginname")));
                    Double d = Double.parseDouble(String.valueOf(u.get("role")));
                    if (d >= role) { // 只能批量上传等级低于自己的用户
                        return ResponseUtil.getResult(ResponseCode.T_APP_NO_POWER);
                    }
                    user.setRole(d.intValue());
                    user.setRealName(String.valueOf(u.get("name")));
                } else {
                    return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.N_APP_NO_PARAMS, null));
                }
                // 插入默认值
                user.setPassword(defaultPwd);
                user.setReg(timestamp);
                // 插入可选值
                if (u.containsKey("headimg")) {
                    user.setHeadimg(String.valueOf(u.get("headimg")));
                }
                if (u.containsKey("college")) {
                    user.setCollege(String.valueOf(u.get("college")));
                }
                if (u.containsKey("pro")) {
                    user.setPro(String.valueOf(u.get("pro")));
                }
                if (u.containsKey("email")) {
                    user.setMail(String.valueOf(u.get("email")));
                }
                if (u.containsKey("tel")) {
                    user.setTel(String.valueOf(u.get("tel")));
                }
                users.add(user);
            }
            if (userRepository.saveAll(users) != null) {
                return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.T_SUCCESS, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.revert(ResponseUtil.getResultMap(ResponseCode.T_APP_FAIL_SAVE, null));
    }

}
