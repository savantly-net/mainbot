package net.savantly.mainbot.dom.opensearch;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.opensearch.client.json.JsonpDeserializer;
import org.opensearch.client.json.JsonpDeserializerBase;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.jackson.JacksonJsonpParser;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.ErrorResponse;
import org.opensearch.client.transport.JsonEndpoint;
import org.opensearch.client.transport.endpoints.SimpleEndpoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.JsonException;
import jakarta.json.stream.JsonGenerationException;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.dom.opensearch.ml.ModelGroup;


// some of the code in this class is copied from
// https://github.com/dlvenable/data-prepper/blob/do-not-merge-opensearch-java-client-generic-poc/data-prepper-plugins/opensearch/src/test/java/org/opensearch/dataprepper/plugins/sink/opensearch/GeneralRequestDemoTest.java
@RequiredArgsConstructor
public class OpenSearchRawClient {

    private final OpenSearchClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> Map<String, Object> runScript(String method, String path, Map<String, String> queryParameters,
            Optional<T> body) throws IOException {

        var restClient = client._transport();

        final JsonpDeserializer<Map> deserializer = new JacksonValueParser<>(Map.class);

        JsonEndpoint<T, Map, ErrorResponse> endpoint = new SimpleEndpoint<>(
                m -> method,
                m -> path,
                m -> queryParameters,
                m -> Collections.emptyMap(),
                body.isPresent(),
                deserializer);

        final Map<String, Object> response = restClient.performRequest(body.orElse(null), endpoint,
                client._transportOptions());
        return response;

    }

    public Map<String, Object> searchModelByName(String name) throws IOException {
        var path = "_plugins/_ml/models/_search";
        var queryParameters = new HashMap<String, String>();
        var bodyString = "{\n" + //
                        "  \"query\": {\n" + //
                        "    \"bool\": {\n" + //
                        "      \"should\": [\n" + //
                        "        {\n" + //
                        "          \"match\": {\n" + //
                        "            \"name\": \"" + name +"\"\n" + //
                        "          }\n" + //
                        "        }\n" + //
                        "      ],\n" + //
                        "      \"must_not\": {\n" + //
                        "        \"exists\": {\n" + //
                        "          \"field\": \"chunk_number\"\n" + //
                        "        }\n" + //
                        "      }\n" + //
                        "    }\n" + //
                        "  }\n" + //
                        "}";
        var serialized = objectMapper.readValue(bodyString, Map.class);
        var body = Optional.of(serialized);
        return this.runScript("GET", path, queryParameters, Optional.of(body));
    }

    public Map<String, Object> registerModel(String modelRegistrationJson) throws IOException {
        var serialized = objectMapper.readValue(modelRegistrationJson, Map.class);
        var body = Optional.of(serialized);
        var path = "_plugins/_ml/models/_register";
        var queryParameters = new HashMap<String, String>();
        return this.runScript("POST", path, queryParameters, body);
    }

    public Map<String, Object> createModelGroup(ModelGroup modelGroup) throws IOException {
        var path = "_plugins/_ml/model_groups/_register";
        var queryParameters = new HashMap<String, String>();
        var body = Optional.of(modelGroup);
        return this.runScript("POST", path, queryParameters, body);
    }

    public Map<String, Object> getMlTaskStatus(String taskId) throws IOException {
        var path = "_plugins/_ml/tasks/" + taskId;
        var queryParameters = new HashMap<String, String>();
        return this.runScript("GET", path, queryParameters, Optional.empty());
    }

    /**
     * Copied from opensearch-java because it is package protected. I did add the
     * ObjectMapper
     * and copied over the convertException() method which was in JacksonUtils and
     * package protected.
     * 
     * @param <T>
     */
    private static class JacksonValueParser<T> extends JsonpDeserializerBase<T> {

        private final ObjectMapper objectMapper = new ObjectMapper();
        private final Class<T> clazz;

        protected JacksonValueParser(Class<T> clazz) {
            super(EnumSet.allOf(JsonParser.Event.class));
            this.clazz = clazz;
        }

        @Override
        public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {

            if (!(parser instanceof JacksonJsonpParser)) {
                throw new IllegalArgumentException(
                        "Jackson's ObjectMapper can only be used with the JacksonJsonpProvider");
            }

            com.fasterxml.jackson.core.JsonParser jkParser = ((JacksonJsonpParser) parser).jacksonParser();

            try {
                return objectMapper.readValue(jkParser, clazz);
            } catch (IOException ioe) {
                throw convertException(ioe);
            }
        }

        public static JsonException convertException(IOException ioe) {
            if (ioe instanceof com.fasterxml.jackson.core.JsonGenerationException) {
                return new JsonGenerationException(ioe.getMessage(), ioe);

            } else if (ioe instanceof com.fasterxml.jackson.core.JsonParseException) {
                JsonParseException jpe = (JsonParseException) ioe;
                return new JsonParsingException(ioe.getMessage(), jpe, new JacksonJsonpLocation(jpe.getLocation()));

            } else {
                return new JsonException("Jackson exception", ioe);
            }
        }
    }

    /**
     * Copied from opensearch-java because the constructor is package-protected.
     */
    private static class JacksonJsonpLocation implements JsonLocation {

        private final com.fasterxml.jackson.core.JsonLocation location;

        public JacksonJsonpLocation(com.fasterxml.jackson.core.JsonLocation location) {
            this.location = location;
        }

        JacksonJsonpLocation(com.fasterxml.jackson.core.JsonParser parser) {
            this(parser.getTokenLocation());
        }

        @Override
        public long getLineNumber() {
            return location.getLineNr();
        }

        @Override
        public long getColumnNumber() {
            return location.getColumnNr();
        }

        @Override
        public long getStreamOffset() {
            long charOffset = location.getCharOffset();
            return charOffset == -1 ? location.getByteOffset() : charOffset;
        }
    }

}
