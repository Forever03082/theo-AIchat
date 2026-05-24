package com.theo.aiknowledgebase.entity;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Data
public class Session {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String title;
    private LocalDateTime createTime;
}