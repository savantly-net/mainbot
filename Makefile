
.PHONY dev:
dev:
	@echo "Starting the application..."
	@echo "Make sure you have a .env file in the root directory"
	@echo "Exporting environment variables..."
	$(call setup_env, .env)
	SPRING_PROFILES_ACTIVE=dev,${SPRING_PROFILES_ACTIVE} ./gradlew bootRun

.PHONY dev-docker:
dev-docker:
	@echo "Starting the application..."
	docker-compose -f docker-compose.yml up --build

.PHONY build-test-image:
build-test-image:
	@echo "Building the test image..."
	./gradlew clean build -x test
	docker build -t mainbot-test -f Dockerfile .


# Export each line in the .env file as an environment variable
define setup_env
	$(eval ENV_FILE := $(1))
	@echo " - setup env $(ENV_FILE)"
	$(eval include $(1))
	$(eval export)
endef