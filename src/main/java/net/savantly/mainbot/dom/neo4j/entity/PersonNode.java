package net.savantly.mainbot.dom.neo4j.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.experimental.Accessors;

@Node("Person")
@Data
@Accessors(chain = true)
public class PersonNode {
    
    @Id
    private String name;
    private String description;

    @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING)
    private List<PersonNode> knows = new ArrayList<>();

    @Relationship(type = "LIVES_IN", direction = Relationship.Direction.OUTGOING)
    private PlaceNode livesIn;

    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    private ThingNode has;

    @Relationship(type = "IS_A", direction = Relationship.Direction.OUTGOING)
    private ThingNode isA;

    @Relationship(type = "HAILS_FROM", direction = Relationship.Direction.OUTGOING)
    private PlaceNode hailsFrom;
}
