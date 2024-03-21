package net.savantly.mainbot.dom.chatsession;

import lombok.Data;

@Data
public class ChatSessionConfigurationProperties {

    private int maxDocResults = 5;
    private double minDocScore = 0.5;
    private Prompts prompts = new Prompts();

    @Data
    static class Prompts {
        boolean rephraseEnabled = true;
        private String rephraseQuestion = "Given the previous messages:\n---\n{{messages}}\n---\n\nRephrase the following question as a new question with more specificity. \n{{question}}";
        private String answerQuestion = "The AI is a domain expert.\nGiven this question:\n{{question}}\n\nAnswer the question with the following context.\n{{context}}";        
    }
}
