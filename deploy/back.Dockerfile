FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8 as build-deps

WORKDIR /usr/src/app

COPY build.sbt ./
COPY application ./application
COPY domain ./domain
COPY infrastructure ./infrastructure
COPY library ./library
COPY courseTemplateEngine ./courseTemplateEngine
COPY project ./project

RUN sbt infrastructure/assembly

FROM openjdk:11-slim

WORKDIR /usr/app
COPY --from=build-deps /usr/src/app/infrastructure/target/scala-2.12 .
COPY install-deps.sh .

RUN apt-get update
RUN apt-get install -y wget
RUN /bin/bash install-deps.sh

EXPOSE 8080
CMD ["java", "-jar", "-Dconfig.resource=application-prod.conf", "/usr/app/lambdacademy.jar"]