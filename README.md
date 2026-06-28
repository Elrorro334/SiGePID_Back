# SiGePID Back

**Sistema de Gestión de Pedidos e Inventario Distribuido** – Backend basado en microservicios con Spring Boot 3 y Java 17.

## Arquitectura

| Servicio               | Puerto | Descripción                                    |
|------------------------|--------|------------------------------------------------|
| `discovery-server`     | 8761   | Eureka Server – Registro y descubrimiento      |
| `api-gateway`          | 8080   | Spring Cloud Gateway – Punto de entrada único   |
| `auth-service`         | 8081   | Autenticación y usuarios (JWT + PostgreSQL)     |
| `catalog-service`      | 8082   | Catálogo e inventario inteligente (MongoDB)     |
| `order-service`        | 8083   | Gestión de pedidos (PostgreSQL)                 |
| `notification-service` | 8084   | Notificaciones y alertas                        |

## Requisitos

- Java 17+
- Maven 3.9+
- Docker & Docker Compose (para bases de datos)

## Inicio Rápido

```bash
# 1. Levantar las bases de datos
docker-compose up -d

# 2. Compilar todos los módulos
mvn clean install -DskipTests

# 3. Ejecutar cada servicio (en terminales separadas, en este orden)
cd discovery-server  && mvn spring-boot:run
cd api-gateway       && mvn spring-boot:run
cd auth-service      && mvn spring-boot:run
cd catalog-service   && mvn spring-boot:run
cd order-service     && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

## Estructura del Proyecto (Clean Architecture)

Cada microservicio de negocio sigue la separación por capas inspirada en .NET Core:

```
service-name/
└── src/main/java/com/sigepid/<service>/
    ├── domain/           # Entidades, Enums, Interfaces de repositorio
    ├── application/      # DTOs, Servicios de aplicación (casos de uso)
    ├── infrastructure/   # Implementación de repositorios, configuración
    └── presentation/     # Controladores REST
```

## Licencia

Uso privado – Todos los derechos reservados.
