#!/bin/bash
echo "======================================================="
echo "Iniciando Sistema SiGePID_Back en Railway (Todo en uno)"
echo "======================================================="

# Railway inyecta la variable PORT.
# Solo el API Gateway debe usar este puerto.
RAILWAY_PORT=${PORT:-8080}

# -------------------------------------------------------
# Limitar la memoria de cada JVM.
# -------------------------------------------------------
JVM_OPTS="-Xms128m -Xmx400m"

# -------------------------------------------------------
# PASO 1: Iniciar PRIMERO el API Gateway en el puerto de Railway.
# Esto asegura que Railway detecte el puerto correcto.
# Ya no depende de Eureka (usa URLs directas a localhost).
# -------------------------------------------------------
echo ""
echo "1. Iniciando API Gateway (puerto $RAILWAY_PORT)..."
java $JVM_OPTS -jar api-gateway.jar --server.port=$RAILWAY_PORT &
GATEWAY_PID=$!

# Darle tiempo al Gateway para arrancar
sleep 5

# -------------------------------------------------------
# PASO 2: Iniciar los microservicios en sus puertos fijos.
# El Gateway los encuentra directamente por localhost.
# -------------------------------------------------------
echo ""
echo "2. Iniciando microservicios..."

echo "   -> Auth Service (puerto 8081)"
java $JVM_OPTS -jar auth-service.jar --server.port=8081 --eureka.client.enabled=false &

echo "   -> Catalog Service (puerto 8082)"
java $JVM_OPTS -jar catalog-service.jar --server.port=8082 --eureka.client.enabled=false &

echo "   -> Order Service (puerto 8083)"
java $JVM_OPTS -jar order-service.jar --server.port=8083 --eureka.client.enabled=false &

echo "   -> Notification Service (puerto 8084)"
java $JVM_OPTS -jar notification-service.jar --server.port=8084 --eureka.client.enabled=false &

echo ""
echo "======================================================="
echo "Todos los microservicios fueron lanzados."
echo "API Gateway escuchando en puerto: $RAILWAY_PORT"
echo "======================================================="
echo ""
echo "NOTA: El Discovery Server (Eureka) NO se inicia."
echo "En Railway todos los servicios estan en el mismo"
echo "contenedor, asi que el Gateway usa URLs directas."
echo "======================================================="

# Mantener el contenedor vivo
wait
