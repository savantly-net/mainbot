package net.savantly.mainbot.dom.chatsession;

import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.chatmessage.AiResponseMessage;
import net.savantly.mainbot.dom.chatmessage.ResponseMessageDocRef;
import net.savantly.mainbot.dom.documents.DocumentQuery;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.dom.userchatsession.UserChatSession;
import net.savantly.mainbot.dom.userchatsessionmemory.UserChatSessionMemories;
import net.savantly.mainbot.service.languagetools.LanguageTools;

@RequiredArgsConstructor
@Slf4j
public class ChatMessageSender {

    private final LanguageTools chat;
    private final UserChatSessionMemories userSessionMemories;
    private final DocumentService documentService;
    

    @Transactional
    public AiResponseMessage sendMessage(String requestId, UserChatSession userChatSession, String userInput) {

        var summarizedMemories = userSessionMemories.getSummarizedMemories(userChatSession.getId());

        var rephrasedQuestion = chat.rephraseQuestion(List.of(summarizedMemories), userInput);

        var query = new DocumentQuery()
            .setText(rephrasedQuestion)
            .setMaxResults(5)
            .setMinScore(0.5);

        var relevantDocs = documentService.search(query, userChatSession.getNamespace());

        var docTexts = relevantDocs.stream().map(d -> d.getText()).toList();

        var aiResponse = chat.answerQuestionWithContext(docTexts, rephrasedQuestion);

        List<ResponseMessageDocRef> docsReferenced = relevantDocs.stream().map(d -> {
            return new ResponseMessageDocRef()
                .setDocId(d.getEmbeddingId())
                .setNamespace(userChatSession.getNamespace())
                .setScore(d.getScore());
        }).toList();

        var aiMessage = new AiResponseMessage()
            .setId(requestId)
            .setAiMessage(aiResponse)
            .setDocRefs(docsReferenced);

        log.debug("AI Message: {}", aiMessage);
        return aiMessage;
    }
}
