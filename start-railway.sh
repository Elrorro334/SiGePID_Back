#!/bin/bash
echo "======================================================="
echo "Iniciando Sistema SiGePID_Back en Railway (Todo en uno)"
echo "======================================================="

# Railway inyecta la variable PORT (usualmente 8080 o similar)
# Asignaremos este puerto al API Gateway para que sea publico.
RAILWAY_PORT=${PORT:-8080}

echo "1. Iniciando Discovery Server en puerto 8761..."
java -jar discovery-server.jar --server.port=8761 &
# Esperamos a que Eureka levante para que los demas puedan registrarse
sleep 15

echo "2. Iniciando los demas microservicios en background..."
# A todos les sobreescribimos el puerto explicitamente para que no colisionen
# con la variable de entorno PORT que inyecta Railway.

java -jar api-gateway.jar --server.port=$RAILWAY_PORT &
java -jar auth-service.jar --server.port=8081 &
java -jar catalog-service.jar --server.port=8082 &
java -jar order-service.jar --server.port=8083 &
java -jar notification-service.jar --server.port=8084 &

echo "======================================================="
echo "¡Todos los microservicios estan en ejecucion!"
echo "API Gateway escuchando en el puerto: $RAILWAY_PORT"
echo "======================================================="

# El comando 'wait -n' hara que el script se quede bloqueado ejecutandose.
# Si CUALQUIERA de los microservicios falla y se cierra, el script terminara,
# lo que causara que Railway reinicie el contenedor completo (comportamiento deseado).
wait -n
