package com.example.llm.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {
    @Value("${openai.key}")
    private String key;

    @Value("${openai.timeout}")
    private Integer timeout;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(key, Duration.ofSeconds(timeout));
    }
}
