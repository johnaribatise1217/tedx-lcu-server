FROM 3.8.5-openjdk-17 AS build
COPY . . 
RUN mvn clean package -DskipTests

FROM 3.8.5-openjdk-17-slim
COPY --from=build target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]