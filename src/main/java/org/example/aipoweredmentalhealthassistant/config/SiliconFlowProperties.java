package org.example.aipoweredmentalhealthassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.silicon-flow")
public class SiliconFlowProperties {

    private String baseUrl = "https://api.siliconflow.cn/v1";
    private String apiKey;
    private String model = "tencent/Hunyuan-MT-7B";
    private Integer maxTokens = 4096;
}
