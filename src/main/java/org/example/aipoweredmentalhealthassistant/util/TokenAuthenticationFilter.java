package org.example.aipoweredmentalhealthassistant.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.aipoweredmentalhealthassistant.config.JwtProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
/*TokenAuthenticationFilter
作用是：

从每次请求中提取 JWT，并将认证结果交给 Spring Security。

它负责：

获取请求头中的 Token；
调用 JwtUtil 验证 Token；
解析用户名；
创建 Spring Security 认证对象；
保存到 SecurityContextHolder。*/
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = request.getHeader(jwtProperties.getHeader());

        if (token != null
                && !token.isBlank()
                && jwtUtil.verifyToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtil.getUsername(token);
            if (username != null && !username.isBlank()) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
