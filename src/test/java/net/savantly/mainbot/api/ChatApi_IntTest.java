package net.savantly.mainbot.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import net.savantly.mainbot.dom.chatmessage.ChatResponseDto;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatApi_IntTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    public void testInit() {
        var response = rest.postForEntity("/api/chat/start", null, ChatResponseDto.class);
        System.out.println(response);
    }
    
}
