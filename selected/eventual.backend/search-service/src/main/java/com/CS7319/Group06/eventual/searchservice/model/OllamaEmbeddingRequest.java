package com.CS7319.Group06.eventual.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Request body sent to the Ollama /api/embeddings endpoint.
 */
@Data
@AllArgsConstructor
public class OllamaEmbeddingRequest {

    private String model;

    private String prompt;
}
