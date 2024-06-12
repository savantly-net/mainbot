package net.savantly.mainbot.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.config.AuthorizationConfig;
import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentService;
import net.savantly.mainbot.identity.UserContext;
import net.savantly.mainbot.identity.UserDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentApi {

    private final AuthorizationConfig authorizationConfig;
    private final DocumentService documentService;
    private final UserContext userContext;

    @PostMapping("/add")
    public ResponseEntity<List<String>> addDocument(@RequestBody DocumentAddRequest document) {

        var currentUser = userContext.getCurrentUser().orElseThrow();
        if (isUnauthorized(currentUser, document)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(documentService.addDocument(document));
    }

    @PreAuthorize("${authorization.addDocsExpression}")
    private boolean isUnauthorized(UserDto currentUser, DocumentAddRequest document) {
        log.info("checking authorization for user: {}", currentUser);
        if (currentUser.getGroups().contains(document.getNamespace())) {
            return false;
        }
        return true;
    }

}
