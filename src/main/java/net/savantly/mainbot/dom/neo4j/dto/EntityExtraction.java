package net.savantly.mainbot.dom.neo4j.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import net.savantly.mainbot.dom.neo4j.entity.PersonNode;
import net.savantly.mainbot.dom.neo4j.entity.PlaceNode;
import net.savantly.mainbot.dom.neo4j.entity.ThingNode;

@Data
public class EntityExtraction {
    
    private List<PersonNode> people = new ArrayList<>();
    private List<PlaceNode> places = new ArrayList<>();
    private List<ThingNode> things = new ArrayList<>();

}
