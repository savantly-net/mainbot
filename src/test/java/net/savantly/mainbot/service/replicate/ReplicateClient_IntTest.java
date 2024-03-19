package net.savantly.mainbot.service.replicate;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.savantly.mainbot.service.replicate.ReplicateClient;
import net.savantly.mainbot.service.replicate.ReplicateRequest;
import net.savantly.mainbot.service.replicate.models.CodeLlama34bInstructGGUF;
import net.savantly.mainbot.service.replicate.models.Mistral7bInstructV1;
import net.savantly.mainbot.service.replicate.models.StabilityAISDXL;

@ActiveProfiles("local")
@SpringBootTest
public class ReplicateClient_IntTest {
    
    private final static Logger log = LoggerFactory.getLogger(ReplicateClient_IntTest.class);

    @Autowired
    ReplicateClient client;

    private static final String testPrompt = """
        A computer programmer
""";

    @Test
    public void testMistral() {
        var input = new Mistral7bInstructV1.Input();
        input.setDebug(true);
        input.setMax_new_tokens(300);
        input.setMin_new_tokens(30);
        input.setPrompt(testPrompt);
        input.setSeed(42);
        input.setStop_sequences("###");
        input.setTemperature(1);
        input.setTop_k(40);
        input.setTop_p(1);

        var request = new ReplicateRequest<>(input);

        var response = client.predictions(request);
        log.info("response: {}", response);
    }

    @Test
    public void testCodeLamma() {
        var input = new CodeLlama34bInstructGGUF.Input();
        input.setPrompt(testPrompt);
        input.setMax_tokens(300);
        input.setTemperature(1);
        input.setTop_k(40);
        input.setTop_p(1);

        var request = new ReplicateRequest<CodeLlama34bInstructGGUF.Input, CodeLlama34bInstructGGUF.Output>(input);

        var response = client.predictions(request, Optional.empty(), 2000, 25);
        log.info("response: {}", response);
    }


    @Test
    public void testStabilityAi() {
        var input = new StabilityAISDXL.Input();
        input.setPrompt("A computer programmer");
        input.setSeed(42);

        var request = new ReplicateRequest<StabilityAISDXL.Input, StabilityAISDXL.Output>(input);

        var response = client.predictions(request, Optional.empty(), 1500, 25);
        log.info("response: {}", response);
    }
}
