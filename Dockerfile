# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and directories
COPY pom.xml .
COPY discovery-server discovery-server
COPY api-gateway api-gateway
COPY auth-service auth-service
COPY catalog-service catalog-service
COPY order-service order-service
COPY notification-service notification-service

# Build all modules (skipping tests for faster build)
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jars from the build stage
COPY --from=build /app/discovery-server/target/*.jar discovery-server.jar
COPY --from=build /app/api-gateway/target/*.jar api-gateway.jar
COPY --from=build /app/auth-service/target/*.jar auth-service.jar
COPY --from=build /app/catalog-service/target/*.jar catalog-service.jar
COPY --from=build /app/order-service/target/*.jar order-service.jar
COPY --from=build /app/notification-service/target/*.jar notification-service.jar

# Copy the startup script
COPY start-railway.sh start.sh
RUN chmod +x start.sh

# The PORT environment variable is automatically provided by Railway
EXPOSE 8080 8081 8082 8083 8084 8761

# Run the startup script
ENTRYPOINT ["./start.sh"]
