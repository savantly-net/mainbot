package net.savantly.wanderling.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.savantly.mainbot.service.languagetools.LanguageTools;

@ActiveProfiles("local")
@SpringBootTest
public class ChatTest_IntTest {

    @Autowired
    LanguageTools chat;

    @Test
    public void test() throws InterruptedException {
        var given = "I am a human";
        var text = "I am a homosapien";

        var response = chat.isSameMeaning(given, text);
        System.out.println(response);
    }
}
