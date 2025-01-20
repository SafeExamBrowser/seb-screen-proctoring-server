package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import io.minio.BucketExistsArgs;
import io.minio.GetBucketLifecycleArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.SetBucketLifecycleArgs;
import io.minio.SnowballObject;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;


@Lazy
@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('S3_RDBMS')")
public class S3DAO {

    private static final Logger log = LoggerFactory.getLogger(S3DAO.class);

    private final Environment environment;

    private MinioClient minioClient;

    private String BUCKET_NAME;

    public S3DAO(final Environment environment) {
        this.environment = environment;
    }

    @EventListener(ServiceInitEvent.class)
    public void init() throws Exception {

        BUCKET_NAME = this.environment.getProperty("sps.s3.bucketName", "sps.s3.defaultBucketName");
        this.minioClient = createMinioClient();

        printAllBucketsInService();

        if(!isBucketExisting()){
            createBucket();
//            getBucketLifecycle();
        }
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
                                .stream(screenshotInputStream, screenshotInputStream.available(), -1)
                                .build())
        );
    }

    public Result<ObjectWriteResponse> uploadItemBatch(final List<SnowballObject> batchItems){
        return Result.tryCatch(() ->
                this.minioClient.uploadSnowballObjects(
                        UploadSnowballObjectsArgs
                                .builder()
                                .bucket(BUCKET_NAME)
                                .objects(batchItems)
                                .build())
        );
    }

    public void deleteItemBatch(final List<DeleteObject> batchItems) throws Exception{
        Iterable<io.minio.Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs
                                .builder()
                                .bucket(BUCKET_NAME)
                                .objects(batchItems)
                                .build());

        for (io.minio.Result<DeleteError> result : results) {
            DeleteError error = result.get();
            log.info("Error in deleting object {}, {}", error.objectName(), error.message());
        }
    }

    private boolean isBucketExisting() throws Exception {
        try{
            return this.minioClient.bucketExists(
                    BucketExistsArgs
                            .builder()
                            .bucket(BUCKET_NAME)
                            .build());

        }catch(Exception e){
            throw new Exception("Could not retrieve status of bucket: " + BUCKET_NAME, e);
        }
    }

    private void createBucket() throws Exception {
        try{
            this.minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(BUCKET_NAME)
                            .region("HAD")
                            .build());

            log.info("Bucket {} created successfully", BUCKET_NAME);

        }catch(Exception e){
            throw new Exception("Failed to create bucket: " + BUCKET_NAME, e);
        }
    }

    private void printAllBucketsInService(){
        try{
            List<Bucket> bucketList = this.minioClient.listBuckets();
            log.info("**************S3 Buckets**************");
            for (Bucket bucket : bucketList) {
                log.info("Bucket: " + bucket.creationDate() + ", " + bucket.name());
            }
            log.info("**************************************");

        }catch(Exception e){
            log.error("Error printing buckets in S3 service {}", e.getMessage());
        }

    }

    private void getBucketLifecycle(){
        try{
            LifecycleConfiguration config =
                    minioClient.getBucketLifecycle(
                            GetBucketLifecycleArgs.builder().bucket(BUCKET_NAME).build());

        }catch(Exception e){
            log.error("");
        }
    }

    private MinioClient createMinioClient() throws Exception {
        if(this.environment.getProperty("sps.s3.tls.cert") == null){
            return MinioClient.builder()
                    .endpoint(this.environment.getRequiredProperty("sps.s3.endpointUrl"))
                    .credentials(
                            this.environment.getRequiredProperty("sps.s3.accessKey"),
                            this.environment.getRequiredProperty("sps.s3.secretKey")
                    )
                    .build();
        }

        return MinioClient.builder()
                .endpoint(this.environment.getRequiredProperty("sps.s3.endpointUrl"))
                .credentials(
                        this.environment.getRequiredProperty("sps.s3.accessKey"),
                        this.environment.getRequiredProperty("sps.s3.secretKey")
                )
                .httpClient(createOkHttpClientWithCert())
                .build();
    }

    private OkHttpClient createOkHttpClientWithCert() throws Exception {
        String pemCert = this.environment.getRequiredProperty("sps.s3.tls.cert");

        // Convert PEM string to X509Certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(pemCert.getBytes()));

        // Create a KeyStore and load the certificate
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("custom-cert", cert);

        // Initialize TrustManager with the KeyStore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Set up SSLContext using the TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

        // Return the OkHttpClient with SSLContext configured
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                .build();
    }
}