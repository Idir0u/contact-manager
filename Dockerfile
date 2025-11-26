# Dockerfile – version 100 % fonctionnelle Railway 2025
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copie tout d’un coup (plus simple et plus fiable)
COPY . .

# Nettoyage complet + build forcé + debug
RUN rm -rf /root/.m2/repository/* && \
    mvn -B -DskipTests clean package && \
    echo "=== CONTENU DU DOSSIER target ===" && \
    ls -la target/ && \
    echo "===================================="

# Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/contact-manager-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]