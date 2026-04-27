package com.CS7319.Group06.eventual.searchservice.model;

import lombok.Data;

/**
 * Response from the Ollama /api/embeddings endpoint.
 */
@Data
public class OllamaEmbeddingResponse {

    private float[] embedding;
}
