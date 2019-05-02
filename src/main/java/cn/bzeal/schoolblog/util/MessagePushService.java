package cn.bzeal.schoolblog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务类
 * Created by Godbobo on 2019/4/11.
 */
@Component
@ServerEndpoint(value = "/ws/message/{userId}")
public class MessagePushService {

    private final Logger logger = LoggerFactory.getLogger(MessagePushService.class);

    // 静态变量，用于记录当前连接数量，应该设计为线程安全的
    private static int onlineCount = 0;

    // 线程安全的Set，用来存放每个客户端对应的WebSocket对象
    private static CopyOnWriteArraySet<MessagePushService> clientMap = new CopyOnWriteArraySet<>();

    // 与某个客户端之间的会话，需要通过它给客户端发送数据
    private Session session;

    // 该连接对应的用户
    private Long userId;

    // 连接建立成功调用的方法
    @OnOpen
    public void onOpen(@PathParam("userId") String username, Session session) {
        this.session = session;
        this.userId = Long.parseLong(username);
        clientMap.add(this);
        addOnlineCount();
        logger.info(session.getId() + "有新连接加入，当前连接数为:" + MessagePushService.getOnlineCount());
    }

    // 连接关闭时
    @OnClose
    public void onClose() {
        clientMap.remove(this);
        subOnlineCount();
        logger.info("一个客户端断开连接，当前连接数为:" + MessagePushService.getOnlineCount());
    }

    // 收到消息时
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("来自客户端的消息:" + message);
    }

    // 发生错误时
    @OnError
    public void onError(Session session, Throwable error) {
        logger.debug("client发生错误");
        error.printStackTrace();
    }

    // 给所有客户端发送消息
    public void sendMsgToAll(String message) throws IOException {
        for (MessagePushService item : clientMap) {
            item.session.getBasicRemote().sendText(message);
        }
        logger.info("成功群发消息，当前客户端数量为:" + MessagePushService.getOnlineCount());
    }

    // 发送消息
    public void sendMsg(String message, Long userId) throws IOException {
        for(MessagePushService item: clientMap) {
            if (item.userId.equals(userId)){
                item.session.getBasicRemote().sendText(message);
                logger.info("成功发送消息给" + this.userId);
                break;
            }
        }
    }

    // 获取在线数量
    private static synchronized int getOnlineCount() {
        return MessagePushService.onlineCount;
    }

    // 新增客户端连接
    private static synchronized void addOnlineCount() {
        MessagePushService.onlineCount++;
    }

    // 客户端断开连接
    private static synchronized void subOnlineCount() {
        MessagePushService.onlineCount--;
    }

}
