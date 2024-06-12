package net.savantly.mainbot.dom.neo4j;

import java.util.List;

import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.documents.DocumentSearchResult;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.documents.impl.LC4JDocumentService;
import net.savantly.mainbot.dom.neo4j.dto.Neo4JEntityPersistor;

public class Neo4JDocumentService implements DocumentService {

    private final Neo4JEntityExtractor entityExtractor;
    private final Neo4JEntityPersistor entityPersistor;
    private final LC4JDocumentService lc4jDocumentService;

    public Neo4JDocumentService(Neo4JEntityExtractor entityExtractor, Neo4JEntityPersistor entityPersistor, LC4JDocumentService lc4jDocumentService) {
        this.entityExtractor = entityExtractor;
        this.entityPersistor = entityPersistor;
        this.lc4jDocumentService = lc4jDocumentService;
    }

    @Override
    public List<String> addDocument(DocumentAddRequest document) {
        var extractedEntities = entityExtractor.extractEntities(document.getText());
        entityPersistor.persist(extractedEntities);
        return lc4jDocumentService.addDocument(document);
    }

    @Override
    public List<DocumentSearchResult> search(DocumentQuery query, String namespace) {
        return lc4jDocumentService.search(query, namespace);
    }

}
