package net.savantly.mainbot.dom.documents;

import java.util.List;

public interface DocumentService {

    public List<String> addDocument(DocumentAddRequest document);

    public List<DocumentSearchResult> search(DocumentQuery query, String namespace);
}
