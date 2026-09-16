package org.example.aipoweredmentalhealthassistant.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationMessageResponseDTO {

    private Long messageId;
    private String role;
    private String content;
    private Integer messageType;
    private String emotionTag;
    private String aiModel;
    private LocalDateTime createdAt;
}
