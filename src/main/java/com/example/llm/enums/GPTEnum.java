package com.example.llm.enums;

public enum GPTEnum {
    GPT_4("gpt-4", 4000, 0.7, 0.01),
    GPT_3_5_TURBO("gpt-3.5-turbo", 2000, 0.7, 0.002);

    private final String model;
    private final int maxToken;
    private final double temperature;
    private final double costPerToken;

    GPTEnum(String model, int maxToken, double temperature, double costPerToken) {
        this.model = model;
        this.maxToken = maxToken;
        this.temperature = temperature;
        this.costPerToken = costPerToken;
    }

    public String model() {
        return model;
    }

    public int maxToken() {
        return maxToken;
    }

    public double temperature() {
        return temperature;
    }

    public double costPerToken() {
        return costPerToken;
    }
}
