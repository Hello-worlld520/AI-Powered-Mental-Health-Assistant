package org.example.aipoweredmentalhealthassistant.service;

import org.example.aipoweredmentalhealthassistant.DTO.response.ConsultationMessageResponseDTO;
import org.example.aipoweredmentalhealthassistant.entity.ConsultationMessage;
import org.example.aipoweredmentalhealthassistant.entity.ConsultationSession;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.exception.BusinessException;
import org.example.aipoweredmentalhealthassistant.mapper.ConsultationMessageMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationMessageServiceTest {

    @Mock
    private ConsultationMessageMapper messageMapper;

    @Mock
    private ConsultationSessionMapper sessionMapper;

    @Mock
    private UserMapper userMapper;

    private ConsultationMessageService consultationMessageService;

    @BeforeEach
    void setUp() {
        consultationMessageService = new ConsultationMessageService(sessionMapper, userMapper);
        ReflectionTestUtils.setField(consultationMessageService, "baseMapper", messageMapper);
    }

    @Test
    void listHistoryMapsStoredSenderTypesToFrontendRoles() {
        when(userMapper.selectOne(any())).thenReturn(activeUser());
        when(sessionMapper.selectOne(any())).thenReturn(ownedSession());
        when(messageMapper.selectList(any())).thenReturn(List.of(
                message(1L, 1, "我感到焦虑"),
                message(2L, 2, "我会陪着你")));

        List<ConsultationMessageResponseDTO> result = consultationMessageService.listHistory("test", 11L);

        assertEquals(List.of("user", "assistant"), result.stream()
                .map(ConsultationMessageResponseDTO::getRole)
                .toList());
        assertEquals(List.of("我感到焦虑", "我会陪着你"), result.stream()
                .map(ConsultationMessageResponseDTO::getContent)
                .toList());
    }

    @Test
    void listHistoryRejectsSessionNotOwnedByCurrentUser() {
        when(userMapper.selectOne(any())).thenReturn(activeUser());
        when(sessionMapper.selectOne(any())).thenReturn(null);

        assertThrows(BusinessException.class, () -> consultationMessageService.listHistory("test", 12L));

        verify(messageMapper, never()).selectList(any());
    }

    private User activeUser() {
        User user = new User();
        user.setId(7L);
        user.setStatus(1);
        return user;
    }

    private ConsultationSession ownedSession() {
        ConsultationSession session = new ConsultationSession();
        session.setId(11L);
        session.setUserId(7L);
        return session;
    }

    private ConsultationMessage message(Long id, int senderType, String content) {
        ConsultationMessage message = new ConsultationMessage();
        message.setId(id);
        message.setSenderType(senderType);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.of(2026, 9, 15, 12, 0));
        return message;
    }
}
