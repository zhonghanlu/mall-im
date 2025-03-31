package cn.source.system.service.impl;

import cn.source.common.exception.ServiceException;
import cn.source.common.utils.SecurityUtils;
import cn.source.common.utils.uuid.IDGenerator;
import cn.source.system.domain.ImMessages;
import cn.source.system.domain.ImRelationships;
import cn.source.system.mapper.ImMessagesMapper;
import cn.source.system.mapper.ImRelationshipsMapper;
import cn.source.system.service.IImMessagesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息记录表 服务实现类
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Service
@RequiredArgsConstructor
public class ImMessagesServiceImpl implements IImMessagesService {

    private final ImMessagesMapper imMessagesMapper;

    private final ImRelationshipsMapper imRelationshipsMapper;

    @Override
    public List<ImRelationships> temporaryChat() {
        Long userId = SecurityUtils.getUserId();
        List<ImMessages> temporaryChatList = imMessagesMapper.temporaryChat(userId);

        if (CollectionUtils.isNotEmpty(temporaryChatList)) {
            Set<Long> chatUserId = temporaryChatList.stream().map(ImMessages::getChatUserId).collect(Collectors.toSet());

            List<ImRelationships> imRelationshipsList = imRelationshipsMapper.selectInfoList(chatUserId);

            Map<Long, ImMessages> imMessagesMap = temporaryChatList.stream()
                    .collect(Collectors.toMap(ImMessages::getChatUserId,
                            Function.identity(), (k1, k2) -> k2));

            imRelationshipsList.forEach(relationships -> {
                ImMessages imMessages = imMessagesMap.get(relationships.getFriendId());
                if (Objects.nonNull(imMessages)) {
                    relationships.setLastTime(imMessages.getSentAt());
                }
            });
            return imRelationshipsList;
        }

        return Collections.emptyList();
    }

    @Override
    public List<ImMessages> getSingleChat(Long userId, Long chatUserId) {
        return imMessagesMapper.getSingleChat(userId, chatUserId);
    }

    @Override
    public void sendMessage(Long sendId, Long receiverId, String content) {
        ImMessages imMessages = new ImMessages();
        imMessages.setId(IDGenerator.next());
        imMessages.setSenderId(sendId);
        imMessages.setReceiverId(receiverId);
        imMessages.setContent(content);
        imMessages.setMessageType(0);
        imMessages.setStatus(0);
        imMessages.setSentAt(LocalDateTime.now());

        int i = imMessagesMapper.insert(imMessages);

        if (i <= 0) {
            throw new ServiceException("发送失败");
        }
    }
}
