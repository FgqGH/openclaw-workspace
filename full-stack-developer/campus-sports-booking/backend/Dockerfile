FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download all dependencies first
COPY pom.xml .
RUN mvn dependency:resolve -DskipTests && mvn dependency:resolve-plugins -DskipTests

# Copy source code
COPY src ./src

# Build
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Xmx512m", "-Xms256m", "app.jar"]