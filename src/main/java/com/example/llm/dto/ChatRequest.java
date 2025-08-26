package com.example.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    @NotBlank(message = "프롬프트는 필수입니다")
    private String prompt;

    private String model;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Integer n;
    private Boolean stream;
    private List<String> stop;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private String user;
}
