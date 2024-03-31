package net.savantly.mainbot.dom.documents;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocumentPart {

    private String id;
    private String parentId;
    private String uri;
    private String text;
    private int chunkIndex;
    private float[] embedding;
    private Map<String, String> metadata = new HashMap<>();

    public String getId() {
        if (this.id == null) {
            this.id = this.uri + "#" + this.chunkIndex;
        }
        return this.id;
    }

}
