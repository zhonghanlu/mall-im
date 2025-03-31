package cn.source.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息记录表
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ImMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String content;

    /**
     * 消息类型 1:文本 2:图片 3:文件
     */
    private Integer messageType;

    /**
     * 0:未读 1:已读 2:撤回
     */
    private Integer status;

    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    /// ////////////////

    private Long chatUserId;

}
