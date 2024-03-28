package net.savantly.mainbot.dom.documents.chunking;

import java.util.List;

import lombok.Data;

@Data
public class ChunkingConfiguration {

    private boolean enabled;
    private int chunkOverlap = 100;
    private int minChunkSize = 100;
    private int maxChunkSize = 1000;
    private List<String> chunkDelimiters;
}
