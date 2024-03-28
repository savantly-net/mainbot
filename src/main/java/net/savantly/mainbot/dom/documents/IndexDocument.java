package net.savantly.mainbot.dom.documents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IndexDocument {
    
    private String id;
    private String namespace;
    private String uri;
    private String text;
    private boolean chunked;
    private Set<DocumentPart> parts = new HashSet<>();
    private Map<String, String> metadata = new HashMap<>();

    public IndexDocument addPart(DocumentPart part) {
        this.parts.add(part);
        return this;
    }

    public boolean hasParts() {
        return !this.parts.isEmpty();
    }

    static public IndexDocument fromAddRequest(DocumentAddRequest request) {
        return new IndexDocument()
                .setId(request.getId())
                .setNamespace(request.getNamespace())
                .setUri(request.getUri())
                .setText(request.getText())
                .setChunked(request.isChunk())
                .setParts(new HashSet<>())
                .setMetadata(request.getMetadata());
    }
}
