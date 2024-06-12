package net.savantly.mainbot.dom.neo4j.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Relationship {
    
    /**
     * The start node of the relationship
     */
    private NodeDTO startNode;

    /**
     * The end node of the relationship
     */
    private NodeDTO endNode;

    /**
     * The type of the relationship
     * KNOWS, LIVES_IN, etc.
     */
    private String type;
}
