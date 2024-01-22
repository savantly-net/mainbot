package net.savantly.mainbot.service.replicate.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.service.replicate.ReplicateVersionHolder;

public class PlaygroundV2Aesthetic {

    static final String VERSION = "42fe626e41cc811eaf02c94b892774839268ce1994ea778eba97103fe1ef51b8";

    static enum Scheduler {
        DDIM, DPMSolverMultistep, HeunDiscrete, K_EULER_ANCESTRAL, K_EULER, PNDM
    }

    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Input implements ReplicateVersionHolder {

        @Override
        public String getVersion() {
            return VERSION;
        }

        private String prompt;
        private String negative_prompt;
        private int width = 1024;
        private int height = 1024;
        private int num_outputs = 1;
        private Scheduler scheduler = Scheduler.K_EULER;
        private int num_inference_steps = 50;
        private float guidance_scale = 3;
        private Integer seed;
        private boolean apply_watermark;

    }

    public static class Output extends ArrayList<String> {
    }
}
