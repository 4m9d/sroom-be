FROM adoptopenjdk:11-jdk-hotspot

ARG SPRING_PROFILE=default
ENV SPRING_PROFILE=${SPRING_PROFILE}

COPY ./build/libs/sroom-0.0.1-SNAPSHOT.jar /build/libs/sroom-0.0.1-SNAPSHOT.jar


CMD java -jar /build/libs/sroom-0.0.1-SNAPSHOT.jar --spring.profiles.active=${SPRING_PROFILE}"]
