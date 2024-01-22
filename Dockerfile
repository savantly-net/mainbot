FROM eclipse-temurin:17-jre as builder
WORKDIR /build
COPY build/libs/mainbot*-SNAPSHOT.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder build/dependencies/ ./
COPY --from=builder build/snapshot-dependencies/ ./
COPY --from=builder build/spring-boot-loader/ ./
COPY --from=builder build/application/ ./
COPY docker/start.sh start.sh
ENV PORT=8080
ENTRYPOINT ["./start.sh"]