# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and all modules
COPY ../pom.xml ./pom.xml
COPY ../ /app

# Build auth-service
RUN mvn clean package -DskipTests -pl auth-service -am

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar
COPY --from=build /app/auth-service/target/*.jar auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "auth-service.jar", "--server.port=8081", "--server.address=0.0.0.0", "--eureka.client.register-with-eureka=false", "--eureka.client.fetch-registry=false"]

