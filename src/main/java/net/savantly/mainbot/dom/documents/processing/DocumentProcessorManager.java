package net.savantly.mainbot.dom.documents.processing;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentPartCollection;

@Slf4j
public class DocumentProcessorManager {

    private final List<DocumentProcessor> processors;

    public DocumentProcessorManager(List<DocumentProcessor> processors) {
        processors.sort((a, b) -> a.getPriority() - b.getPriority());
        log.info("Document processors loaded: {}", processors.size());
        for (DocumentProcessor processor : processors) {
            log.info("Document processor loaded: {}", processor.getClass().getSimpleName());
        }
        this.processors = processors;
    }

    public DocumentPartCollection processDocument(DocumentPartCollection document) {
        DocumentPartCollection processed = document;
        for (DocumentProcessor processor : processors) {
            processed = processor.processDocument(processed);
        }
        return processed;
    }

}
