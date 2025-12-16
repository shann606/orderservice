# Build stage

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# Runtime Stage

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar  app.jar

#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar

EXPOSE 8082

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar" ,"--spring.profile.active=local-secure"]



