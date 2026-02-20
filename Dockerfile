# Stage 1: Build da aplicacao
FROM gradle:8.5.0-jdk17-alpine AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
# Baixa as dependencias do gradle
RUN gradle dependencies --no-daemon || true

COPY src ./src
# Compila e gera o build final (.jar)
RUN gradle bootJar --no-daemon

# Stage 2: Imagem final leve
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o jar resultante do builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Define o timezone
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
