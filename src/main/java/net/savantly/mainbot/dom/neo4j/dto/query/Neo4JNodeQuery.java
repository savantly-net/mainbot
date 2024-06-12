package net.savantly.mainbot.dom.neo4j.dto.query;

import lombok.Data;

@Data
public class Neo4JNodeQuery {
    
    /**
     * The label of the node (Person, Place, etc)
     */
    private String label;

    /**
     * The key to search using the value
     */
    private String key;

    /**
     * The value to search for
     */
    private String value;
}
