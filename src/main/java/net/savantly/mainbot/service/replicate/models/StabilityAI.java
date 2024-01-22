package net.savantly.mainbot.service.replicate.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.service.replicate.ReplicateVersionHolder;

public class StabilityAI {

    static final String VERSION = "ac732df83cea7fff18b8472768c88ad041fa750ff7682a21affe81863cbe77e4";
    
    static enum Scheduler {
        DDIM, K_EULER, DPMSolverMultistep, K_EULER_ANCESTRAL, PNDM, KLMS
    }

    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Input  implements ReplicateVersionHolder {
        
        @Override
        public String getVersion() {
            return VERSION;
        }
        
        private String prompt;
        private String negative_prompt;
        private int width = 1024;
        private int height = 1024;
        private int num_outputs = 1;
        private Scheduler scheduler = Scheduler.DPMSolverMultistep;
        private int num_inference_steps = 50;
        private Integer seed;
        private float guidance_scale = 7.5f;
        private float prompt_strength = 0.8f;

    }

    public static class Output extends ArrayList<String> {
    }
}
