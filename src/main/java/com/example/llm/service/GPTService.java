package com.example.llm.service;

import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.enums.GPTEnum;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GPTService {
    private final OpenAiService openAiService;

    public GPTService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Value("${openai.default-model}")
    private String defaultModel;

    @Value("${openai.fallback-model}")
    private String fallbackModel;

    @Cacheable("openai-responses")
    public ChatResponse generateResponse(ChatRequest request) {
        long startTime = System.currentTimeMillis();

        String model = getOptimalModel(request.getModel());

        try {
            Map<String, Object> config = getModelConfig(model);

            ChatCompletionRequest chatRequest = buildChatRequest(request, model, config);
            ChatCompletionResult result = openAiService.createChatCompletion(chatRequest);

            String response = result.getChoices().getFirst().getMessage().getContent();
            Long tokensUsed = result.getUsage().getTotalTokens();
            String finishReason = result.getChoices().getFirst().getFinishReason();

            long responseTime = System.currentTimeMillis() - startTime;
            Double cost = calculateCost(model, tokensUsed);

            return ChatResponse.builder()
                .id(result.getId())
                .response(response)
                .model(model)
                .tokensUsed(tokensUsed)
                .responseTime(responseTime)
                .timestamp(LocalDateTime.now())
                .finishReason(finishReason)
                .cost(cost)
                .build();

        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생: {}", e.getMessage());

            if (!model.equals(fallbackModel)) {
                log.info("폴백 모델 {}로 재시도합니다.", fallbackModel);
                request.setModel(fallbackModel);
                return generateResponse(request);
            }

            throw new RuntimeException("OpenAI API 호출 실패", e);
        }
    }

    private Map<String, Object> getModelConfig(String model) {
        Map<String, Object> config = new HashMap<>();

        if (GPTEnum.GPT_3_5_TURBO.model().equals(model)) {
            config.put("maxToken", GPTEnum.GPT_3_5_TURBO.maxToken());
            config.put("temperature", GPTEnum.GPT_3_5_TURBO.temperature());
        } else {
            config.put("maxToken", GPTEnum.GPT_4_TURBO_PREVIEW.maxToken());
            config.put("temperature", GPTEnum.GPT_4_TURBO_PREVIEW.temperature());
        }

        return config;
    }

    private ChatCompletionRequest buildChatRequest(ChatRequest request, String model, Map<String, Object> config) {
        ChatCompletionRequest.ChatCompletionRequestBuilder builder = ChatCompletionRequest.builder()
            .model(model)
            .messages(List.of(new ChatMessage("user", request.getPrompt())));

        if (request.getMaxTokens() != null) {
            builder.maxTokens(request.getMaxTokens());
        } else if (config.get("maxToken") != null) {
            builder.maxTokens((Integer) config.get("maxToken"));
        }

        if (request.getTemperature() != null) {
            builder.temperature(request.getTemperature());
        } else if (config.get("temperature") != null) {
            builder.temperature((Double) config.get("temperature"));
        }

        // 추가 옵션들
        if (request.getTopP() != null) builder.topP(request.getTopP());
        if (request.getN() != null) builder.n(request.getN());
        if (request.getStream() != null) builder.stream(request.getStream());
        if (request.getStop() != null) builder.stop(request.getStop());
        if (request.getPresencePenalty() != null) builder.presencePenalty(request.getPresencePenalty());
        if (request.getFrequencyPenalty() != null) builder.frequencyPenalty(request.getFrequencyPenalty());
        if (request.getUser() != null) builder.user(request.getUser());

        return builder.build();
    }

    private String getOptimalModel(String requestedModel) {
        if (requestedModel != null && !requestedModel.isEmpty()) {
            return requestedModel;
        }

        return defaultModel;
    }

    private Double calculateCost(String model, Long tokens) {
        Map<String, Double> costPer1kTokens = Map.of(
            GPTEnum.GPT_4.model(), GPTEnum.GPT_4.costPerToken(),
            GPTEnum.GPT_3_5_TURBO.model(), GPTEnum.GPT_3_5_TURBO.costPerToken()
        );

        Double costPerToken = costPer1kTokens.getOrDefault(model, 0.01);
        return (tokens / 1000.0) * costPerToken;
    }
}

