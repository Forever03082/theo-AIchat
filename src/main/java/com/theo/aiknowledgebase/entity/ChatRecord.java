package com.theo.aiknowledgebase.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRecord {

    private Long id;
    private Long sessionId;
    private String username;
    private String question;
    private String answer;
    private LocalDateTime createTime;
}