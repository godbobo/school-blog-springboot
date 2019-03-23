package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.Tag;
import cn.bzeal.schoolblog.domain.TagRepository;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.TagService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

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
    public String add(QueryModel model, Long id) {
        // 首先查找到该用户
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            // 新的标签名不能和自己已经创建的标签名重复
            Tag tag = model.getTag();
            for (Tag t : user.getTags()){
                if (t.getName().equals(tag.getName())){
                    return ResponseUtil.getResult(ResponseCode.T_TAG_DUPLICATE_NAME);
                }
            }
            // 设置创建者后提交请求
            tag.setCreator(user);
            if(tagRepository.save(tag) != null) {
                return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_ADD);
            }else {
                return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_SAVE);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lstByUser(Long id) {
        // 首先查找到该用户
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            List<Tag> list = user.getTags();
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", list);
            return ResponseUtil.revertTag(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String delete(Long tagid, Long userid) {
        // 首先查找指定的用户，查看他的标签列表中是否含有该 tagid
        User user = userRepository.findById(userid).orElse(null);
        Tag tag = null;
        if(user!=null){
            for(Tag t : user.getTags()){
                if(t.getId().equals(tagid)){
                    tag = t;
                    user.getTags().remove(t);
                    break;
                }
            }
            if(tag != null) {
                // 级联删除需要同时保存父元素的状态
                tagRepository.delete(tag);
                userRepository.save(user);
                return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_DELETE);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }
}
