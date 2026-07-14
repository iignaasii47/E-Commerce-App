package com.iignaasii47.e_commerce_api.infrastructure.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenRouterPropertiesTest {

    @Test
    void shouldSetAndGetApiKey() {
        OpenRouterProperties props = new OpenRouterProperties();
        props.setApiKey("test-key");

        assertThat(props.getApiKey()).isEqualTo("test-key");
    }

    @Test
    void shouldSetAndGetApiUrl() {
        OpenRouterProperties props = new OpenRouterProperties();
        props.setApiUrl("https://custom.url/api");

        assertThat(props.getApiUrl()).isEqualTo("https://custom.url/api");
    }

    @Test
    void shouldSetAndGetModel() {
        OpenRouterProperties props = new OpenRouterProperties();
        props.setModel("custom-model");

        assertThat(props.getModel()).isEqualTo("custom-model");
    }

    @Test
    void shouldHaveDefaultApiUrl() {
        OpenRouterProperties props = new OpenRouterProperties();

        assertThat(props.getApiUrl()).isEqualTo("https://openrouter.ai/api/v1");
    }

    @Test
    void shouldHaveDefaultModel() {
        OpenRouterProperties props = new OpenRouterProperties();

        assertThat(props.getModel()).isEqualTo("meta-llama/llama-3.3-70b-instruct:free");
    }

}
