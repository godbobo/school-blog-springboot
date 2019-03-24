package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.model.Qtopic;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.TopicService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public String add(QueryModel model, Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (user != null) {
            Topic topic = model.getTopic();
            topic.setCreator(user);
            List<Tag> tagList;
            // 尝试获取标签列表
            if (StringUtils.isNotBlank(model.getJsonRest())) {
                // 从数据库查询相关Tags
                tagList = tagRepository.findByIdIn(CommonUtil.getLongListFromJsonList(model.getJsonRest()));
                topic.setTags(tagList);
                // 因为是多对多关系，因此在这里还需要对tag绑定topic
                for (Tag t : tagList) {
                    t.getTopics().add(topic);
                }
            }
            topic.setUpt(new Timestamp(System.currentTimeMillis()));
            if (topicRepository.save(topic) != null) {
                return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_ADD);
            } else {
                return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_SAVE);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);

    }

    @Override
    public String lstById(Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (user != null) {
            List<Topic> topics = user.getTopics();
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", topics);
            return ResponseUtil.revertTopic(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lstByCreator(QueryModel model) {
        User user = userRepository.findById(model.getUser().getId()).orElse(null);
        if (user != null) {
            Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
            Page<Topic> page = topicRepository.findByCreator(user, pageable);
            // 此时得到的对象并不是我们想要的结果，还需要对文章数量和用户数量进行计数
            List<Qtopic> list = new ArrayList<>();
            for (Topic t : page.getContent()) {
                Qtopic tp = new Qtopic();
                BeanUtils.copyProperties(t, tp); // 复制目标属性
                tp.setEssaycount(t.getArticles().size());
                tp.setUsercount(t.getFollowers().size() + 1);
                list.add(tp);
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("topiclst", list);
            data.put("total", page.getTotalElements());
            return ResponseUtil.revertTopicList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lstByFollower(QueryModel model) {
        User user = userRepository.findById(model.getUser().getId()).orElse(null);
        if (user != null) {
            Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
            Page<Topic> page = topicRepository.findByFollowers(user, pageable);
            // 此时得到的对象并不是我们想要的结果，还需要对文章数量和用户数量进行计数
            List<Qtopic> list = new ArrayList<>();
            for (Topic t : page.getContent()) {
                Qtopic tp = new Qtopic();
                BeanUtils.copyProperties(t, tp); // 复制目标属性
                tp.setEssaycount(t.getArticles().size());
                tp.setUsercount(t.getFollowers().size() + 1);
                list.add(tp);
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("topiclst", list);
            data.put("total", page.getTotalElements());
            return ResponseUtil.revertTopicList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lst(QueryModel model) {
        PageRequest pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "id"));
        Page<Topic> page = topicRepository.findAll(pageable);
        // 此时得到的对象并不是我们想要的结果，还需要对文章数量和用户数量进行计数
        List<Qtopic> list = new ArrayList<>();
        for (Topic t : page.getContent()) {
            Qtopic tp = new Qtopic();
            BeanUtils.copyProperties(t, tp); // 复制目标属性
            tp.setEssaycount(t.getArticles().size());
            tp.setUsercount(t.getFollowers().size() + 1);
            list.add(tp);
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("lst", list);
        data.put("total", page.getTotalElements());
        return ResponseUtil.revertTopicList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
    }

    @Override
    public String lstAboutId(Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (user != null) {
            List<Topic> list = user.getTopics();
            list.addAll(user.getFollows());
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", list);
            return ResponseUtil.revertTopic(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String find(Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("topic", topic);
            return ResponseUtil.revertTopic(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_TOPIC_EMPTY_FIND);
    }

}
