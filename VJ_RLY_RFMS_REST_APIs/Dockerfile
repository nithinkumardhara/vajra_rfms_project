FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/VJ_RLY_RFMS_REST_APIs-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8889

ENTRYPOINT ["java","-jar","app.jar"]
