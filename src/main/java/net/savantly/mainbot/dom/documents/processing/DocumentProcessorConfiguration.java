package net.savantly.mainbot.dom.documents.processing;

import lombok.Data;

@Data
public class DocumentProcessorConfiguration {

    private Chunking chunking = new Chunking();

    @Data
    public static class Chunking {
        private boolean enabled;
        private int chunkOverlap = 100;
        private int maxChunkSize = 1000;
    }

}
