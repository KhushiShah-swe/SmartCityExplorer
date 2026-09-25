FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml backend/pom.xml
COPY shared shared
COPY backend/src backend/src
RUN mvn -B -ntp -f backend/pom.xml package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd --system explorer && useradd --system --gid explorer explorer
COPY --from=build /app/backend/target/backend-1.0.0.jar app.jar
USER explorer
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
