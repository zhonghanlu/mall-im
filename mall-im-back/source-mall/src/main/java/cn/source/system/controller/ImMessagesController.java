package cn.source.system.controller;


import cn.source.common.core.domain.AjaxResult;
import cn.source.system.service.IImMessagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 消息记录表 前端控制器
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/im-messages")
public class ImMessagesController {

    private final IImMessagesService iImMessagesService;

    @GetMapping("/temporary-chat")
    public AjaxResult temporaryChat() {
        return AjaxResult.success(iImMessagesService.temporaryChat());
    }

    @GetMapping("/single-chat")
    public AjaxResult getSingleChat(Long userId, Long chatUserId) {
        return AjaxResult.success(iImMessagesService.getSingleChat(userId, chatUserId));
    }


}
