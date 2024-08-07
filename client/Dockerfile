FROM openjdk:17

WORKDIR /app

COPY . /app

RUN ["javac", "org/uom/tesla/TestClient.java"]

CMD ["java", "org/uom/tesla/TestClient"]