package com.iignaasii47.e_commerce_api.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DotEnvEnvironmentPostProcessor.class);
    private static final String ENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envPath = findEnvFile();
        if (envPath == null) {
            log.debug("No .env file found in working directory or backend/ subdirectory");
            return;
        }

        try {
            Map<String, Object> envProperties = new HashMap<>();
            List<String> lines = Files.readAllLines(envPath);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eqIndex = line.indexOf('=');
                if (eqIndex > 0) {
                    String key = line.substring(0, eqIndex).trim();
                    String value = line.substring(eqIndex + 1).trim();
                    envProperties.put(key, value);
                }
            }

            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envProperties));
            log.info("Loaded {} properties from {}", envProperties.size(), envPath.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to read .env file: {}", e.getMessage());
        }
    }

    private static Path findEnvFile() {
        return Stream.of(
                Paths.get(ENV_FILE),
                Paths.get("backend", ENV_FILE)
        )
                .filter(Files::exists)
                .findFirst()
                .orElse(null);
    }

}
