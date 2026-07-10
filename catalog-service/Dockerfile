# ──────────────────────────────────────────────
# Stage 1: Build
# ──────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml ./pom.xml
COPY catalog-service/pom.xml ./catalog-service/pom.xml

RUN mvn -B dependency:go-offline -pl catalog-service -am -q

COPY catalog-service/src ./catalog-service/src

RUN mvn -B package -pl catalog-service -am -DskipTests -q

# ──────────────────────────────────────────────
# Stage 2: Runtime
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

COPY --from=builder /app/catalog-service/target/*.jar app.jar

EXPOSE 8082

ENV SERVER_PORT=8082

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]
