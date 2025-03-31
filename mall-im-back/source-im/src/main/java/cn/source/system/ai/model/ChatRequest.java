package cn.source.system.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 交互请求实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    /**
     * 对话的消息列表。
     */
    private List<Messages> messages;

    /**
     * 使用的模型的 ID。您可以使用 deepseek-chat。
     */
    private String model;

    /**
     * 介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其在已有文本中的出现频率受到相应的惩罚，降低模型重复相同内容的可能性。
     */
    private int frequency_penalty;

    /**
     * 介于 1 到 8192 间的整数，限制一次请求中模型生成 completion 的最大 token 数。输入 token 和输出 token 的总长度受模型的上下文长度的限制。
     * 如未指定 max_tokens参数，默认使用 4096。
     */
    private int max_tokens;

    /**
     * 介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其是否已在已有文本中出现受到相应的惩罚，从而增加模型谈论新主题的可能性。
     */
    private int presence_penalty;

    /**
     * 一个 object，指定模型必须输出的格式。
     * <p>
     * 设置为 { "type": "json_object" } 以启用 JSON 模式，该模式保证模型生成的消息是有效的 JSON。
     * <p>
     * 注意: 使用 JSON 模式时，你还必须通过系统或用户消息指示模型生成 JSON。否则，模型可能会生成不断的空白字符，直到生成达到令牌限制，
     * 从而导致请求长时间运行并显得“卡住”。此外，如果 finish_reason="length"，这表示生成超过了 max_tokens 或对话超过了最大上下文长度，
     * 消息内容可能会被部分截断。
     */
    private Response_format response_format;

    /**
     * 一个 string 或最多包含 16 个 string 的 list，在遇到这些词时，API 将停止生成更多的 token。
     */
    private String stop;

    /**
     * 如果设置为 True，将会以 SSE（server-sent events）的形式以流式发送消息增量。消息流以 data: [DONE] 结尾。
     */
    private boolean stream;

    /**
     * 流式输出相关选项。只有在 stream 参数为 true 时，才可设置此参数。
     * <p>
     * 如果设置为 true，在流式消息最后的 data: [DONE] 之前将会传输一个额外的块。此块上的 usage 字段显示整个请求的 token 使用统计信息，
     * 而 choices 字段将始终是一个空数组。所有其他块也将包含一个 usage 字段，但其值为 null。
     */
    private String stream_options;

    /**
     * 采样温度，介于 0 和 2 之间。更高的值，如 0.8，会使输出更随机，而更低的值，如 0.2，会使其更加集中和确定。
     * 我们通常建议可以更改这个值或者更改 top_p，但不建议同时对两者进行修改。
     */
    private int temperature;

    /**
     * 作为调节采样温度的替代方案，模型会考虑前 top_p 概率的 token 的结果。所以 0.1 就意味着只有包括在最高 10% 概率中的 token 会被考虑。
     * 我们通常建议修改这个值或者更改 temperature，但不建议同时对两者进行修改。
     */
    private int top_p;

    /**
     * 模型可能会调用的 tool 的列表。目前，仅支持 function 作为工具。使用此参数来提供以 JSON 作为输入参数的 function 列表。最多支持 128 个 function。
     */
    private String tools;

    /**
     * 控制模型调用 tool 的行为。
     */
    private String tool_choice;

    /**
     * 是否返回所输出 token 的对数概率。如果为 true，则在 message 的 content 中返回每个输出 token 的对数概率。
     */
    private boolean logprobs;

    /**
     * 一个介于 0 到 20 之间的整数 N，指定每个输出位置返回输出概率 top N 的 token，且返回这些 token 的对数概率。
     * 指定此参数时，logprobs 必须为 true。
     */
    private String top_logprobs;

    /**
     * 对话的消息列表。
     */
    @Data
    public static class Messages {
        /**
         * 消息的内容。
         */
        private String content;

        /**
         * 该消息的发起角色 system、user、assistant。
         */
        private String role;

        /**
         * 选填
         */
        private String name;
    }

    @Data
    public static class Response_format {
        private String type;

    }
}
