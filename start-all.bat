@echo off
echo =======================================================
echo Iniciando Sistema SiGePID_Back...
echo =======================================================

echo.
echo 1. Levantando bases de datos (PostgreSQL y MongoDB) con Docker...
docker-compose up -d

echo.
echo Esperando 5 segundos para que las bases de datos esten listas...
timeout /t 5 /nobreak >nul

echo.
echo 2. Iniciando Discovery Server (Eureka)...
start "Discovery Server" cmd /c "title Discovery Server && cd discovery-server && ..\mvnw.cmd spring-boot:run"

echo.
echo Esperando 10 segundos para que Eureka inicialice...
timeout /t 10 /nobreak >nul

echo.
echo 3. Iniciando microservicios restantes en paralelo...
start "API Gateway" cmd /c "title API Gateway && cd api-gateway && ..\mvnw.cmd spring-boot:run"
start "Auth Service" cmd /c "title Auth Service && cd auth-service && ..\mvnw.cmd spring-boot:run"
start "Catalog Service" cmd /c "title Catalog Service && cd catalog-service && ..\mvnw.cmd spring-boot:run"
start "Order Service" cmd /c "title Order Service && cd order-service && ..\mvnw.cmd spring-boot:run"
start "Notification Service" cmd /c "title Notification Service && cd notification-service && ..\mvnw.cmd spring-boot:run"

echo.
echo =======================================================
echo ¡Todos los servicios han sido lanzados!
echo Se abriran 6 ventanas de consola, una para cada servicio.
echo Cuando quieras detener todo, simplemente cierra las ventanas negras.
echo =======================================================
pause
