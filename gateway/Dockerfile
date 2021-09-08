FROM adoptopenjdk:14-jre-hotspot
LABEL maintainer="Ingo Seidel <i.seidel@max-recall.com>"

ARG JAR_FILE
ADD target/${JAR_FILE} app.jar

COPY docker-image/cacert.pem /

# Install max.recall certificate in order to be able to contact SSO server 
# (sso.vhh-*.max-recall.com) in environments with self-signed certificates.
#
RUN openssl x509 -in cacert.pem -inform pem -out cacert.der -outform der \
    && echo yes | keytool -importcert -alias maxrecall -keystore /opt/java/openjdk/lib/security/cacerts -storepass changeit -file cacert.der \
    && rm /cacert.*

EXPOSE 8080

ENV JAVA_OPTS=""

CMD java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
