package net.savantly.mainbot.service.replicate.chat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import dev.ai4j.openai4j.chat.Function;
import dev.langchain4j.agent.tool.ToolParameters;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;

public class ChatMessageToReplicatePrompt {

    static String createPrompt(List<ChatMessage> messages) {
        return createPrompt(messages, List.of());
    }

    static String createPrompt(List<ChatMessage> messages, List<ToolSpecification> toolSpecifications) {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage message : messages) {
            sb.append(message.type());
            sb.append(": ");
            sb.append(message.text());
            sb.append("\n\n\n");
        }
        for (var f : toFunctions(toolSpecifications)) {
            sb.append(f.toString());
            sb.append("\n\n");
        }
        return sb.toString();
    }


    private static List<Function> toFunctions(Collection<ToolSpecification> toolSpecifications) {
        return toolSpecifications.stream()
                .map(ChatMessageToReplicatePrompt::toFunction)
                .collect(Collectors.toList());
    }

    private static Function toFunction(ToolSpecification toolSpecification) {
        return Function.builder()
                .name(toolSpecification.name())
                .description(toolSpecification.description())
                .parameters(toOpenAiParameters(toolSpecification.parameters()))
                .build();
    }

    private static dev.ai4j.openai4j.chat.Parameters toOpenAiParameters(ToolParameters toolParameters) {
        if (toolParameters == null) {
            return dev.ai4j.openai4j.chat.Parameters.builder().build();
        }
        return dev.ai4j.openai4j.chat.Parameters.builder()
                .properties(toolParameters.properties())
                .required(toolParameters.required())
                .build();
    }
    
}
