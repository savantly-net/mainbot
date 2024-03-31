package net.savantly.mainbot.dom.opensearch;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.springframework.core.io.DefaultResourceLoader;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.opensearch.ml.ModelGroup;

/**
 * This will bootstrap the Opensearch required indexes and mappings
 */
@RequiredArgsConstructor
@Slf4j
public class OpenSearchBoostrapper {

    private final OpenSearchClient client;
    private final OpenSearchConfiguration.Bootstrap configuration;

    private boolean bootStrapped;
    private OpenSearchRawClient rawClient;

    private Optional<String> modelGroupId = Optional.empty();

    public Optional<String> getModelGroupId() {
        return modelGroupId;
    }

    private Optional<String> modelId = Optional.empty();

    public Optional<String> getModelId() {
        return modelId;
    }

    private Optional<String> modelRegistrationTaskId = Optional.empty();

    public void bootstrap() {

        this.rawClient = new OpenSearchRawClient(client);

        if (ensurePluginInstalled("opensearch-knn") &&
                ensureIndexExists()) {
            bootStrapped = true;
        }

        if (configuration.isEnableMachineLearning()) {
            if (ensurePluginInstalled("opensearch-ml") &&
                    ensurePluginInstalled("opensearch-flow-framework") &&
                    ensureModelGroupExists() &&
                    ensureModelDeployed()) {
                bootStrapped = true;
            } else {
                bootStrapped = false;
            }
        }

        if (!bootStrapped) {
            throw new RuntimeException("Failed to bootstrap Opensearch");
        }

    }

    private Optional<String> readResourceAsString(String resourcePath) {
        try {
            var resource = new DefaultResourceLoader().getResource(resourcePath);
            return Optional.of(new String(resource.getInputStream().readAllBytes()));
        } catch (IOException e) {
            log.error("Error reading resource: {}", resourcePath, e);
            return Optional.empty();
        }
    }

    private boolean ensureModelGroupExists() {
        try {
            var modelGroupName = configuration.getModelGroupName();
            log.info("Checking if model group exists");

            var indexName = ".plugins-ml-model-group";

            var indexExistsResponse = client.indices().exists(request -> request.index(indexName));
            if (indexExistsResponse.value()) {
                log.info("Model group index exists");
                var response = client.search(fn -> fn.index(indexName)
                        .query(q -> q.match(match -> match.field("name").query(mq -> mq.stringValue(modelGroupName)))),
                        Object.class);

                if (response.hits().total().value() > 0) {
                    modelGroupId = Optional.of((String) response.hits().hits().get(0).id());
                } else {
                    if (!createModelGroup()) {
                        return false;
                    }
                }
            } else {
                if (!createModelGroup()) {
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            log.error("Error checking if embedding model is registered", e);
            return false;
        }
    }

    private boolean ensureModelDeployed() {
        try {
            var modelName = configuration.getModelName();
            log.info("Checking if model exists");

            var indexName = ".plugins-ml-model";

            var indexExistsResponse = client.indices().exists(request -> request.index(indexName));
            if (indexExistsResponse.value()) {
                log.info("Model index exists");
                var response = client.search(fn -> fn.index(indexName)
                        .query(q -> {
                            return q.bool(b -> {
                                return b.should(s -> s
                                        .match(match -> match.field("name").query(mq -> mq.stringValue(modelName))))
                                        .mustNot(mn -> mn.exists(exists -> exists.field("chunk_number")));
                            });
                        }),
                        Object.class);

                if (response.hits().total().value() > 0) {
                    log.info("Model exists: {}");
                    var id = (String) response.hits().hits().get(0).id();
                    this.modelId = Optional.of(id);
                } else {
                    if (!registerModel()) {
                        return false;
                    }
                }
            } else {
                if (!registerModel()) {
                    return false;
                }
            }

            if (modelRegistrationTaskId.isPresent()) {
                if (!checkModelRegistration(modelRegistrationTaskId.get(), 10, 10)) {
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            log.error("Error checking if embedding model is registered", e);
            return false;
        }
    }

    private boolean registerModel() {
        try {
            log.info("Registering model");
            // Read file from resources folder into a string
            var modelRegistration = readResourceAsString(configuration.getModelRegistrationPath())
                    .orElseThrow(() -> new RuntimeException("Model Registration file not found"));
            // replace the openai api key
            for (var entry : configuration.getModelRegistrationReplacements().entrySet()) {
                modelRegistration = modelRegistration.replace(entry.getKey(), entry.getValue());
            }
            modelRegistration = modelRegistration.replace("MODEL_GROUP_ID", modelGroupId.get());
            modelRegistration = modelRegistration.replace("MODEL_NAME", configuration.getModelName());
            var result = rawClient.registerModel(modelRegistration);
            this.modelRegistrationTaskId = Optional.of((String) result.get("task_id"));
            log.info("Registered model: {}", result);
        } catch (IOException e) {
            log.error("Error registering model", e);
            return false;
        }
        return true;
    }

    private boolean checkModelRegistration(String taskId, int maxRetries, int retryIntervalSeconds) {

        for (int i = 0; i < maxRetries; i++) {
            try {
                log.info("Checking if model registration is complete");
                var result = rawClient.getMlTaskStatus(taskId);
                log.info("Model registration status: {}", result);
                if (result.get("state").equals("COMPLETED")) {
                    return true;
                }
            } catch (IOException e) {
                log.error("Error checking if embedding model is registered", e);
            }
            try {
                Thread.sleep(retryIntervalSeconds * 1000);
            } catch (InterruptedException e) {
                log.error("Error sleeping", e);
            }
        }

        return false;
    }

    private boolean createModelGroup() {
        var modelGroupName = configuration.getModelGroupName();
        try {
            log.info("Creating model group");
            var result = rawClient.createModelGroup(
                    new ModelGroup().setName(modelGroupName).setDescription("Mainbot model group"));
            modelGroupId = Optional.of((String) result.get("model_group_id"));
            log.info("Created model group: {}", result);
        } catch (IOException e) {
            log.error("Error creating model group", e);
            return false;
        }
        return true;
    }

    protected boolean ensureIndexExists() {
        var indexName = configuration.getIndexName();
        try {
            // create the index and mappings
            log.info("Checking if index exists: {}", indexName);
            var indexExistsResponse = client.indices().exists(request -> request.index(indexName));
            if (!indexExistsResponse.value()) {
                log.info("Creating index: {}", indexName);
                client.indices().create(request -> request.index(indexName).mappings(mappings -> {
                    mappings
                            .properties("id", (propBuilder) -> propBuilder.keyword(keyword -> keyword.store(true)))
                            .properties("uri", (propBuilder) -> propBuilder.keyword(keyword -> keyword.store(true)))
                            .properties("embedding", (propBuilder) -> propBuilder.knnVector(knn -> knn.dimension(1536)))
                            .properties("chunkIndex",
                                    (propBuilder) -> propBuilder.integer(integer -> integer.store(true)))
                            .properties("text", (propBuilder) -> propBuilder.text(text -> text.fielddata(true)))
                            .properties("timestamp", (propBuilder) -> propBuilder.date(date -> date.store(true)))
                            .properties("parentId",
                                    (propBuilder) -> propBuilder.keyword(keyword -> keyword.store(true)));

                    return mappings;
                }).settings(settings -> {
                    settings
                            .knn(true)
                            .knnAlgoParamEfSearch(100);
                    return settings;
                }));
            }
            return true;
        } catch (OpenSearchException | IOException e) {
            log.error("Error creating index: {}", indexName, e);
            return false;
        }
    }

    protected boolean ensurePluginInstalled(String pluginName) {
        // install the knn plugin
        log.info("Checking if {} plugin is installed", pluginName);
        try {
            var response = client.nodes().info(request -> {
                request.metric("plugins");
                return request;
            });

            var plugins = response.nodes().values().stream().findFirst().get().plugins();

            if (plugins.stream().anyMatch(p -> p.name().contains(pluginName))) {
                log.info("{} plugin is installed", pluginName);
            } else {
                log.error("{} plugin is not installed", pluginName);
                var pluginNames = plugins.stream().map(p -> p.name()).reduce((a, b) -> a + ", " + b).orElse("none");
                log.error("Installed plugins: {}", pluginNames);
                return false;
            }

            return true;
        } catch (OpenSearchException | IOException e) {
            log.error("Error checking if plugin is installed", e);
            return false;
        }
    }


    /**********************************/
    /********** EXPERIMENTAL **********/
    /**********************************/
 
    private boolean ensureWorkflowDeployed() {
        try {
            log.info("Checking if workflow is deployed");
            // Read file from resources folder into a string
            var workflow = readResourceAsString(configuration.getAgentWorkflowPath())
                    .orElseThrow(() -> new RuntimeException("Workflow not found"));
            // replace the openai api key
            for (var entry : configuration.getAgentWorkflowReplacements().entrySet()) {
                workflow = workflow.replace(entry.getKey(), entry.getValue());
            }

            var response = rawClient.runScript("POST", "_plugins/_flow_framework/workflow", Collections.emptyMap(),
                    Optional.of(workflow));

            return true;
        } catch (Exception e) {
            log.error("Error checking if embedding model is registered", e);
            return false;
        }
    }

    private boolean updateSetting(String setting, String value) {
        try {
            log.info("Updating setting: {}", setting);
            var clusterSetting = new ClusterSetting();
            clusterSetting.persistent = Map.of(setting, value);
            var response = rawClient.runScript("PUT", "_cluster/settings", Collections.emptyMap(),
                    Optional.of(clusterSetting));
            log.info("Updated setting: {}", response);
            return true;
        } catch (IOException e) {
            log.error("Error checking if embedding model is registered", e);
            return false;
        }
    }

    @Data
    private static class ClusterSetting {
        private Map<String, Object> persistent;
    }
}
