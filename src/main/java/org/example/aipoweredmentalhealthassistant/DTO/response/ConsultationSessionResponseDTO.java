package org.example.aipoweredmentalhealthassistant.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationSessionResponseDTO {

    private Long sessionId;
    private String sessionTitle;
    private LocalDateTime startedAt;
}
