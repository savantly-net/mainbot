package net.savantly.mainbot.dom.embeddingstore;

import dev.langchain4j.data.embedding.Embedding;

public interface HasEmbedding {
    
    Embedding getEmbedding();
    void setEmbedding(Embedding embeddings);
}
