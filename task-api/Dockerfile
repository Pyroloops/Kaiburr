# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final, slim image 
FROM eclipse-temurin:17-jre-alpine 
WORKDIR /app 
COPY --from=build /app/target/*.jar app.jar 
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]