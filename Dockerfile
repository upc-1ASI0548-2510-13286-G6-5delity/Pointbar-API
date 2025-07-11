# Imagen base con JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Crear el directorio de trabajo
WORKDIR /app

# Copiar el archivo .jar al contenedor (usa un nombre genérico para consistencia)
COPY app.jar .

# Copiar archivo de configuración externo si se usa
COPY .env.properties .

# Exponer el puerto (se puede sobreescribir con SERVER_PORT)
EXPOSE 8082

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "-Dspring.config.import=file:.env.properties", "app.jar"]
