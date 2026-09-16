package org.example.aipoweredmentalhealthassistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation_message")
public class ConsultationMessage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("session_id")
    private Long sessionId;

    @TableField("sender_type")
    private Integer senderType;

    @TableField("message_type")
    private Integer messageType;

    private String content;

    @TableField("emotion_tag")
    private String emotionTag;

    @TableField("ai_model")
    private String aiModel;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
