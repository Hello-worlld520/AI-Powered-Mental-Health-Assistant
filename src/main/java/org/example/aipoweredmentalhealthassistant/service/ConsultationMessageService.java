package org.example.aipoweredmentalhealthassistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aipoweredmentalhealthassistant.DTO.response.ConsultationMessageResponseDTO;
import org.example.aipoweredmentalhealthassistant.entity.ConsultationMessage;
import org.example.aipoweredmentalhealthassistant.entity.ConsultationSession;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.enumClass.UserStatus;
import org.example.aipoweredmentalhealthassistant.common.ResultCode;
import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.example.aipoweredmentalhealthassistant.mapper.ConsultationMessageMapper;
import org.example.aipoweredmentalhealthassistant.mapper.ConsultationSessionMapper;
import org.example.aipoweredmentalhealthassistant.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationMessageService extends ServiceImpl<ConsultationMessageMapper, ConsultationMessage> {

    private static final int USER_SENDER = 1;
    private static final int AI_SENDER = 2;

    private final ConsultationSessionMapper sessionMapper;
    private final UserMapper userMapper;

    public ConsultationMessageService(
            ConsultationSessionMapper sessionMapper,
            UserMapper userMapper
    ) {
        this.sessionMapper = sessionMapper;
        this.userMapper = userMapper;
    }

    public ConsultationSession verifySession(String username, Long sessionId) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (sessionId == null || sessionId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (!UserStatus.NORMAL.getCode().equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        ConsultationSession session = sessionMapper.selectOne(new LambdaQueryWrapper<ConsultationSession>()
                .eq(ConsultationSession::getId, sessionId)
                .eq(ConsultationSession::getUserId, user.getId()));
        if (session == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return session;
    }

    public List<ConsultationMessageResponseDTO> listHistory(String username, Long sessionId) {
        verifySession(username, sessionId);
        return baseMapper.selectList(new LambdaQueryWrapper<ConsultationMessage>()
                        .eq(ConsultationMessage::getSessionId, sessionId)
                        .orderByAsc(ConsultationMessage::getCreatedAt)
                        .orderByAsc(ConsultationMessage::getId))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void saveUserMessage(Long sessionId, String content) {
        saveMessage(sessionId, USER_SENDER, content, null);
    }

    public void saveAiMessage(Long sessionId, String content, String model) {
        saveMessage(sessionId, AI_SENDER, content, model);
    }

    private void saveMessage(Long sessionId, int senderType, String content, String model) {
        ConsultationMessage message = new ConsultationMessage();
        message.setSessionId(sessionId);
        message.setSenderType(senderType);
        message.setMessageType(1);
        message.setContent(content);
        message.setAiModel(model);
        message.setCreatedAt(LocalDateTime.now());
        baseMapper.insert(message);
    }

    private ConsultationMessageResponseDTO toResponse(ConsultationMessage message) {
        ConsultationMessageResponseDTO response = new ConsultationMessageResponseDTO();
        response.setMessageId(message.getId());
        response.setRole(message.getSenderType() == USER_SENDER ? "user" : "assistant");
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setEmotionTag(message.getEmotionTag());
        response.setAiModel(message.getAiModel());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
