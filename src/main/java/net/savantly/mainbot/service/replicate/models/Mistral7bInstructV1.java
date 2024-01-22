package net.savantly.mainbot.service.replicate.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.service.replicate.ReplicateVersionHolder;

public class Mistral7bInstructV1{

    static final String VERSION = "83b6a56e7c828e667f21fd596c338fd4f0039b46bcfa18d973e8e70e455fda70";

    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Input  implements ReplicateVersionHolder {
        
        @Override
        public String getVersion() {
            return VERSION;
        }
        
        private String prompt;
        private int max_new_tokens;
        private int min_new_tokens;
        private float temperature;
        private float top_p;
        private int top_k;
        private String stop_sequences;
        private int seed;
        private boolean debug;
    }

    public static class Output extends ArrayList<String> {
    }
}
