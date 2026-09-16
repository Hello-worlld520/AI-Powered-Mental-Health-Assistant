package org.example.aipoweredmentalhealthassistant.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StreamChatCommandDTO {

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 10000, message = "消息内容不能超过10000个字符")
    private String message;
}
