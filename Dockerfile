FROM clojure:lein-2.8.1-alpine
MAINTAINER Lucas Marques <lucasmarquesds@gmail.com>

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN lein uberjar

EXPOSE 8080

CMD ["java", "-jar", "target/uberjar/maestro-1.0.0-standalone.jar"]
