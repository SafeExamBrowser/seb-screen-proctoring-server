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

CMD if [ "x${JMX_PORT}" = "x" ] ; \
    then exec java \
        -Xms${JAVA_HEAP_MIN} \
        -Xmx${JAVA_HEAP_MAX} \
        -jar "seb-sps.jar" \
        --spring.config.location=file:/sebsps/config/spring/,classpath:/config/; \
    else echo "admin ${JMX_SECRET}" > jmxremote.password && chown spring:spring /sebsps/jmxremote.password && chmod 400 /sebsps/jmxremote.password && exec java \
        -Xms${JAVA_HEAP_MIN} \
        -Xmx${JAVA_HEAP_MAX} \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.port=${JMX_PORT} \
        -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT} \
        -Djava.rmi.server.hostname=localhost \
        -Dcom.sun.management.jmxremote.local.only=false \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.authenticate=true \
        -Dcom.sun.management.jmxremote.password.file=/sebsps/jmxremote.password \
        -Dcom.sun.management.jmxremote.access.file=/sebsps/config/jmx/jmxremote.access \
        -jar "seb-sps.jar" \
        --spring.config.location=file:/sebsps/config/spring/,classpath:/config/; \
fi

EXPOSE $SERVER_PORT $JMX_PORT