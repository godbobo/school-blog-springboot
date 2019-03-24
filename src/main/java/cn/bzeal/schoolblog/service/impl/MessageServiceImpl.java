package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.Message;
import cn.bzeal.schoolblog.domain.MessageRepository;
import cn.bzeal.schoolblog.domain.User;
import cn.bzeal.schoolblog.domain.UserRepository;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.MessageService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String send(QueryModel model, Long userId) {
        User target = userRepository.findById(model.getUser().getId()).orElse(null);
        User current = userRepository.findById(userId).orElse(null);
        if (target == null || current == null) {
            Message message = model.getMessage();
            message.setType(AppConst.MESSAGE_TYPE_USER);
            message.setUpt(new Timestamp(System.currentTimeMillis()));
            message.setTarget(target);
            message.setCreator(current);
            if (messageRepository.save(message) != null) {
                return ResponseUtil.getResult(ResponseCode.T_MESSAGE_SUCCESS_SEND);
            }else {
                return ResponseUtil.getResult(ResponseCode.T_MESSAGE_FAIL_SEND);
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lst(QueryModel model, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "upt"));
            Page<Message> page = messageRepository.findByIsreadAndTarget(model.getQueryType(), user, pageable);
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", page.getContent());
            data.put("total", page.getTotalElements());
            return ResponseUtil.revertMessageList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }

    @Override
    public String lstFromTarget(QueryModel model, Long userId) {
        User target = userRepository.findById(model.getUser().getId()).orElse(null);
        User current = userRepository.findById(userId).orElse(null);
        if (target != null && current != null) {
            Pageable pageable = PageRequest.of(model.getPage(), model.getRow(), new Sort(Sort.Direction.DESC, "upt"));
            Page<Message> page = messageRepository.findByCreatorAndTarget(current, target, pageable);
            HashMap<String, Object> data = new HashMap<>();
            data.put("lst", page.getContent());
            data.put("total", page.getTotalElements());
            return ResponseUtil.revertMessageList(ResponseUtil.getResultMap(ResponseCode.N_SUCCESS, data));
        }
        return ResponseUtil.getResult(ResponseCode.T_USER_EMPTY_FIND);
    }
}
