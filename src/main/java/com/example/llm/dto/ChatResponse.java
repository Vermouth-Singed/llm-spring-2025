package com.example.llm.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class ChatResponse {
    private String id;
    private String response;
    private String model;
    private Long tokensUsed;
    private Long responseTime;
    private LocalDateTime timestamp;
    private String finishReason;
    private Double cost;
}
