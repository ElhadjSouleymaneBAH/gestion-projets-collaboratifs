FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y maven && apt-get clean
COPY . .
RUN mvn clean package -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/gestionprojets-0.0.1-SNAPSHOT.jar"]