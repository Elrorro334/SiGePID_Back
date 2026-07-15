# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and all modules
COPY ../pom.xml ./pom.xml
COPY ../ /app

# Build discovery-server
RUN mvn clean package -DskipTests -pl discovery-server -am

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar
COPY --from=build /app/discovery-server/target/*.jar discovery-server.jar

EXPOSE 8761

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "discovery-server.jar", "--server.address=0.0.0.0"]

