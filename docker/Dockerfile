FROM eclipse-temurin:21-jre

# remove all eMudhra trusts until the JDK vendor ships a fix
RUN keytool -delete -noprompt -trustcacerts -cacerts -alias "cn_emsign_root_ca__g1,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]" -storepass changeit

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