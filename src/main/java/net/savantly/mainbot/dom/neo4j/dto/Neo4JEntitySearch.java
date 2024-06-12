package net.savantly.mainbot.dom.neo4j.dto;

import java.util.List;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.neo4j.dto.query.Neo4JQueryGenerator;
import net.savantly.mainbot.dom.neo4j.entity.PersonRepository;
import net.savantly.mainbot.dom.neo4j.entity.PlaceRepository;
import net.savantly.mainbot.dom.neo4j.entity.ThingRepository;

@RequiredArgsConstructor
public class Neo4JEntitySearch {

    private final Neo4JQueryGenerator queryGenerator;
    private final PersonRepository personRepository;
    private final PlaceRepository placeRepository;
    private final ThingRepository thingRepository;

    public List<NodeDTO> search(DocumentQuery query, String namespace) {

        var nodeQuery = queryGenerator.generateQuery(query.getText());
        
        return null;
    }
}
