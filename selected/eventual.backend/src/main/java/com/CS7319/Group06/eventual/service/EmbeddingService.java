package com.CS7319.Group06.eventual.service;

/**
 * Service for generating vector embeddings from text. Used by the search layer to enable semantic/hybrid search.
 *
 * @author harininatarajan
 */
public interface EmbeddingService {

    /**
     * Generates an embedding vector for the given text.
     *
     * @param text
     * @return
     * @throws RuntimeException
     */
    float[] generateEmbedding(String text);
}
