FROM openjdk:8-jdk

ARG PROFILE=local
ARG SERVICE_NAME=olapic

# TODO add config arg
ENV PROFILE=${PROFILE}
ENV SERVICE_NAME=${SERVICE_NAME}
ENV LOGS=/microservices/logs

RUN mkdir -p /microservices/logs

WORKDIR microservices

COPY target/olapic*.jar service.jar

EXPOSE $APP_PORT

ENTRYPOINT java -jar -Djava.security.egd=file:/dev/./urandom \
-Dspring.profiles.active=$PROFILE \
-Xss512K -Xms256M -Xmx512M \
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=8 \
-XX:ConcGCThreads=2 -XX:InitiatingHeapOccupancyPercent=70 service.jar