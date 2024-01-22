package net.savantly.mainbot.service.replicate.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.service.replicate.ReplicateVersionHolder;

public class CodeLlama34bInstructGGUF {
    
    static final String VERSION = "f1091fa795c142a018268b193c9eea729e0a3f4d55d723df0b69f17b863bf5ea";

    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Input  implements ReplicateVersionHolder {
        
        @Override
        public String getVersion() {
            return VERSION;
        }
        
        private String prompt;

        /**
         * Grammar in GBNF format. Use either grammar or jsonschema.
         */
        private String grammar;

        /**
         * JSON schema for the generated output. Use either grammar or jsonschema. 
         *  You can use the jsonschema in the prompt by using the special string '{jsonschema}'
         */
        private String jsonschema;

        private int max_tokens = 500;
        private float temperature;
        private float top_p;
        private int top_k;
        private float frequency_penalty;
        private float presence_penalty;
        private float repeat_penalty = 1.1f;
        private String mirostat_mode = "Disabled";
        private float mirostat_learning_rate = 0.1f;
        private float mirostat_entropy = 5.0f;
    }

    public static class Output extends ArrayList<String> {
    }
}
