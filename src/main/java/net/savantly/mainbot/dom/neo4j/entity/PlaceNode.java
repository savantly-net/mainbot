package net.savantly.mainbot.dom.neo4j.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;
import lombok.experimental.Accessors;

@Node("Place")
@Data
@Accessors(chain = true)
public class PlaceNode {
    
    @Id
    private String name;
    private String description;
    private Float latitude;
    private Float longitude;
}
