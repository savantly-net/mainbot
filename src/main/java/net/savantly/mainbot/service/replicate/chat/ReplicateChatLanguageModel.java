package net.savantly.mainbot.service.replicate.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import net.savantly.mainbot.service.replicate.ReplicateClient;
import net.savantly.mainbot.service.replicate.ReplicateRequest;
import net.savantly.mainbot.service.replicate.models.Mistral7bInstructV1;

@RequiredArgsConstructor
public class ReplicateChatLanguageModel implements ChatLanguageModel {

    private final ReplicateClient client;
    private final int backoffMillis = 2000;
    private final int retries = 10;
    private final int maxNewTokens = 300;
    private final int minNewTokens = 10;
    private final float temperature = 0.75f;
    private final float topP = 1;
    private final int topK = 40;
    private final int seed = 0;
    private final boolean debug = false;

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        return generate(messages, List.of());
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification toolSpecification) {
        return generate(messages, List.of(toolSpecification));
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> toolSpecifications) {
        var prompt = ChatMessageToReplicatePrompt.createPrompt(messages);
        var input = new Mistral7bInstructV1.Input()
                .setPrompt(prompt)
                .setTemperature(temperature)
                .setMax_new_tokens(300)
                .setTop_p(topP)
                .setTop_k(topK);
        var request = new ReplicateRequest<Mistral7bInstructV1.Input, ArrayList<String>>(input);
        var response = client.predictions(request, Optional.empty(), backoffMillis, retries);
        var allLines = response.getOutput().stream().reduce("", (a, b) -> a + b);
        var aiMessage = AiMessage.from(allLines);
        return new Response<AiMessage>(aiMessage);
    }

}