FROM gradle:8.10.2-jdk21-alpine AS builder
WORKDIR /app
COPY src/main ./src/main
COPY build.gradle settings.gradle  ./

RUN gradle clean build

FROM gcr.io/distroless/java21-debian12
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]