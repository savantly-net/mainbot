package net.savantly.mainbot.service.languagetools;

import java.util.List;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LanguageTools {


        @SystemMessage("""
You will be provided an excerpt from a script.
Create a summary of the events.
"""
        )
        @UserMessage("{{text}}")
        String summarizeEvents(@V("text") String text);


        @SystemMessage("""
determine if the following text means the same thing as the given text. Write 'true' or 'false'.
Given: {{given}}
""")
        @UserMessage("{{text}}")
        boolean isSameMeaning(@V("given") String given, @V("text") String text);


        @UserMessage("""
Read the context, and answer the following question with true or false.
---  
{{text}}
---  
Question: {{phrase}}
""")
        boolean evaluateConditionPhrase(@V("phrase") String phrase, @V("text") String text);


        @SystemMessage("""
Given the previous messages...
---
{{messages}}
---

Rephrase the following question as a new question with more specificity.
""")
        @UserMessage("""
{{text}}
""")
        String rephraseQuestion(@V("messages") List<String> previousMessages, @V("text") String text);
        


        @SystemMessage("""
The AI is a domain expert.
Given this question: 
{{text}}

Answer the question with the following context.

Context:
{{context}}
""")
        String answerQuestionWithContext(@V("context") List<String> context, @V("text") String text);


        @SystemMessage("{{prompt}}")
        String completion(@V("prompt") String prompt);
                        
}
