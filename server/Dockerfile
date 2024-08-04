FROM openjdk:11

WORKDIR /app

COPY . /app

RUN ["javac", "BootstrapServer.java"]

CMD ["java", "BootstrapServer"]