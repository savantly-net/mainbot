package net.savantly.mainbot.config;

import java.nio.file.Files;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import dev.langchain4j.data.document.Document;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.documents.DocumentAddRequest;
import net.savantly.mainbot.dom.documents.DocumentService;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SeedConfig {

    @ConfigurationProperties(prefix = "seed")
    @Data
    static class SeedConfigProperties {
        private boolean enabled = true;
        private String seedFolder = "classpath:seed";
        private String namespace = "/admin";
    }

    private final DocumentService documentService;

    private SeedConfigProperties properties() {
        return new SeedConfigProperties();
    }

    @PostConstruct
    public void postConstruct() {
        var properties = properties();
        if (!properties.isEnabled()) {
            log.info("seeding disabled");
            return;
        }
        try {
            var folderResource = ResourceUtils.getFile(properties.getSeedFolder());
            if (folderResource.exists()) {
                log.info("seeding from {}", folderResource.getAbsolutePath());
                var seedFiles = folderResource.listFiles();
                for (var seedFile : seedFiles) {
                    log.info("seeding {}", seedFile.getName());
                    var seed = ResourceUtils.getFile(seedFile.getAbsolutePath());
                    if (seed.exists()) {
                        log.info("seeding {}", seed.getAbsolutePath());
                        var document = Document.from(Files.readString(seed.toPath()));
                        var request = new DocumentAddRequest()
                                .setId(seedFile.getName())
                                .setNamespace(properties.getNamespace())
                                .setText(document.text())
                                .setUri(seedFile.getName());

                        documentService.addDocument(request);
                    } else {
                        log.error("seed file {} does not exist", seed.getAbsolutePath());
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
