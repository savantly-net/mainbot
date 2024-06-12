package net.savantly.mainbot.dom.neo4j.dto;

import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

//** TODO: NOT USED YET */
@Data
@Accessors(chain = true)
public class NodeDTO {
    private String id;
     /**
      * The label of the node
      * Person, Place, Thing, etc.
      */
    private String label;

    /**
     * The name of the node
     * John Doe, New York, etc.
     */
    private String name;

    /**
     * The description of the node
     * A person, a city, etc.
     */
    private String description;

    /**
     * The properties of the node
     * key-value pairs
     */
    private Map<String, Object> properties = new java.util.HashMap<>();
}
