package org.example.aipoweredmentalhealthassistant.service;

import org.example.aipoweredmentalhealthassistant.DTO.response.ConsultationSessionResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.entity.ConsultationSession;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.mapper.ConsultationSessionMapper;
import org.example.aipoweredmentalhealthassistant.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationSessionServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ConsultationSessionMapper sessionMapper;

    private ConsultationSessionService consultationSessionService;

    @BeforeEach
    void setUp() {
        consultationSessionService = new ConsultationSessionService(userMapper);
        ReflectionTestUtils.setField(consultationSessionService, "baseMapper", sessionMapper);
    }

    @Test
    void listReturnsCurrentUsersSessionsInMapperOrder() {
        User user = new User();
        user.setId(7L);
        user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);
        when(sessionMapper.selectList(any())).thenReturn(List.of(
                session(3L, "最近会话", LocalDateTime.of(2026, 9, 15, 12, 0)),
                session(1L, "较早会话", LocalDateTime.of(2026, 9, 14, 12, 0))
        ));

        Result<List<ConsultationSessionResponseDTO>> result = consultationSessionService.list("test");

        assertEquals("200", result.getCode());
        assertEquals(List.of(3L, 1L), result.getData().stream()
                .map(ConsultationSessionResponseDTO::getSessionId)
                .toList());
    }

    private ConsultationSession session(Long id, String title, LocalDateTime startedAt) {
        ConsultationSession session = new ConsultationSession();
        session.setId(id);
        session.setSessionTitle(title);
        session.setStartedAt(startedAt);
        return session;
    }
}
