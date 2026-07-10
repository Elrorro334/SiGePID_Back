# ──────────────────────────────────────────────
# Stage 1: Build
# ──────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml ./pom.xml
COPY auth-service/pom.xml ./auth-service/pom.xml

RUN mvn -B dependency:go-offline -pl auth-service -am -q

COPY auth-service/src ./auth-service/src

RUN mvn -B package -pl auth-service -am -DskipTests -q

# ──────────────────────────────────────────────
# Stage 2: Runtime
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

COPY --from=builder /app/auth-service/target/*.jar app.jar

EXPOSE 8081

ENV SERVER_PORT=8081

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]
