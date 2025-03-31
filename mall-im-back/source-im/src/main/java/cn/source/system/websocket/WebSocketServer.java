package cn.source.system.websocket;

import cn.source.common.core.redis.RedisCache;
import cn.source.common.utils.StringUtils;
import cn.source.system.dto.MessageDTO;
import cn.source.system.service.IImMessagesService;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * websocket 消息处理
 *
 * @author ruoyi
 */
@Component
@ServerEndpoint("/websocket/message/{userId}")
public class WebSocketServer {
    /**
     * WebSocketServer 日志控制器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 注入消息redis
     */
    private static RedisCache redisCache;

    public static IImMessagesService iImMessagesService;

    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /**
     * 默认最多允许同时在线人数1000
     */
    public static int socketMaxOnlineCount = 1000;

    private static Semaphore socketSemaphore = new Semaphore(socketMaxOnlineCount);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) throws Exception {
        boolean semaphoreFlag = false;
        // 尝试获取信号量
        semaphoreFlag = SemaphoreUtils.tryAcquire(socketSemaphore);
        if (!semaphoreFlag) {
            // 未获取到信号量
            LOGGER.error("\n 当前在线人数超过限制数- {}", socketMaxOnlineCount);
            WebSocketUsers.sendMessageToUserByText(session, "当前在线人数超过限制数：" + socketMaxOnlineCount);
            session.close();
        } else {
            // 添加用户
            WebSocketUsers.put(userId, session);
            LOGGER.info("\n 建立连接 - {}", session);
            LOGGER.info("\n 当前人数 - {}", WebSocketUsers.getUsers().size());
        }
    }

    /**
     * 连接关闭时处理
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId, Session session) {
        LOGGER.info("\n 关闭连接 - {}", session);
        // 移除用户
        WebSocketUsers.remove(userId);
        // 获取到信号量则需释放
        SemaphoreUtils.release(socketSemaphore);
    }

    /**
     * 抛出异常时处理
     */
    @OnError
    public void onError(@PathParam("userId") Long userId, Session session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            // 关闭连接
            session.close();
        }
        String sessionId = session.getId();
        LOGGER.info("\n 连接异常 - {}", sessionId);
        LOGGER.info("\n 异常信息 - {}", exception);
        // 移出用户
        WebSocketUsers.remove(userId);
        // 获取到信号量则需释放
        SemaphoreUtils.release(socketSemaphore);
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if (StringUtils.equals("ping", message)) {
            // 回执 pong
            return;
        }
        MessageDTO messageDTO = JSON.parseObject(message, MessageDTO.class);

        if (Objects.nonNull(messageDTO)) {
            Long receiverId = messageDTO.getReceiverId();
            String content = messageDTO.getContent();
            Session session1 = WebSocketUsers.get(receiverId);

            // 保存消息记录
            iImMessagesService.sendMessage(messageDTO.getSendId(), receiverId, content);

            if (Objects.nonNull(session1)) {
                WebSocketUsers.sendMessageToUserByText(session1, messageDTO.getContent());
            } else {
                LOGGER.info("\n 用户 {} 未在线; 消息内容：{}", receiverId, content);
            }
        }
    }
}
