package net.savantly.wanderling.service;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.savantly.mainbot.dom.appuser.AppUsers;
import net.savantly.mainbot.dom.chatsession.ChatSession;
import net.savantly.mainbot.dom.userchatsession.UserChatSession;
import net.savantly.mainbot.dom.userchatsession.UserChatSessions;
import net.savantly.mainbot.identity.UserDto;

@ActiveProfiles("local")
@SpringBootTest
public class ChatSession_IntTest {

    private final static Logger log = LoggerFactory.getLogger(ChatSession_IntTest.class);

    @Autowired
    ChatSession gamePlay;
    @Autowired
    AppUsers players;
    @Autowired
    UserChatSessions playerSessions;

    @Test
    public void test() throws InterruptedException {

        var playerId = UUID.randomUUID().toString();
        var playerName = "test-user";

        var userDto = new UserDto().setUid(playerId).setName(playerName);
        players.create(playerId, playerName);

        var playerSession = playerSessions.create(userDto);

        gmLog(waitForResponse(playerSession, "hello"));

        systemLog("last prompt: " + playerSession.getLastPrompt());

    }

    private void gmLog(String message) {
        log.info("GM: {}", message);
    }

    private void playerLog(String message) {
        log.info("Player: {}", message);
    }

    private void systemLog(String message) {
        log.info("System: {}", message);
    }

    private String waitForResponse(UserChatSession playerSession, String message) throws InterruptedException {
        playerLog(message);
        var response = gamePlay.sendMessage(playerSession, message);

        return response.getAiMessage();
    }
}
