package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.TopicService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.JsonUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public GlobalResult add(QueryModel model, Long userid) {
        GlobalResult result = new GlobalResult();
        User user = userRepository.findById(userid).orElse(null);
        if(user!=null){
            Topic topic = model.getTopic();
            topic.setCreator(user);
            List<Tag> tagList = new ArrayList<>();
            // 尝试获取标签列表
            if(StringUtils.isNotBlank(model.getJsonRest())){
                // 从数据库查询相关Tags
                tagList = tagRepository.findByIdIn(getLongListFromJsonList(model.getJsonRest()));
                topic.setTags(tagList);
                // 因为是多对多关系，因此在这里还需要对tag绑定topic
                for (Tag t : tagList) {
                    t.getTopics().add(topic);
                }
            }
            topic.setUpt(new Timestamp(System.currentTimeMillis()));
            // TODO 后期整合所有的重复方法
            ArticleServiceImpl.setResponse(result, topicRepository.save(topic)!=null && !isAnyNull(tagRepository.saveAll(tagList)));
        }
        return result;
    }

    @Override
    public GlobalResult lstById(Long userid) {
        GlobalResult result = new GlobalResult();
        User user = userRepository.findById(userid).orElse(null);
        if(user!=null){
            List<Topic> topics = user.getTopics();
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", topics);
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revertTopic(data));
        }
        return result;
    }

    @Override
    public GlobalResult lst(QueryModel model) {
        GlobalResult result = new GlobalResult();
        PageRequest pageable = PageRequest.of(model.getPage(), model.getRow());
        Page<Topic> page = topicRepository.findAll(pageable);
        if (page.getTotalElements() > 0) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", page.getContent());
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revertTopicTagAuthorArticle(ResponseUtil.getSuccessResult(data)));
        }
        return result;
    }

    @Override
    public GlobalResult lstAboutId(Long userid) {
        GlobalResult result = new GlobalResult();
        User user = userRepository.findById(userid).orElse(null);
        if(user!=null){
            List<Topic> list = user.getTopics();
            list.addAll(user.getFollows());
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", list);
            result.setCode(AppConst.RES_SUCCESS);
            result.setMap(ResponseUtil.revertTopic(ResponseUtil.getSuccessResult(data)));
        }
        return result;
    }

    // 判断数组中是否存在为空的元素
    public static boolean isAnyNull(List<Tag> list){
        for(Object o : list){
            if(o == null){
                return true;
            }
        }
        return false;
    }


    // TODO 后面将该方法移到一个专门的地方
    // 转换json数组为List<Long>类型
    public static List<Long> getLongListFromJsonList(String jsonRest) {
        JsonUtil jsonUtil = new JsonUtil();
        List<Integer> res = (List<Integer>) jsonUtil.toObject(jsonRest, ArrayList.class);
        // 由 json 转换的list 中存的实际是Integer对象，且没有办法通过强制转换转为Long，因此使用该笨方法将其转换为Long之后继续操作
        // 否则会在sql查询时报类型不匹配异常
        List<Long> tags = new ArrayList<>();
        for(Integer i : res){
            tags.add(new Long(i));
        }
        return tags;
    }
}
