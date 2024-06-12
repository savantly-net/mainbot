package net.savantly.mainbot.dom.neo4j;

import net.savantly.mainbot.dom.neo4j.dto.EntityExtraction;
import net.savantly.mainbot.dom.neo4j.entity.PersonNode;
import net.savantly.mainbot.dom.neo4j.entity.PlaceNode;
import net.savantly.mainbot.dom.neo4j.entity.ThingNode;

public interface Neo4JEntityExtractor {

    default EntityExtraction extractEntities(String text) {

        var person1 = new PersonNode().setName("John Doe");
        var person2 = new PersonNode().setName("Jane Doe");

        person1.getKnows().add(person2);

        var place1 = new PlaceNode().setName("New York");
        var place2 = new PlaceNode().setName("Los Angeles");

        person1.setLivesIn(place1);
        person2.setLivesIn(place2);

        person1.setHailsFrom(place2);

        var car = new ThingNode().setName("Car");
        var house = new ThingNode().setName("House");
        person1.setHas(car);
        person2.setHas(house);

        var extracted = new EntityExtraction();
        extracted.setPeople(java.util.List.of(person1, person2));
        extracted.setPlaces(java.util.List.of(place1, place2));
        extracted.setThings(java.util.List.of(car, house));
        return extracted;
    };
}
