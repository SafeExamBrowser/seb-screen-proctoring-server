FROM eclipse-temurin:25-jre

ENV SERVER_PORT="8090"

#just try wihtout user to check if we habe a permission problem
RUN groupadd --system spring && useradd --system --gid spring spring && mkdir /sebsps && chown spring:spring /sebsps
USER spring:spring

COPY seb-sps.jar /sebsps/

WORKDIR /sebsps

CMD exec java \
        -Xms${JAVA_HEAP_MIN} \
        -Xmx${JAVA_HEAP_MAX} \
        -XX:-UseBASE64Intrinsics \
        -jar "seb-sps.jar" \
        --spring.config.location=classpath:/config/,optional:file:/sebsps/config/spring/; \

EXPOSE $SERVER_PORT