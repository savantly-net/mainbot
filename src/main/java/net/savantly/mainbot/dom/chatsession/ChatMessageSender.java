package net.savantly.mainbot.dom.chatsession;

import java.util.HashMap;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.chatmessage.AiResponseMessage;
import net.savantly.mainbot.dom.chatmessage.ResponseMessageDocRef;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.promptTemplate.PromptParser;
import net.savantly.mainbot.dom.userchatsession.UserChatSession;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;
import net.savantly.mainbot.service.languagetools.LanguageTools;

@RequiredArgsConstructor
@Slf4j
public class ChatMessageSender {

    private final ChatSessionConfigurationProperties config;
    private final LanguageTools chat;
    private final UserChatSessionMemories userSessionMemories;
    private final DocumentService documentService;

    private PromptParser promptParser = new PromptParser();

    @Transactional
    public AiResponseMessage sendMessage(String requestId, UserChatSession userChatSession, String userInput) {

        var rephrasedQuestion = rephraseQuestion(userChatSession, userInput);

        var query = new DocumentQuery()
                .setText(rephrasedQuestion)
                .setMaxResults(config.getMaxDocResults())
                .setMinScore(config.getMinDocScore());

        var relevantDocs = documentService.search(query, userChatSession.getNamespace());
        var docTexts = relevantDocs.stream().map(d -> d.getText()).toList();

        var aiResponse = answerQuestion(docTexts, rephrasedQuestion);

        List<ResponseMessageDocRef> docsReferenced = relevantDocs.stream().map(d -> {
            return new ResponseMessageDocRef()
                    .setDocId(d.getEmbeddingId())
                    .setNamespace(userChatSession.getNamespace())
                    .setMetadata(d.getMetadata())
                    .setScore(d.getScore());
        }).toList();

        var aiMessage = new AiResponseMessage()
                .setId(requestId)
                .setAiMessage(aiResponse)
                .setDocRefs(docsReferenced);

        log.debug("AI Message: {}", aiMessage);
        return aiMessage;
    }

    private String rephraseQuestion(UserChatSession userChatSession, String userInput) {
        var memories = userSessionMemories.getSummarizedMemories(userChatSession.getId());

        if (memories.isEmpty() || !config.getPrompts().isRephraseEnabled()) {
            return userInput;
        }

        var templateValues = new HashMap<String, Object>();
        templateValues.put("messages", memories);
        templateValues.put("question", userInput);

        var prompt = promptParser.parse(config.getPrompts().getRephraseQuestion(), templateValues);

        return chat.completion(prompt);
    }

    private String answerQuestion(List<String> docTexts, String rephrasedQuestion) {
        var templateValues = new HashMap<String, Object>();
        templateValues.put("question", rephrasedQuestion);
        templateValues.put("context", docTexts);

        var prompt = promptParser.parse(config.getPrompts().getAnswerQuestion(), templateValues);

        return chat.completion(prompt);
    }
}
