package net.savantly.mainbot.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentAdd;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.identity.UserContext;
import net.savantly.mainbot.identity.UserDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentApi {

    private final DocumentService documentService;
    private final UserContext userContext;


    @PostMapping("/add")
    public ResponseEntity<List<String>> addDocument(@RequestBody DocumentAdd document) {

        var currentUser = userContext.getCurrentUser().orElseThrow();
        if (isUnauthorized(currentUser, document)) {
            return ResponseEntity.status(403).build();
        }

        var doc = Document.from(document.getText(), Metadata.from(document.getMetadata()));
        return ResponseEntity.ok(documentService.addDocument(doc, document.getNamespace()));
    }


    private boolean isUnauthorized(UserDto currentUser, DocumentAdd document) {
        log.info("checking authorization for user: {}", currentUser);
        if (currentUser.getGroups().contains("admin")) {
            return false;
        }
        if (currentUser.getGroups().contains(document.getNamespace())) {
            return false;
        }
        return true;
    }

    
}
