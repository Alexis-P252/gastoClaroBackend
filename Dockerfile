# --- Etapa 1: build ---
# Compila el proyecto con Maven, usando una imagen que ya trae JDK 21 + Maven.
# Esta etapa NO queda en la imagen final, solo se usa para generar el .jar.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar el cache de Docker:
# si no cambiaron las dependencias, Docker no vuelve a descargarlas
# aunque cambies el código después.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Recién ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Etapa 2: runtime ---
# Imagen final, mucho más liviana: solo el JRE (no hace falta el JDK completo
# ni Maven para EJECUTAR la app, solo para compilarla).
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos únicamente el .jar ya compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
