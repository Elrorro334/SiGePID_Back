#!/bin/bash
echo "======================================================="
echo "Iniciando Sistema SiGePID_Back en Railway (Todo en uno)"
echo "======================================================="

# Railway inyecta la variable PORT.
# Solo el API Gateway debe usar este puerto.
RAILWAY_PORT=${PORT:-8080}

# Limitar la memoria de cada JVM drasticamente para evitar OOM (Out Of Memory)
JVM_OPTS="-Xms64m -Xmx128m -Xss512k"

# Argumentos para desactivar Eureka
EUREKA_OFF="--eureka.client.register-with-eureka=false --eureka.client.fetch-registry=false"

# -------------------------------------------------------
# PASO 1: Iniciar los microservicios en background.
# -------------------------------------------------------
echo ""
echo "1. Iniciando microservicios..."

echo "   -> Auth Service (puerto 8081)"
java $JVM_OPTS -jar auth-service.jar --server.port=8081 $EUREKA_OFF &
sleep 5

echo "   -> Catalog Service (puerto 8082)"
java $JVM_OPTS -jar catalog-service.jar --server.port=8082 $EUREKA_OFF &
sleep 5

echo "   -> Order Service (puerto 8083)"
java $JVM_OPTS -jar order-service.jar --server.port=8083 $EUREKA_OFF &
sleep 5

echo "   -> Notification Service (puerto 8084)"
java $JVM_OPTS -jar notification-service.jar --server.port=8084 $EUREKA_OFF &
sleep 5

# -------------------------------------------------------
# PASO 2: Iniciar el API Gateway en FOREGROUND.
# Si el Gateway crashea, el contenedor se detendrá y veremos el error real.
# -------------------------------------------------------
echo ""
echo "======================================================="
echo "Microservicios en background lanzados."
echo "Iniciando API Gateway (puerto $RAILWAY_PORT) en FOREGROUND..."
echo "======================================================="
java $JVM_OPTS -jar api-gateway.jar --server.port=$RAILWAY_PORT --server.address=0.0.0.0

