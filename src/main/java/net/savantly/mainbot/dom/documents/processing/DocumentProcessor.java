package net.savantly.mainbot.dom.documents.processing;

import net.savantly.mainbot.dom.documents.IndexDocument;

public interface DocumentProcessor {

    int getPriority();

    IndexDocument processDocument(IndexDocument document);
}
