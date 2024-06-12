package net.savantly.mainbot.dom.neo4j.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.experimental.Accessors;

@Node("Thing")
@Data
@Accessors(chain = true)
public class ThingNode {
    
    @Id
    private String name;
    private String description;

    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    private ThingNode has;

    @Relationship(type = "IS_A", direction = Relationship.Direction.OUTGOING)
    private ThingNode isA;

    @Relationship(type = "PART_OF", direction = Relationship.Direction.OUTGOING)
    private ThingNode partOf;
}
