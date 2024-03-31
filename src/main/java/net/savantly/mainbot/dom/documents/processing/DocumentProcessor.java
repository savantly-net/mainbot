package net.savantly.mainbot.dom.documents.processing;

import net.savantly.mainbot.dom.documents.DocumentPartCollection;

public interface DocumentProcessor {

    int getPriority();

    DocumentPartCollection processDocument(DocumentPartCollection document);
}
