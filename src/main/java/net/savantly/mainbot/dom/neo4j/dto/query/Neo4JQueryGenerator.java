package net.savantly.mainbot.dom.neo4j.dto.query;

import dev.langchain4j.service.SystemMessage;

public interface Neo4JQueryGenerator {

    @SystemMessage("Generate a Cypher query from the given schema and context, for Neo4J database.")
    public String generateQuery(String text);

}
