package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 发送消息
    @RequestMapping("/send")
    public String send(QueryModel model) {
        if (model.getUser() == null || model.getUser().getId() == null || model.getMessage() == null || model.getMessage().getContent() == null) {
            return defaultResult();
        }
        String userId = getRequest().getAttribute("uid").toString();
        return messageService.send(model, Long.parseLong(userId));
    }

    // 删除消息
    @RequestMapping("/delete")
    public String delete(QueryModel model) {
        String userId = getRequest().getAttribute("uid").toString();
        if (model.getUser() == null || model.getUser().getId() == null ) {
            return defaultResult();
        }
        return messageService.delete(model.getUser().getId(), Long.parseLong(userId));
    }

    // 消息列表
    @RequestMapping("/lst")
    public String lst(QueryModel model) {
        String userId = getRequest().getAttribute("uid").toString();
        return messageService.lst(model, Long.parseLong(userId));
    }

    // 与某人的聊天记录
    @RequestMapping("lstFromTarget")
    public String lstFromTarget(QueryModel model) {
        if (model.getUser() == null || model.getUser().getId() == null) {
            return defaultResult();
        }
        String userId = getRequest().getAttribute("uid").toString();
        return messageService.lstFromTarget(model, Long.parseLong(userId));
    }

}
