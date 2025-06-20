# Imagen base con JDK 17 (puedes cambiar a 21 si es tu versión)
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el JAR generado al contenedor
COPY app.jar app.jar

# Copia el archivo .env.properties al contenedor
COPY .env.properties /app/.env.properties

# Expone el puerto (puede sobreescribirse por env SERVER_PORT)
EXPOSE 8082

# Ejecuta el JAR cargando las propiedades desde el archivo externo
ENTRYPOINT ["sh", "-c", "java -jar -Dspring.config.import=file:.env.properties app.jar"]

