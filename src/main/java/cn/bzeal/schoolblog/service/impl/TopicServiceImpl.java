package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.domain.Topic;
import cn.bzeal.schoolblog.domain.TopicRepository;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.TopicService;
import cn.bzeal.schoolblog.util.CommonUtil;
import cn.bzeal.schoolblog.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GlobalResult add(QueryModel model, Long userid) {
        GlobalResult result = new GlobalResult();
        User user = userRepository.findById(userid).orElse(null);
        if(user!=null){
            Topic topic = model.getTopic();
            topic.setCreator(user);
            ArticleServiceImpl.setResponse(result, topicRepository.save(topic)!=null);
        }
        return result;
    }
}
