FROM openjdk:11

WORKDIR /app

COPY . /app

RUN ["javac", "BootstrapClient.java"]

CMD ["java", "BootstrapClient"]