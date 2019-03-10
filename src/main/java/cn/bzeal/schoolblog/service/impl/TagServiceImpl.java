package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.Tag;
import cn.bzeal.schoolblog.domain.TagRepository;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.TagService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GlobalResult add(QueryModel model, Long id) {
        GlobalResult result = new GlobalResult();
        // 首先查找到该用户
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            // 新的标签名不能和自己已经创建的标签名重复
            Tag tag = model.getTag();
            for (Tag t : user.getTags()){
                if (t.getName().equals(tag.getName())){
                    return result;
                }
            }
            // 设置创建者后提交请求
            tag.setCreator(user);
            if(tagRepository.save(tag) != null) {
                result.setCode(AppConst.RES_SUCCESS);
                // 将 map 转换为 json
                HashMap<String, Object> res = CommonUtil.getSuccessResult(null);
                JsonUtil util = new JsonUtil();
                result.setMap(util.toJson(res));
            }
        }
        return result;
    }
}
