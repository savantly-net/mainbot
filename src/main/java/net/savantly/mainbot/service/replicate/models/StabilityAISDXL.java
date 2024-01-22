package net.savantly.mainbot.service.replicate.models;

import java.util.ArrayList;

import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;
import net.savantly.mainbot.service.replicate.ReplicateVersionHolder;

public class StabilityAISDXL {
    
    static final String VERSION = "39ed52f2a78e934b3ba6e2a89f5b1c712de7dfea535525255b1aa35c5565e08b";

    static enum Scheduler {
        DDIM, DPMSolverMultistep, HeunDiscrete, KarrasDPM, K_EULER_ANCESTRAL, K_EULER, PNDM
    }

    static enum Refiner {
        no_refiner, expert_ensemble_refiner, base_image_refiner
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
        private String image;
        private String mask;
        private int width = 1024;
        private int height = 1024;
        private int num_outputs = 1;
        private Scheduler scheduler = Scheduler.K_EULER;
        private int num_inference_steps = 25;
        private float guidance_scale = 7.5f;
        private float prompt_strength = 0.8f;
        private Integer seed;
        private Refiner refine = Refiner.expert_ensemble_refiner;
        private float high_noise_frac = 0.8f;
        private Integer refine_steps;
        private boolean apply_watermark;
        private float lora_scale = 0.6f;

    }

    public static class Output extends ArrayList<String> {
    }
}
