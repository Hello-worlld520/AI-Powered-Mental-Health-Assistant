package org.example.aipoweredmentalhealthassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥。
     */
    private String secret;

    /**
     * Token 有效期，单位：毫秒。
     */
    private long expiration;

    /**
     * 携带 Token 的请求头名称。
     */
    private String header;

    /**
     * Token 类型前缀。
     */
    private String prefix;
}
