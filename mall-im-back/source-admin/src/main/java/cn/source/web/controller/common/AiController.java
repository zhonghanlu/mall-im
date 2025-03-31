package cn.source.web.controller.common;

import cn.source.common.core.domain.AiChatRequest;
import cn.source.system.ai.DeepseekServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * @author zhl
 * @create 2025/1/13 14:21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final DeepseekServer deepseekServer;

    @PostMapping("/chat")
    public StreamingResponseBody chat(@RequestBody() AiChatRequest request) {
        return deepseekServer.streamChat(request.getMessage());
    }

}
