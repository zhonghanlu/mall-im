package cn.source.system.ai;

import cn.hutool.json.JSONUtil;
import cn.source.common.constant.AiChatConstant;
import cn.source.common.constant.RedisConstant;
import cn.source.common.core.redis.RedisCache;
import cn.source.common.utils.JsonUtils;
import cn.source.common.utils.RedisUtils;
import cn.source.common.utils.http.ServletUtil;
import cn.source.system.ai.model.ChatRequest;
import cn.source.system.ai.model.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhl
 * @create 2025/1/8 13:52
 * @description deepseek 工具类
 * 多轮对话
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepseekServer {

    // client连接池
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES))
            .build();

    // deepseek配置
    private final DeepseekProperties deepseekProperties;

    /**
     * 流式对话
     */
    public StreamingResponseBody streamChat(String message) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // 初始化请求体
        ChatRequest chatRequest = initRequest();

        // 处理多轮会话
        List<ChatRequest.Messages> messagesList = beforeHandlerChat(message);

        // 塞入请求内容中
        chatRequest.setMessages(messagesList);
        RequestBody body = RequestBody.create(Objects.requireNonNull(JsonUtils.toJsonString(chatRequest)), mediaType);

        Request request = new Request.Builder()
                .url(deepseekProperties.getUrl())
                .post(body)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization", "Bearer " + deepseekProperties.getApiKey())
                .build();
        return getStreamResponseBody(request);
    }

    /**
     * 返回流式响应
     */
    private StreamingResponseBody getStreamResponseBody(Request request) {
        return outputStream -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    outputStream.write(("请求失败，状态码: " + response.code()).getBytes());
                    return;
                }

                BufferedSource source = response.body().source();
                StringBuilder assistantReply = new StringBuilder();

                while (!source.exhausted()) {
                    String line = source.readUtf8Line();
                    if (StringUtils.isNotBlank(line) && line.startsWith(AiChatConstant.BACK_PREFIX)) {
                        line = line.substring(6);
                        handleChatResponse(line, outputStream, assistantReply);
                    }
                }

                // 添加模型回复到对话历史
                afterHandlerChat(assistantReply);
            } catch (IOException e) {
                outputStream.write(("请求发送失败: " + e.getMessage()).getBytes());
            }
        };
    }

    /**
     * 多轮会话前置处理
     */
    private List<ChatRequest.Messages> beforeHandlerChat(String message) {
        String roundKey = RedisConstant.AI_CHAT_ROUND_HISTORY + ServletUtil.getClientIP(Objects.requireNonNull(ServletUtil.getRequest()));
        String chatHistoryKey = RedisConstant.AI_CHAT_HISTORY + ServletUtil.getClientIP(Objects.requireNonNull(ServletUtil.getRequest()));

        // map 实际key
        long atomicValue = RedisUtils.getAtomicValue(roundKey);
        if (atomicValue == 0) {
            RedisUtils.incrAtomicValue(roundKey);
        }
        String mapKey = chatHistoryKey + ":" + RedisUtils.getAtomicValue(roundKey);
        List<ChatRequest.Messages> messagesList = RedisUtils.getCacheMapValue(chatHistoryKey, mapKey);

        // 如果为空赋值一个空的数据
        if (CollectionUtils.isEmpty(messagesList)) {
            messagesList = new ArrayList<>();
        }

        // 判断此时聊天轮数，超过5轮重置
        int size = messagesList.size();
        Map<String, List<ChatRequest.Messages>> roleMap = messagesList.stream().collect(Collectors.groupingBy(ChatRequest.Messages::getRole));
        if (!roleMap.isEmpty()) {
            int assistantSize = roleMap.get(AiChatConstant.ASSISTANT).size();
            int userSize = roleMap.get(AiChatConstant.USER).size();
            if (size >= 10 && (assistantSize >= 5 && userSize >= 5)) {
                messagesList = new ArrayList<>();
                // 总轮数+1
                RedisUtils.incrAtomicValue(roundKey);
            }
        }

        // 不超过5轮塞入信息
        ChatRequest.Messages assistantMsg = new ChatRequest.Messages();
        assistantMsg.setRole(AiChatConstant.USER);
        assistantMsg.setContent(message);
        messagesList.add(assistantMsg);
        // 键入缓存 获取map 实际key
        mapKey = chatHistoryKey + ":" + RedisUtils.getAtomicValue(roundKey);
        RedisUtils.setCacheMapValue(chatHistoryKey, mapKey, messagesList);
        // 设置全部消息记录过期 七天
        RedisUtils.expire(chatHistoryKey, 7, TimeUnit.DAYS);
        return messagesList;
    }

    /**
     * 多轮会话后置处理
     */
    private void afterHandlerChat(StringBuilder assistantReply) {
        String roundKey = RedisConstant.AI_CHAT_ROUND_HISTORY + ServletUtil.getClientIP(Objects.requireNonNull(ServletUtil.getRequest()));
        String chatHistoryKey = RedisConstant.AI_CHAT_HISTORY + ServletUtil.getClientIP(Objects.requireNonNull(ServletUtil.getRequest()));
        ChatRequest.Messages assistantMsg = new ChatRequest.Messages();
        assistantMsg.setRole(AiChatConstant.ASSISTANT);
        assistantMsg.setContent(assistantReply.toString());
        // 塞入缓存 获取Map实际key
        String mapKey = chatHistoryKey + ":" + RedisUtils.getAtomicValue(roundKey);
        List<ChatRequest.Messages> cacheList = RedisUtils.getCacheMapValue(chatHistoryKey, mapKey);
        cacheList.add(assistantMsg);
        RedisUtils.setCacheMapValue(chatHistoryKey, mapKey, cacheList);
    }

    /**
     * 刷入数据
     */
    private void handleChatResponse(String line, OutputStream outputStream, StringBuilder assistantReply) throws IOException {
        if (isJson(line)) {
            ChatResponse chatResponse = JSONUtil.toBean(line, ChatResponse.class);
            List<ChatResponse.Choices> choicesList = chatResponse.getChoices();
            if (CollectionUtils.isNotEmpty(choicesList)) {
                ChatResponse.Choices message = choicesList.get(0);
                if (Objects.nonNull(message) && Objects.nonNull(message.getDelta())) {
                    String content = message.getDelta().getContent();
                    if (Objects.nonNull(content)) {
                        outputStream.write(content.getBytes());
                        outputStream.flush();
                        assistantReply.append(content);
                    }
                }
            }
        }
    }


    /**
     * 初始化请求参数
     */
    private ChatRequest initRequest() {
        ChatRequest chatRequest = new ChatRequest();

        chatRequest.setModel("deepseek-chat");
        chatRequest.setFrequency_penalty(0);
        chatRequest.setMax_tokens(4096);
        chatRequest.setPresence_penalty(0);

        ChatRequest.Response_format responseFormat = new ChatRequest.Response_format();
        responseFormat.setType("text");
        chatRequest.setResponse_format(responseFormat);

        chatRequest.setStream(true); // 设置为流式响应
        chatRequest.setTemperature(1);
        chatRequest.setTop_p(1);
        return chatRequest;
    }

    /**
     * 判断文件是否为json
     */
    public boolean isJson(String input) {
        try {
            // 使用ObjectMapper的readValue方法尝试解析字符串，若成功则为合法的json
            new ObjectMapper().readValue(input, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
