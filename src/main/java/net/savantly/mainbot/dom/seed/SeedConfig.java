package net.savantly.mainbot.dom.seed;

import java.nio.file.Files;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import dev.langchain4j.data.document.Document;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentService;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SeedConfig {

    private final DocumentService documentService;

    private final SeedConfigProperties seed;

    @PostConstruct
    public void postConstruct() {
        if (!seed.isEnabled()) {
            log.info("seeding disabled");
            return;
        }
        try {
            var folderResource = ResourceUtils.getFile(seed.getSeedFolder());
            if (folderResource.exists()) {
                log.info("seeding from {}", folderResource.getAbsolutePath());
                var seedFiles = folderResource.listFiles();
                for (var seedFileItem : seedFiles) {
                    log.info("seeding {}", seedFileItem.getName());
                    var seedFile = ResourceUtils.getFile(seedFileItem.getAbsolutePath());
                    if (seedFile.exists()) {
                        log.info("seeding {}", seedFile.getAbsolutePath());
                        var document = Document.from(Files.readString(seedFile.toPath()));
                        var request = new DocumentAddRequest()
                                .setId(seedFile.getName())
                                .setNamespace(seed.getNamespace())
                                .setText(document.text())
                                .setUri(seedFile.getName());

                        documentService.addDocument(request);
                    } else {
                        log.error("seed file {} does not exist", seedFile.getAbsolutePath());
                    }
                }
            } else {
                log.error("seed folder {} does not exist", folderResource.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("seed failed", e);
        }

    }

}
