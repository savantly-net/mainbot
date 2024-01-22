package net.savantly.mainbot.service.languagetools;

import java.util.List;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.output.Response;

public class MockChatLanguageModel implements LanguageToolModel {

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        return new Response<AiMessage>(new AiMessage("mock"));
    }

}
