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

RUN mvn -B dependency:go-offline -pl notification-service -am -q

COPY notification-service/src ./notification-service/src

RUN mvn -B package -pl notification-service -am -DskipTests -q

# ──────────────────────────────────────────────
# Stage 2: Runtime
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

COPY --from=builder /app/notification-service/target/*.jar app.jar

EXPOSE 8084

ENV SERVER_PORT=8084

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]
