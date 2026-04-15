package com.CS7319.Group06.eventual.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Ollama embedding model server.
 * Ollama runs as a Docker container defined in /elasticsearch/docker-compose.yml.
 *
 * @author harininatarajan
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {

    private String host;
    private int port;
    private String model;

    public String getBaseUrl() {
        return "http://" + host + ":" + port;
    }
}
