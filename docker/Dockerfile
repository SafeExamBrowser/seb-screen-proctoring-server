FROM eclipse-temurin:21-jre

# Temporary: remove eMudhra trusts until the JDK vendor ships a fix
RUN rm /etc/ssl/certs/emSign_ECC_Root_CA_-_C3.pem
RUN rm /etc/ssl/certs/emSign_Root_CA_-_C1.pem
RUN rm /etc/ssl/certs/emSign_Root_CA_-_G1.pem
RUN keytool -delete -noprompt -trustcacerts -cacerts -alias "cn_emsign_ecc_root_ca__c3,o_emudhra_inc,ou_emsign_pki,c_us [jdk]" -storepass changeit
RUN keytool -delete -noprompt -trustcacerts -cacerts -alias "cn_emsign_ecc_root_ca__g3,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]" -storepass changeit
RUN keytool -delete -noprompt -trustcacerts -cacerts -alias "cn_emsign_root_ca__c1,o_emudhra_inc,ou_emsign_pki,c_us [jdk]" -storepass changeit
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