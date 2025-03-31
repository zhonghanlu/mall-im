package cn.source.system.dto;

import lombok.Data;

/**
 * @Author : zhl
 * @Date: 2025/3/9 21:26
 * @Description:
 */
@Data
@SuppressWarnings("all")
public class MessageDTO {

    private Long sendId;

    private Long receiverId;

    private String content;

}
