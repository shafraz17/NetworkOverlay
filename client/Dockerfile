FROM openjdk:11

WORKDIR /app

COPY . /app

RUN ["javac", "org/uom/tesla/controller/BootstrapNode.java"]

CMD ["java", "org/uom/tesla/controller/BootstrapNode"]