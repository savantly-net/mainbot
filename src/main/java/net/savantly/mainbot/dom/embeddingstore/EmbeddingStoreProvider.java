package net.savantly.mainbot.dom.embeddingstore;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.config.PineconeConfig;

@Service
@RequiredArgsConstructor
public class EmbeddingStoreProvider {

    final private PineconeConfig pineconeConfig;

    final private HashMap<String, EmbeddingStore<TextSegment>> stores = new HashMap<>();

    /**
     * Returns a PineconeEmbeddingStore for the given gameId. If one does not exist,
     * it will be created.
     * 
     * @param id
     * @return
     */
    public EmbeddingStore<TextSegment> embeddingStore(String id) {
        if (!stores.containsKey(id)) {
            stores.put(id, create(id));
        }
        return stores.get(id);
    }

    private EmbeddingStore<TextSegment> create(String id) {

        if (!pineconeConfig.isEnabled()) {
            return new InMemoryEmbeddingStore<>();
        }

        PineconeEmbeddingStore pinecone = PineconeEmbeddingStore.builder()
                .apiKey(pineconeConfig.getApiKey()) // https://app.pinecone.io/organizations/xxx/projects/yyy:zzz/keys
                .environment(pineconeConfig.getEnvironment())
                .projectId(pineconeConfig.getProjectName())
                .nameSpace(convertIdToPineconeNamespace(id))
                .index(pineconeConfig.getIndex()) // make sure the dimensions of the Pinecone index match the dimensions
                                                  // of the
                // embedding model (1536 for text-embedding-ada-002)
                .build();
        return pinecone;
    }

    private String convertIdToPineconeNamespace(String id) {
        return pineconeConfig.getNamespacePrefix() + id;
    }
}
