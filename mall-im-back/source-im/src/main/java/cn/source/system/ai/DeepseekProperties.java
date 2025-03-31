package cn.source.system.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhl
 * @create 2025/1/8 13:53
 */
@Data
@Component
@ConfigurationProperties(prefix = "mall-ai.deepseek")
public class DeepseekProperties {

    /**
     * 模型地址
     */
    private String url;

    /**
     * api key
     */
    private String apiKey;
}
