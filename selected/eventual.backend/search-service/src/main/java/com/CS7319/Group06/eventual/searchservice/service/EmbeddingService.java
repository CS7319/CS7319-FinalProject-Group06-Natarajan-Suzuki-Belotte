package com.CS7319.Group06.eventual.searchservice.service;

/**
 * Service for generating vector embeddings from text. Used by the search layer to enable semantic/hybrid search.
 */
public interface EmbeddingService {

    /**
     * Generates an embedding vector for the given text.
     *
     * @param text
     * @return float array of embedding dimensions
     * @throws RuntimeException if embedding generation fails
     */
    float[] generateEmbedding(String text);
}
