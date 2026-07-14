#!/bin/bash
echo "======================================================="
echo "Iniciando Sistema SiGePID_Back en Railway (Todo en uno)"
echo "======================================================="

# Railway inyecta la variable PORT.
# Solo el API Gateway debe usar este puerto.
RAILWAY_PORT=${PORT:-8080}

# Dado que tienes 8GB de RAM, asignamos memoria generosa pero segura
JVM_OPTS="-Xms256m -Xmx512m"

# Argumentos para desactivar Eureka
EUREKA_OFF="--eureka.client.register-with-eureka=false --eureka.client.fetch-registry=false"

# -------------------------------------------------------
# PASO 1: Iniciar PRIMERO el API Gateway
# -------------------------------------------------------
echo ""
echo "1. Iniciando API Gateway (puerto $RAILWAY_PORT)..."
java $JVM_OPTS -jar api-gateway.jar --server.port=$RAILWAY_PORT --server.address=0.0.0.0 &

# Esperar a que el Gateway levante comprobando su Health Check
echo "Esperando a que el API Gateway responda..."
while ! curl -s http://localhost:$RAILWAY_PORT/ > /dev/null; do
    sleep 2
done
echo "API Gateway levantado exitosamente. Railway conectará el proxy ahora."

# -------------------------------------------------------
# PASO 2: Iniciar el resto de servicios escalonadamente
# (15 segundos entre cada uno para evitar picos de CPU)
# -------------------------------------------------------
echo ""
echo "2. Iniciando microservicios..."

echo "   -> Auth Service (puerto 8081)"
java $JVM_OPTS -jar auth-service.jar --server.port=8081 $EUREKA_OFF &
sleep 15

echo "   -> Catalog Service (puerto 8082)"
java $JVM_OPTS -jar catalog-service.jar --server.port=8082 $EUREKA_OFF &
sleep 15

echo "   -> Order Service (puerto 8083)"
java $JVM_OPTS -jar order-service.jar --server.port=8083 $EUREKA_OFF &
sleep 15

echo "   -> Notification Service (puerto 8084)"
java $JVM_OPTS -jar notification-service.jar --server.port=8084 $EUREKA_OFF &

echo ""
echo "======================================================="
echo "Todos los microservicios fueron lanzados."
echo "API Gateway escuchando en puerto: $RAILWAY_PORT"
echo "======================================================="

# Mantener el contenedor vivo
wait
