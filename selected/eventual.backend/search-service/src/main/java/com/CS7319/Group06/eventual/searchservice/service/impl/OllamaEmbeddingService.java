package com.CS7319.Group06.eventual.searchservice.service.impl;

import com.CS7319.Group06.eventual.searchservice.config.OllamaConfig;
import com.CS7319.Group06.eventual.searchservice.model.OllamaEmbeddingRequest;
import com.CS7319.Group06.eventual.searchservice.model.OllamaEmbeddingResponse;
import com.CS7319.Group06.eventual.searchservice.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Ollama implementation for EmbeddingService
 */
@Slf4j
@Service
public class OllamaEmbeddingService implements EmbeddingService {

    private final RestClient restClient;
    private final OllamaConfig config;

    public OllamaEmbeddingService(OllamaConfig config) {
        this.config = config;
        this.restClient = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public float[] generateEmbedding(String text) {
        log.debug("Generating embedding for text via Ollama (model={})", config.getModel());

        OllamaEmbeddingResponse response = restClient.post()
                .uri("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new OllamaEmbeddingRequest(config.getModel(), text))
                .retrieve()
                .body(OllamaEmbeddingResponse.class);

        if (response == null || response.getEmbedding() == null || response.getEmbedding().length == 0) {
            throw new RuntimeException("Ollama returned an empty embedding for model: " + config.getModel());
        }

        log.debug("Generated embedding with {} dimensions", response.getEmbedding().length);
        return response.getEmbedding();
    }
}
