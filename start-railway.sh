#!/bin/bash
echo "======================================================="
echo "Iniciando Sistema SiGePID_Back en Railway (Todo en uno)"
echo "======================================================="

# Railway inyecta la variable PORT.
# Solo el API Gateway debe usar este puerto.
RAILWAY_PORT=${PORT:-8080}

# Limitar la memoria de cada JVM.
JVM_OPTS="-Xms128m -Xmx400m"

# Argumentos para desactivar Eureka en cada servicio
# (no se necesita service discovery porque todo corre en localhost)
EUREKA_OFF="--eureka.client.register-with-eureka=false --eureka.client.fetch-registry=false"

# -------------------------------------------------------
# PASO 1: Iniciar PRIMERO el API Gateway en el puerto de Railway.
# -------------------------------------------------------
echo ""
echo "1. Iniciando API Gateway (puerto $RAILWAY_PORT)..."
java $JVM_OPTS -jar api-gateway.jar --server.port=$RAILWAY_PORT &

# Darle tiempo al Gateway para arrancar y que Railway lo detecte
sleep 10

# -------------------------------------------------------
# PASO 2: Iniciar los microservicios en puertos fijos.
# -------------------------------------------------------
echo ""
echo "2. Iniciando microservicios..."

echo "   -> Auth Service (puerto 8081)"
java $JVM_OPTS -jar auth-service.jar --server.port=8081 $EUREKA_OFF &

echo "   -> Catalog Service (puerto 8082)"
java $JVM_OPTS -jar catalog-service.jar --server.port=8082 $EUREKA_OFF &

echo "   -> Order Service (puerto 8083)"
java $JVM_OPTS -jar order-service.jar --server.port=8083 $EUREKA_OFF &

echo "   -> Notification Service (puerto 8084)"
java $JVM_OPTS -jar notification-service.jar --server.port=8084 $EUREKA_OFF &

echo ""
echo "======================================================="
echo "Todos los microservicios fueron lanzados."
echo "API Gateway escuchando en puerto: $RAILWAY_PORT"
echo "======================================================="

# Mantener el contenedor vivo
wait
