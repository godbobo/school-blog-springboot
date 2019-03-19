package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.model.UserModel;
import cn.bzeal.schoolblog.service.UserService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.JsonUtil;
import cn.bzeal.schoolblog.util.JwtTokenUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public GlobalResult login(UserModel model) {
        GlobalResult result = new GlobalResult();
        // 查询用户信息
        if (model.getUsername() != null) {
            String username = model.getUsername();
            User user = userRepository.findByIdAndPassword(Long.parseLong(username), model.getPassword());

            // 判断用户信息 有则生成 token 返回
            if (user != null) {
                try {
                    String token = JwtTokenUtil.createToken(user.getId().toString(), user.getRole(), user.getName());
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("token", token);
                    data.put("user", user);
                    data.put("expires", 60 * 60 * 24 * 14); // 超时时间为两周
                    result = filterDataByUser(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                result.setCode(AppConst.RES_FAIL_USER_ERROR);
            }
        } else {
            result.setCode(AppConst.RES_FAIL_NO_PARAMS);
        }
        return result;
    }

    @Override
    public GlobalResult getInfo(Long username) {
        GlobalResult result = new GlobalResult();
        // 查询用户信息
        if (username != null) {
            User user = userRepository.findById(username).orElse(null);

            // 判断用户信息 有则生成 token 返回
            if (user != null) {
                try {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("user", user);
                    result = filterDataByUser(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                result.setCode(AppConst.RES_FAIL_USER_ERROR);
            }
        } else {
            result.setCode(AppConst.RES_FAIL_NO_PARAMS);
        }
        return result;
    }

    @Override
    public GlobalResult countUser(Long userid, Long currentUserId) {
        GlobalResult result = new GlobalResult();
        User user = userRepository.findById(userid).orElse(null);
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (user != null && currentUser != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("wzcount", user.getArticles().size());
            data.put("sccount", user.getFavs().size());
            data.put("htcount", user.getTopics().size() + user.getFollows().size());
            data.put("fscount", user.getBefws().size());
            data.put("isfollow", user.getBefws().contains(currentUser));
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revert(ResponseUtil.getSuccessResult(data)));
        }
        return result;
    }

    @Override
    public GlobalResult insertUser(QueryModel model) {
        GlobalResult result = new GlobalResult();
        List<String> querys = model.getQueryList();
        if (querys != null) {
            User user = new User();
            user.setName(querys.get(0));
            user.setCollege(querys.get(1));
            user.setTel(querys.get(2));
            user.setRole(Integer.parseInt(querys.get(3)));
            user.setReg(new Timestamp(System.currentTimeMillis()));
            user.setPassword("admin");
            // TODO 默认密码应该修改为其他密码
            if (userRepository.save(user) != null) {
                result = filterDataByUser(null);
            }
        }
        return result;
    }

    @Override
    public GlobalResult followOrNot(QueryModel model, Long currentUserId) {
        GlobalResult result = new GlobalResult();
        // 不允许自己关注自己
        if (model.getUser().getId().equals(currentUserId)){
            return result;
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
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revert(ResponseUtil.getSuccessResult(null)));
        }
        return result;
    }

    @Override
    public GlobalResult lst(QueryModel model, HttpServletRequest request) {
        GlobalResult result = new GlobalResult();
        // 定义分页，获取全部用户
        // TODO Page size must not be less than one!添加该异常验证
        Pageable pageable = PageRequest.of(model.getPage(), model.getRow());
        List<User> list = new ArrayList<>();
        long totalPage = 0L;
        if (model.getQueryType() == AppConst.QUERY_USERLIST_NORMAL) {
            Page<User> page = userRepository.findAllByIdNot(Long.parseLong((String) request.getAttribute("uid")), pageable);
            totalPage = page.getTotalElements();
            list = page.getContent();
        } else if (model.getQueryType() == AppConst.QUERY_USERLIST_USERNAME) {
            // 此处一定只有一个数据或者没有数据
            Long id = model.getUser().getId();
            userRepository.findById(id).ifPresent(list::add);
            totalPage = (long) list.size();
        } else if (model.getQueryType() >= AppConst.QUERY_USERLIST_Q) {
            User user = model.getUser();
            user.setName("%"+(user.getName() == null ? "" : user.getName())+"%");
            user.setCollege("%"+(user.getCollege() == null ? "" : user.getCollege())+"%");
            Page<User> page = userRepository.findAllByNameLikeAndCollegeLike(user.getName(), user.getCollege(), pageable);
            totalPage = page.getTotalElements();
            list = page.getContent();
        }
        if (list != null && list.size() > 0) {
            // 封装结果
            // 裁剪文章数据
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", list);
            data.put("total", totalPage);
            result = filterDataByUser(data);
        }
        return result;
    }

    @Override
    public GlobalResult lstTopic(UserModel model) {
        GlobalResult result = new GlobalResult();
        // 验证 model 中的数据
        if (model.getUsername() != null) {
            // 查询指定 id 的用户
            User user = userRepository.findById(Long.parseLong(model.getUsername())).orElse(null);
            if (user != null) {
                List<Topic> list = user.getTopics();
                // 封装结果
                HashMap<String, Object> data = new HashMap<>();
                data.put("lst", list);
                HashMap<String, Object> map = ResponseUtil.getSuccessResult(data);
                // 转为 json
                JsonUtil jsonUtil = new JsonUtil();
                jsonUtil.filter(Topic.class, "id,name,summary,articles", null);
                jsonUtil.filter(Article.class, "id,title", null);
                String res = jsonUtil.toJson(map);
                result.setCode(AppConst.RES_SUCCESS);
                result.setMap(res);
            }
        }
        return result;
    }

    @Override
    public GlobalResult lstEssay(UserModel model) {
        GlobalResult result = new GlobalResult();
        // 验证 model 中的数据
        if (model.getUsername() != null) {
            // 查询指定 id 的用户
            User user = userRepository.findById(Long.parseLong(model.getUsername())).orElse(null);
            if (user != null) {
                List<Article> list = user.getArticles();
                result = filterArticleByUser(list);
            }
        }
        return result;
    }

    @Override
    public GlobalResult lstFav(UserModel model) {
        GlobalResult result = new GlobalResult();
        // 验证 model 中的数据
        if (model.getUsername() != null) {
            // 查询指定 id 的用户
            User user = userRepository.findById(Long.parseLong(model.getUsername())).orElse(null);
            if (user != null) {
                List<Article> list = user.getFavs();
                // 封装结果
                result = filterArticleByUser(list);
            }
        }
        return result;
    }

    @Override
    public GlobalResult lstMessage(UserModel model) {
        GlobalResult result = new GlobalResult();
        // 验证 model 中的数据
        if (model.getUsername() != null) {
            // 查询指定 id 的用户
            User user = userRepository.findById(Long.parseLong(model.getUsername())).orElse(null);
            if (user != null) {
                List<Message> list = user.getMessages();
                // 封装结果
                HashMap<String, Object> data = new HashMap<>();
                data.put("lst", list);
                HashMap<String, Object> map = ResponseUtil.getSuccessResult(data);
                // 转为 json
                JsonUtil jsonUtil = new JsonUtil();
                jsonUtil.filter(Message.class, "id,type,content,creator", null);
                jsonUtil.filter(User.class, "id,role,name,headimg", null);
                String res = jsonUtil.toJson(map);
                result.setCode(AppConst.RES_SUCCESS);
                result.setMap(res);
            }
        }
        return result;
    }

    @Override
    public GlobalResult deleteUser(Long userid) {
        GlobalResult result = new GlobalResult();
        userRepository.deleteById(userid);
        result.setCode(AppConst.RES_SUCCESS);
        result.setMap(ResponseUtil.revert(ResponseUtil.getSuccessResult(null)));
        return null;
    }

    // 对代码中的重复部分进行简单的整合

    private GlobalResult filterArticleByUser(List<Article> list) {
        GlobalResult result = new GlobalResult();
        // 封装结果
        HashMap<String, Object> data = new HashMap<>();
        data.put("lst", list);
        HashMap<String, Object> map = ResponseUtil.getSuccessResult(data);
        // 转为 json
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(Article.class, "id,title,content,view,upt,top,hide", null);
        String res = jsonUtil.toJson(map);
        result.setCode(AppConst.RES_SUCCESS);
        result.setMap(res);
        return result;
    }

    private GlobalResult filterDataByUser(HashMap<String, Object> data) {
        GlobalResult result = new GlobalResult();
        HashMap<String, Object> map = ResponseUtil.getSuccessResult(data);
        // 添加过滤字段并生成返回结果
        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.filter(User.class, "id,role,name,college,tel,headimg,reg", null);
        String u = jsonUtil.toJson(map);
        // 若转换结果不为空返回结果
        if (StringUtils.isNotBlank(u)) {
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(u);
        }
        return result;
    }
}
