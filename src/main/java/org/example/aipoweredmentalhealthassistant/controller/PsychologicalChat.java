package org.example.aipoweredmentalhealthassistant.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.aipoweredmentalhealthassistant.DTO.command.CreateConsultationSessionCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.command.StreamChatCommandDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.ConsultationMessageResponseDTO;
import org.example.aipoweredmentalhealthassistant.DTO.response.ConsultationSessionResponseDTO;
import org.example.aipoweredmentalhealthassistant.common.Result;
import org.example.aipoweredmentalhealthassistant.service.ConsultationMessageService;
import org.example.aipoweredmentalhealthassistant.service.ConsultationSessionService;
import org.example.aipoweredmentalhealthassistant.service.StreamingChatService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/psychologicalchat")
public class PsychologicalChat {

    @Resource
    private ConsultationSessionService consultationSessionService;

    @Resource
    private ConsultationMessageService consultationMessageService;

    @Resource
    private StreamingChatService streamingChatService;

    @PostMapping("/session/{sessionId}/messages")
    public SseEmitter streamChat(
            Authentication authentication,
            @Positive(message = "会话ID必须为正数") @PathVariable Long sessionId,
            @Valid @RequestBody StreamChatCommandDTO commandDTO
    ) {
        return streamingChatService.stream(authentication.getName(), sessionId, commandDTO);
    }

    @PostMapping("/session")
    public Result<ConsultationSessionResponseDTO> createSession(
            Authentication authentication,
            @Valid @RequestBody CreateConsultationSessionCommandDTO commandDTO
    ) {
        return consultationSessionService.create(authentication.getName(), commandDTO);
    }

    @GetMapping("/session")
    public Result<List<ConsultationSessionResponseDTO>> listSessions(Authentication authentication) {
        return consultationSessionService.list(authentication.getName());
    }

    @GetMapping("/session/{sessionId}/messages")
    public Result<List<ConsultationMessageResponseDTO>> listMessages(
            Authentication authentication,
            @Positive(message = "会话ID必须为正数") @PathVariable Long sessionId
    ) {
        return Result.success(consultationMessageService.listHistory(authentication.getName(), sessionId));
    }
}
