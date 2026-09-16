package org.example.aipoweredmentalhealthassistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aipoweredmentalhealthassistant.DTO.command.CreateConsultationSessionCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.ConsultationSessionResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.common.ResultCode;
import org.example.aipoweredmentalhealthassistant.entity.ConsultationSession;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.enumClass.UserStatus;
import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.example.aipoweredmentalhealthassistant.mapper.ConsultationSessionMapper;
import org.example.aipoweredmentalhealthassistant.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationSessionService extends ServiceImpl<ConsultationSessionMapper, ConsultationSession> {

    private final UserMapper userMapper;

    public ConsultationSessionService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Result<ConsultationSessionResponseDTO> create(
            String username,
            CreateConsultationSessionCommandDTO commandDTO
    ) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        User user = getActiveUser(username);

        ConsultationSession session = new ConsultationSession();
        session.setUserId(user.getId());
        session.setSessionTitle(resolveTitle(commandDTO));
        session.setStartedAt(LocalDateTime.now());
        baseMapper.insert(session);

        ConsultationSessionResponseDTO response = new ConsultationSessionResponseDTO();
        response.setSessionId(session.getId());
        response.setSessionTitle(session.getSessionTitle());
        response.setStartedAt(session.getStartedAt());
        return Result.success(response);
    }

    public Result<List<ConsultationSessionResponseDTO>> list(String username) {
        User user = getActiveUser(username);
        List<ConsultationSessionResponseDTO> sessions = baseMapper.selectList(
                        new LambdaQueryWrapper<ConsultationSession>()
                                .eq(ConsultationSession::getUserId, user.getId())
                                .orderByDesc(ConsultationSession::getStartedAt)
                                .orderByDesc(ConsultationSession::getId)
                ).stream()
                .map(this::toResponse)
                .toList();
        return Result.success(sessions);
    }

    private User getActiveUser(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (!UserStatus.NORMAL.getCode().equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return user;
    }

    private ConsultationSessionResponseDTO toResponse(ConsultationSession session) {
        ConsultationSessionResponseDTO response = new ConsultationSessionResponseDTO();
        response.setSessionId(session.getId());
        response.setSessionTitle(session.getSessionTitle());
        response.setStartedAt(session.getStartedAt());
        return response;
    }

    private String resolveTitle(CreateConsultationSessionCommandDTO commandDTO) {
        if (commandDTO == null || !StringUtils.hasText(commandDTO.getSessionTitle())) {
            return "心理咨询";
        }
        return commandDTO.getSessionTitle().trim();
    }
}
