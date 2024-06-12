package net.savantly.mainbot.dom.neo4j.entity;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PlaceRepository extends Neo4jRepository<PlaceNode, String>{
    
}
