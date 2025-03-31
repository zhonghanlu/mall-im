package cn.source.system.ai.model;


import lombok.Data;

import java.util.List;

/**
 * 交互返回实体类
 */
@Data
public class ChatResponse {
    /**
     * 该对话的唯一标识符。
     */
    private String id;

    /**
     * 对象的类型, 其值为 chat.completion。
     */
    private String object;

    /**
     * 创建聊天完成时的 Unix 时间戳（以秒为单位）。
     */
    private int created;

    /**
     * 生成该 completion 的模型名。
     */
    private String model;

    /**
     * 模型生成的 completion 的选择列表。
     */
    private List<Choices> choices;

    /**
     * 该对话补全请求的用量信息。
     */
    private Usage usage;

    /**
     * This fingerprint represents the backend configuration that the model runs with.
     */
    private String system_fingerprint;


    /**
     * 模型生成的 completion 的选择列表。
     */
    @Data
    public static class Choices {
        /**
         * 该 completion 在模型生成的 completion 的选择列表中的索引。
         */
        private int index;

        private Message message;

        /**
         * 一个包含输出 token 对数概率信息的列表。
         */
        private String logprobs;

        /**
         * 模型停止生成 token 的原因。
         */
        private String finish_reason;

        private Delta delta;
    }

    /**
     * 流返回数据
     */
    @Data
    public static class Delta {
        private String content;
    }

    /**
     * 模型生成的 completion 消息。
     */
    @Data
    public static class Message {
        /**
         * 生成这条消息的角色。
         */
        private String role;

        private String content;
    }

    /**
     * 该对话补全请求的用量信息。
     */
    @Data
    public static class Usage {
        /**
         * 用户 prompt 所包含的 token 数。该值等于 prompt_cache_hit_tokens + prompt_cache_miss_tokens
         */
        private int prompt_tokens;

        /**
         * 模型 completion 产生的 token 数。
         */
        private int completion_tokens;

        /**
         * 该请求中，所有 token 的数量（prompt + completion）。
         */
        private int total_tokens;

        /**
         * 用户 prompt 中，命中上下文缓存的 token 数。
         */
        private int prompt_cache_hit_tokens;

        /**
         * 用户 prompt 中，未命中上下文缓存的 token 数。
         */
        private int prompt_cache_miss_tokens;

    }
}


