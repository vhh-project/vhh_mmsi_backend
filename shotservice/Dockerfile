FROM adoptopenjdk:14-jre-hotspot
LABEL maintainer="Ingo Seidel <i.seidel@max-recall.com>"

ARG JAR_FILE
ADD target/${JAR_FILE} app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

CMD java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
