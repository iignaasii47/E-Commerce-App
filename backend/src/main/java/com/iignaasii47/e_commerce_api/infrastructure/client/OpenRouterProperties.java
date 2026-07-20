package com.iignaasii47.e_commerce_api.infrastructure.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "openrouter")
@Getter
@Setter
public class OpenRouterProperties {

    private String apiKey;
    private String apiUrl = "https://openrouter.ai/api/v1";
    private String model = "meta-llama/llama-3.3-70b-instruct:free";
    private List<String> fallbackModels = new ArrayList<>();

}
