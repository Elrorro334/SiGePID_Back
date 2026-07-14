#!/bin/bash
echo "======================================================="
echo "Iniciando Sistema SiGePID_Back en Railway (Todo en uno)"
echo "======================================================="

# Railway inyecta la variable PORT (usualmente 8080)
# Se lo asignamos al API Gateway que es el unico que recibe trafico externo.
RAILWAY_PORT=${PORT:-8080}

# -------------------------------------------------------
# Limitar la memoria de cada JVM para que no se coman todo.
# Con 3 GB de RAM total, le damos ~400 MB a cada uno (6 servicios).
# -------------------------------------------------------
JVM_OPTS="-Xms128m -Xmx400m"

echo ""
echo "1. Iniciando Discovery Server (Eureka) en puerto 8761..."
java $JVM_OPTS -jar discovery-server.jar --server.port=8761 &
DISCOVERY_PID=$!

echo "   Esperando a que Eureka inicialice (20s)..."
sleep 20

echo ""
echo "2. Iniciando microservicios..."

# API Gateway - usa el puerto que Railway espera
echo "   -> API Gateway (puerto $RAILWAY_PORT)"
java $JVM_OPTS -jar api-gateway.jar --server.port=$RAILWAY_PORT &

# Auth Service
echo "   -> Auth Service (puerto 8081)"
java $JVM_OPTS -jar auth-service.jar --server.port=8081 &

# Catalog Service
echo "   -> Catalog Service (puerto 8082)"
java $JVM_OPTS -jar catalog-service.jar --server.port=8082 &

# Order Service
echo "   -> Order Service (puerto 8083)"
java $JVM_OPTS -jar order-service.jar --server.port=8083 &

# Notification Service
echo "   -> Notification Service (puerto 8084)"
java $JVM_OPTS -jar notification-service.jar --server.port=8084 &

echo ""
echo "======================================================="
echo "Todos los microservicios fueron lanzados."
echo "API Gateway escuchando en puerto: $RAILWAY_PORT"
echo "======================================================="

# Mantener el contenedor vivo. Si cualquier proceso hijo muere,
# el script termina y Railway reiniciara el contenedor.
wait -n
