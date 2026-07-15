# ──────────────────────────────────────────────
# Stage 1: Build
# ──────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copia el POM raíz y el POM del módulo para aprovechar el cache de dependencias
COPY pom.xml ./pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY catalog-service/pom.xml ./catalog-service/pom.xml
COPY discovery-server/pom.xml ./discovery-server/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml

# Descarga dependencias (cache layer)
RUN mvn -B dependency:go-offline -pl discovery-server -am -q

# Copia el código fuente del módulo
COPY discovery-server/src ./discovery-server/src

# Compila y empaqueta solo este módulo
RUN mvn -B package -pl discovery-server -am -DskipTests -q

# ──────────────────────────────────────────────
# Stage 2: Runtime
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app

COPY --from=builder /app/discovery-server/target/*.jar app.jar

# Puerto de Eureka
EXPOSE 8761

# Railway inyecta PORT como variable de entorno; lo respetamos si existe
ENV SERVER_PORT=8761

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]
