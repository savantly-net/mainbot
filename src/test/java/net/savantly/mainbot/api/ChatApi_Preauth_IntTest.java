package net.savantly.mainbot.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;

import net.savantly.mainbot.dom.userchatsession.UserChatSessionDto;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "app.security.enabled=true",
        "app.security.preauth.enabled=true",
        "pinecone.enabled=false",
})
public class ChatApi_Preauth_IntTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    public void testInit() {
        var headers = new HttpHeaders();
        headers.add("x-forwarded-user", "test-user");
        headers.add("x-forwarded-preferred-username", "test");
        headers.add("Accept", "application/json");

        var url = "/api/chat/start";
        var request = RequestEntity.post(url).headers(headers).build();
        var response = rest.exchange(request, UserChatSessionDto.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        var body = response.getBody();
        assertThat(body.getUser().getUid()).isEqualTo("test-user");
    }
}
