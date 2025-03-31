package cn.source.system.mapper;

import cn.source.system.domain.ImMessages;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 消息记录表 Mapper 接口
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
public interface ImMessagesMapper {

    List<ImMessages> temporaryChat(Long userId);

    List<ImMessages> getSingleChat(@Param("userId") Long userId, @Param("chatUserId") Long chatUserId);

    int insert(ImMessages imMessages);
}
