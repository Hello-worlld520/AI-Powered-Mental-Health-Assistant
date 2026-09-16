package org.example.aipoweredmentalhealthassistant.AiService;

public class StructOutPut {

    public record StreamChatSession(
            String sessionId,        // 会话唯一 ID
            Long userHash,           // 用户标识的哈希（不是明文 username）
            String initialMessage,   // 会话的初始消息
            Long startTime,          // 会话开始时间（时间戳）
            Long expiryTime,         // 会话过期时间
            Integer messageCount,    // 消息条数
            String status            // 会话状态
    ) {}
}