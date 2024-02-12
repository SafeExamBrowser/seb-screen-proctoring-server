package ch.ethz.seb.sps.server.servicelayer.impl;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Lazy
@Component
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3')")
public class ScreenshotS3 {

    protected MinioClient minioClient;
    private final Environment environment;

    public ScreenshotS3(final Environment environment){
        this.environment = environment;

        this.minioClient =
                MinioClient.builder()
                        .endpoint(this.environment.getProperty("sps.s3.endpointUrl"))
                        .credentials(this.environment.getProperty("sps.s3.accessKey"), this.environment.getProperty("sps.s3.secretKey"))
                        .build();
    }


}
