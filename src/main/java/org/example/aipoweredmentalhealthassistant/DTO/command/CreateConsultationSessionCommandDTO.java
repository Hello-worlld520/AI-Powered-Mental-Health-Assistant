package org.example.aipoweredmentalhealthassistant.DTO.command;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateConsultationSessionCommandDTO {

    @Size(max = 200, message = "会话标题不能超过200个字符")
    private String sessionTitle;
}
