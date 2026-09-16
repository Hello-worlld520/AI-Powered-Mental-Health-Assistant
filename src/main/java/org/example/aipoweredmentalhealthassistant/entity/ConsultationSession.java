package org.example.aipoweredmentalhealthassistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation_session")
public class ConsultationSession {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("session_title")
    private String sessionTitle;

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("last_emotion_analysis")
    private String lastEmotionAnalysis;

    @TableField("last_emotion_updated_at")
    private LocalDateTime lastEmotionUpdatedAt;
}
