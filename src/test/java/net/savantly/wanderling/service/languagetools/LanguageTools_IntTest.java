package net.savantly.wanderling.service.languagetools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.service.languagetools.LanguageTools;

@ActiveProfiles("local")
@SpringBootTest
@Slf4j
public class LanguageTools_IntTest {
    
    @Autowired
    LanguageTools languageTools;

    @Test
    public void testCreateImagePrompt() {
        //
    }
}
