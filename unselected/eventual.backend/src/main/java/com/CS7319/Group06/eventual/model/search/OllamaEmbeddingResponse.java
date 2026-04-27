package com.CS7319.Group06.eventual.model.search;

import lombok.Data;

/**
 * Response from the Ollama /api/embeddings endpoint.
 */
@Data
public class OllamaEmbeddingResponse {

    private float[] embedding;
}
