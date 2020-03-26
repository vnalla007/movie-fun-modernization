package org.superbiz.moviefun.albums;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.superbiz.moviefun.blobstore.S3Store;

@SpringBootApplication
public class AlbumServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(AlbumServiceApplication.class, args);
    }

    @Bean
    ServiceCredentials serviceCredentials(@Value("${vcap.services}") String vcapServices) {
        return new ServiceCredentials(vcapServices);
    }

    @Bean
    public S3Store blobStore(
            ServiceCredentials serviceCredentials,
            @Value("${s3.endpointUrl:#{null}}") String s3EndpointUrl
    ) {
        String s3AccessKey = serviceCredentials.getCredential("photo-storage", "access_key_id");
        String s3SecretKey = serviceCredentials.getCredential("photo-storage", "secret_access_key");
        String s3BucketName = serviceCredentials.getCredential("photo-storage", "bucket");

        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        if (s3EndpointUrl != null) {
            s3Client.setEndpoint(s3EndpointUrl);
        }

        return new S3Store(s3Client, s3BucketName);
    }
}