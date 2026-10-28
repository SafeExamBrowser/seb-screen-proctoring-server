FROM eclipse-temurin:21-jre

# Temporary: remove eMudhra G3 trust until the JDK vendor ships a fix
RUN rm /etc/ssl/certs/emSign_ECC_Root_CA_-_C3.pem
RUN rm /etc/ssl/certs/emSign_Root_CA_-_C1.pem
RUN rm /etc/ssl/certs/emSign_Root_CA_-_G1.pem

ENV SERVER_PORT="8090"
ENV JMX_PORT="9091"
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
        -jar "seb-sps.jar" \
        --spring.config.location=classpath:/config/,optional:file:/sebsps/config/spring/; \

EXPOSE $SERVER_PORT $JMX_PORT