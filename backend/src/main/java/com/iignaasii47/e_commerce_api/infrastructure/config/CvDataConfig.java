package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.domain.port.out.CvDataProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class CvDataConfig {

    private static final Logger log = LoggerFactory.getLogger(CvDataConfig.class);
    private static final String CV_RESOURCE_PATH = "classpath:cv-data.md";

    @Bean
    public CvDataProvider cvDataProvider(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(CV_RESOURCE_PATH);
        if (!resource.exists()) {
            log.warn("CV data file not found at {}. The chatbot will run without CV context.", CV_RESOURCE_PATH);
            return () -> "(No CV data available. Please add your CV to src/main/resources/cv-data.md)";
        }
        try {
            String content = resource.getContentAsString(StandardCharsets.UTF_8);
            log.info("Loaded CV data, {} chars", content.length());
            return () -> content;
        } catch (IOException e) {
            log.error("Failed to read CV data from {}", CV_RESOURCE_PATH, e);
            return () -> "(Failed to load CV data)";
        }
    }

}
