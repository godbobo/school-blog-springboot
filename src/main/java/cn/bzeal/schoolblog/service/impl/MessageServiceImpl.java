package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.Message;
import cn.bzeal.schoolblog.domain.MessageRepository;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.MessageVo;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.MessageService;
import cn.bzeal.schoolblog.util.MessagePushService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessagePushService pushService;
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, MessagePushService pushService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.pushService = pushService;
    }

    @Override
    public String send(QueryModel model, Long userId) {
        User target = userRepository.findById(model.getUser().getId()).orElse(null);
        User current = userRepository.findById(userId).orElse(null);
        if (target != null && current != null) {
            Message message = model.getMessage();
            message.setType(AppConst.MESSAGE_TYPE_USER);
            message.setUpt(new Timestamp(System.currentTimeMillis()));
            message.setTarget(target);
            message.setCreator(current);
            message = messageRepository.save(message);
            if (message != null) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("msg", message);
                // 将新加入的消息对象返回
                String res = ResponseUtil.revertMessageList(ResponseUtil.getResultMap(ResponseCode.T_MESSAGE_SUCCESS_SEND, data));
                try {
                    this.pushService.sendMsg(res, target.getId());
                } catch (IOException e) {
                    this.logger.error("发送socket消息失败");
                    e.printStackTrace();
                }
                return res;
            }else {
                return ResponseUtil.getResult(ResponseCode.T_MESSAGE_FAIL_SEND);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String delete(Long targetId, Long userId) {
        User current = userRepository.findById(userId).orElse(null);
        if (current != null) {
            for(Message m: current.getMessages()){
                if (m.getTarget().getId().equals(targetId)) {
                    current.getMessages().remove(m); // 移除目标为target的消息记录
                }
            }
            for(Message m: current.getSendMessages()){
                if (m.getTarget().getId().equals(targetId)) {
                    current.getMessages().remove(m); // 移除目标为target的消息记录
                }
            }
            userRepository.save(current);
            return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_DELETE);
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_DELETE);
    }

    @Override
    public String lst(QueryModel model, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Message> page = messageRepository.findByIsreadAndTarget(model.getQueryType(), user, new Sort(Sort.Direction.DESC, "upt"));
            List<MessageVo> messages = new ArrayList<>();
            Map<Long, Integer> exist = new HashMap<>();
            for (Message m : page){
                if (!exist.containsKey(m.getCreator().getId())){ // 不存在则添加新的
                    MessageVo vo = new MessageVo();
                    BeanUtils.copyProperties(m, vo);
                    vo.setRepeat(1);
                    messages.add(vo);
                    exist.put(vo.getCreator().getId(), messages.size()-1);
                } else { // 存在时更新数量
                    messages.get(exist.get(m.getCreator().getId())).addRepeat();
                }
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", messages);
            data.put("total", page.size());
            return ResponseUtil.revertMessageList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lstFromTarget(QueryModel model, Long userId) {
        User target = userRepository.findById(model.getUser().getId()).orElse(null);
        User current = userRepository.findById(userId).orElse(null);
        if (target != null && current != null) {
            List<Message> page = messageRepository.findRecord(current.getId(), target.getId(), new Sort(Sort.Direction.DESC, "upt"));
            HashMap<String, Object> data = new HashMap<>();
            for(Message message : page) {
                if (message.getIsread() == 0){
                    message.setIsread(1);
                    messageRepository.save(message);
                }
            }
            data.put("lst", page);
            data.put("total", page.size());
            return ResponseUtil.revertMessageList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }
}
