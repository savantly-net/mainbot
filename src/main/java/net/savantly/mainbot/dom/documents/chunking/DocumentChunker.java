package net.savantly.mainbot.dom.documents.chunking;

import java.util.List;
import java.util.Objects;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentPart;
import net.savantly.mainbot.dom.documents.DocumentPartCollection;
import net.savantly.mainbot.dom.documents.processing.DocumentProcessor;

@Slf4j
public class DocumentChunker implements DocumentProcessor {

    final private ChunkingConfiguration config;
    final private DocumentSplitter splitter;

    public DocumentChunker(ChunkingConfiguration config) {
        this.config = config;
        this.splitter = DocumentSplitters.recursive(
                config.getMaxChunkSize(),
                config.getChunkOverlap());
    }

    public int getPriority() {
        return 0;
    }

    public DocumentPartCollection processDocument(DocumentPartCollection document) {
        if (!config.isEnabled()) {
            log.info("Document chunking is disabled");
            return document;
        }

        if (document.hasParts() || document.isChunked()) {
            log.info("Document already chunked. parts: {}", document.getParts().size());
            return document;
        }

        if (Objects.isNull(document.getText()) || document.getText().isEmpty()) {
            log.info("Document text is empty");
            return document;
        }

        return chunkDocument(document);

    }

    private DocumentPartCollection chunkDocument(DocumentPartCollection document) {
        log.info("Chunking document: {}", document.getId());
        List<TextSegment> segments = splitter.split(Document.from(document.getText()));
        log.info("Document chunked into {} parts", segments.size());
        for (var i = 0; i < segments.size(); i++) {
            final DocumentPart part = new DocumentPart()
                    .setParentId(document.getId())
                    .setUri(document.getUri())
                    .setChunkIndex(i)
                    .setMetadata(segments.get(i).metadata().asMap())
                    .setText(segments.get(i).text());
            document.addPart(part);
        }
        return document;
    }

}
