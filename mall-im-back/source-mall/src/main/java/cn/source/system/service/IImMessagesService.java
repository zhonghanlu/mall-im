package cn.source.system.service;

import cn.source.system.domain.ImMessages;
import cn.source.system.domain.ImRelationships;

import java.util.List;

/**
 * <p>
 * 消息记录表 服务类
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
public interface IImMessagesService {

    List<ImRelationships> temporaryChat();

    /**
     * 获取单聊信息
     */
    List<ImMessages> getSingleChat(Long userId, Long chatUserId);

    void sendMessage(Long sendId, Long receiverId, String content);
}
