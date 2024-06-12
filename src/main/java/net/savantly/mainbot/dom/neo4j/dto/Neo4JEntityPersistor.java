package net.savantly.mainbot.dom.neo4j.dto;

import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.neo4j.entity.PersonRepository;
import net.savantly.mainbot.dom.neo4j.entity.PlaceRepository;
import net.savantly.mainbot.dom.neo4j.entity.ThingRepository;

@RequiredArgsConstructor
public class Neo4JEntityPersistor {

    private final PersonRepository personRepository;
    private final PlaceRepository placeRepository;
    private final ThingRepository thingRepository;

    public void persist(EntityExtraction entityExtraction) {
        personRepository.saveAll(entityExtraction.getPeople());
        placeRepository.saveAll(entityExtraction.getPlaces());
        thingRepository.saveAll(entityExtraction.getThings());

    }

}
