package net.savantly.mainbot.dom.opensearch.ml;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModelGroup {

    private String name;
    private String description;
    @JsonProperty("access_mode")
    private AccessMode accessMode;
    @JsonProperty("backend_roles")
    private List<String> backendRoles = new ArrayList<>();

    public enum AccessMode {
        PUBLIC("public"),
        PRIVATE("private"),
        RESTRICTED("restricted");

        private final String value;

        AccessMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static AccessMode fromValue(String value) {
            for (AccessMode mode : values()) {
                if (mode.value.equals(value)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("Invalid access mode: " + value);
        }
    }
}
