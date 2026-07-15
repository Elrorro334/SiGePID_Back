# ──────────────────────────────────────────────
# Stage 1: Build
# ──────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml ./pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY catalog-service/pom.xml ./catalog-service/pom.xml
COPY discovery-server/pom.xml ./discovery-server/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml

RUN mvn -B dependency:go-offline -pl catalog-service -am -q

COPY catalog-service/src ./catalog-service/src

RUN mvn -B package -pl catalog-service -am -DskipTests -q

# ──────────────────────────────────────────────
# Stage 2: Runtime (Jammy — ONNX Runtime needs glibc + libstdc++)
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy AS runtime

RUN groupadd -r spring && useradd -r -g spring spring
USER spring

WORKDIR /app

COPY --from=builder /app/catalog-service/target/*.jar app.jar

EXPOSE 8082

ENV SERVER_PORT=8082

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]

