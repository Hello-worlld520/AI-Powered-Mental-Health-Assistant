package org.example.aipoweredmentalhealthassistant.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.example.aipoweredmentalhealthassistant.config.JwtProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private final JwtProperties jwtProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType) {
        return getApplicationContext().getBean(beanName, requiredType);
    }

    public String generateToken(String username) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + jwtProperties.getExpiration());

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(getAlgorithm());
    }

    public boolean verifyToken(String token) {
        try {
            JWT.require(getAlgorithm())
                    .build()
                    .verify(removePrefix(token));
            return true;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                    .build()
                    .verify(removePrefix(token));
            return decodedJWT.getSubject();
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return null;
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtProperties.getSecret());
    }

    private static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring ApplicationContext 尚未初始化");
        }
        return applicationContext;
    }

    private String removePrefix(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token 不能为空");
        }
        String prefix = jwtProperties.getPrefix();
        if (prefix != null && token.startsWith(prefix)) {
            return token.substring(prefix.length());
        }
        return token;
    }
}
