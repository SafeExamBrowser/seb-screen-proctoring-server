package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.SnowballObject;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.messages.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


@Lazy
@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3_RDBMS')")
public class S3DAO {

    //todo: specify bucket logic: https://jira.let.ethz.ch/browse/SEBSP-108
    private final String BUCKET_NAME = "sebserver-dev";

    private static final Logger log = LoggerFactory.getLogger(S3DAO.class);

    private final Environment environment;

    private MinioClient minioClient;

    public S3DAO(final Environment environment) {
        this.environment = environment;
    }

    @EventListener(ServiceInitEvent.class)
    public void init() {

        //todo: maybe add error handling so the server does not start app when connection to S3 service cannot be granted
        this.minioClient =
                MinioClient.builder()
                        .endpoint(this.environment.getProperty("sps.s3.endpointUrl"))
                        .credentials(this.environment.getProperty("sps.s3.accessKey"), this.environment.getProperty("sps.s3.secretKey"))
                        .build();
    }


    public Result<InputStream> getItem(final String sessionUUID, final Long pk) {
        return Result.tryCatch(() ->
                this.minioClient.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(BUCKET_NAME)
                                .object(sessionUUID + Constants.UNDERLINE + pk)
                                .build())
        );
    }

    public Result<ObjectWriteResponse> uploadItem(final ByteArrayInputStream screenshotInputStream, final String sessionUUID, final Long pk){
        return Result.tryCatch(() ->
            this.minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(sessionUUID + Constants.UNDERLINE + pk)
                                //todo: discuss, if the size of the stream should be added
                                // (but the stream has to be read for this): https://min.io/docs/minio/linux/developers/java/API.html#putobject-putobjectargs-args
                                .stream(screenshotInputStream, -1, 10485760)
                                .build())
        );
    }

    public Result<ObjectWriteResponse> uploadItemBatch(final List<SnowballObject> batchItems){
        return Result.tryCatch(() ->
                this.minioClient.uploadSnowballObjects(
                        UploadSnowballObjectsArgs.builder()
                                .bucket(BUCKET_NAME)
                                .objects(batchItems)
                                .build())
        );
    }

    public void printAllBucketsInService()  {
        try{
            List<Bucket> bucketList = this.minioClient.listBuckets();
            log.info("**************S3 Buckets**************");
            for (Bucket bucket : bucketList) {
                log.info("Bucket: " + bucket.creationDate() + ", " + bucket.name());
            }
            log.info("**************************************");

        }catch(Exception e){
            log.error("error printing buckets in S3 service {}", e.getMessage());
        }

    }


}