package org.example.aipoweredmentalhealthassistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SiliconFlowConfig {

    @Bean
    public RestClient siliconFlowRestClient(
            RestClient.Builder builder,
            SiliconFlowProperties properties
    ) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .defaultHeaders(headers -> {
                    if (properties.getApiKey() != null && !properties.getApiKey().isBlank()) {
                        headers.setBearerAuth(properties.getApiKey());
                    }
                })
                .build();
    }
}
