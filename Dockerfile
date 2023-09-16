FROM adoptopenjdk:11-jdk-hotspot

COPY ./build/libs/sroom-0.0.1-SNAPSHOT.jar /build/libs/sroom-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar","/build/libs/sroom-0.0.1-SNAPSHOT.jar"]
