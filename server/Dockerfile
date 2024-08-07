FROM openjdk:11

WORKDIR /app

COPY . /app

RUN ["javac", "org/uom/tesla/BootstrapServer.java"]

CMD ["java", "org/uom/tesla/BootstrapServer"]