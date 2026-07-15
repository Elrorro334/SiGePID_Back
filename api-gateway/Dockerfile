# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and all modules
COPY ../pom.xml ./pom.xml
COPY ../ /app

# Build api-gateway
RUN mvn clean package -DskipTests -pl api-gateway -am

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar
COPY --from=build /app/api-gateway/target/*.jar api-gateway.jar

# Railway inyecta PORT automáticamente
EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "api-gateway.jar", "--server.port=${PORT:-8080}", "--server.address=0.0.0.0"]

