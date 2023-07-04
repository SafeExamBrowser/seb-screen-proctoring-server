FROM openjdk:17-jdk-slim

ENV SERVER_PORT="8090"
ENV JMX_PORT=
ENV JAVA_HEAP_MIN=
ENV JAVA_HEAP_MAX=

#just try wihtout user to check if we habe a permission problem
RUN groupadd --system spring && useradd --system --gid spring spring && mkdir /sebsps && chown spring:spring /sebsps
USER spring:spring

COPY seb-sps.jar /sebsps/

WORKDIR /sebsps

CMD exec java \
        -Xms${JAVA_HEAP_MIN} \
        -Xmx${JAVA_HEAP_MAX} \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.port=9090 \
        -Dcom.sun.management.jmxremote.rmi.port=9090 \
        -Djava.rmi.server.hostname=localhost \
        -Dcom.sun.management.jmxremote.local.only=false \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -jar "seb-sps.jar" \
        --spring.config.location=file:/sebsps/config/spring/,classpath:/config/; \

EXPOSE $SERVER_PORT $JMX_PORT