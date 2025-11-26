# Dockerfile – version ultra-robuste pour Railway + Spring Boot 3.5.7 + Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copie d’abord les fichiers Maven pour profiter du cache
COPY pom.xml .
COPY src ./src

# Build propre + affiche le nom du JAR généré (debug)
RUN mvn clean package -DskipTests && \
    echo "=== JAR généré ===" && \
    ls -la target/*.jar && \
    echo "===================="

# Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copie avec le nom EXACT du JAR (évite tout wildcard foireux)
COPY --from=build /app/target/contact-manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]