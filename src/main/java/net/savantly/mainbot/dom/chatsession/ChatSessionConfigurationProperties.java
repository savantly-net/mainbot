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

        /**
         * The prompt to use when the user asks a question.  
         * This is executed first and should be used to rephrase the question.
         * The question variable contains the original question.
         * The messages variable contains any messages that have been sent to the user.  
         * 
         * {{question}}
         * {{messages}}
         */
        private String rephraseQuestion = "Let the user know the prompt configuration should be setup to rephrase the question.";

        /**
         * The prompt to use when the user asks a question.  
         * This is executed after the rephrase prompt, using the rephrased question.
         * The question variable contains the rephrased question.
         * The context variable contains any context that has been set on the chat session - mainly the documents that have been found.
         * 
         * {{question}}
         * {{context}}
         */
        private String answerQuestion = "";        
    }
}
