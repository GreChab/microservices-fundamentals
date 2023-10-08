package com.epam.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.config.AwsS3Config;
import com.epam.resourceservice.faulttolerance.Breaker;
import com.epam.resourceservice.model.ResourceEntity;
import com.epam.resourceservice.model.Storage;
import com.epam.resourceservice.model.StorageType;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.decorators.Decorators;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Arrays;

@Service
public class AmazonS3Service {
    private String stagingBucketName;
    private String permanentBucketName;

    private final AmazonS3 s3Client;
    private final AwsS3Config awsS3Config;
    private final RestTemplate restTemplate;
    private final EurekaClient eurekaClient;
    private final Breaker breaker;

    @Autowired
    public AmazonS3Service(AmazonS3 s3Client, AwsS3Config awsS3Config, RestTemplate restTemplate,
                           EurekaClient eurekaClient, Breaker breaker) {
        this.s3Client = s3Client;
        this.awsS3Config = awsS3Config;
        this.restTemplate = restTemplate;
        this.eurekaClient = eurekaClient;
        this.breaker = breaker;
    }

    @SneakyThrows
    public ResourceEntity saveFileToStaging(MultipartFile file, String fileName) {
        Decorators.ofRunnable(this::createBuckets)
                .withCircuitBreaker(breaker.circuitBreaker())
                .run();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        s3Client.putObject(stagingBucketName, fileName, file.getInputStream(), objectMetadata);
        return ResourceEntity.builder()
                .withFileName(fileName)
                .withFileUrl(getUrl(fileName).toString())
                .build();
    }

    public void processToPermBucket(String fileName) {
        s3Client.copyObject(stagingBucketName, fileName, permanentBucketName, fileName);
        s3Client.deleteObject(stagingBucketName, fileName);
    }

    @SneakyThrows
    public S3Object getFile(String fileName) {
        return s3Client.getObject(stagingBucketName, fileName);
    }

    public void removeFile(String fileName) {
        s3Client.deleteObject(permanentBucketName, fileName);
    }

    private URL getUrl(String fileName) {
        return s3Client.getUrl(stagingBucketName, fileName);
    }

    public void createBuckets() {
        Storage[] storages = restTemplate.getForObject(getStorageServiceUrl(), Storage[].class);
        String stagingBucketName = Arrays.stream(storages)
                .filter(x -> StorageType.STAGING.equals(x.getStorageType()))
                .findFirst()
                .map(Storage::getBucket)
                .orElseThrow(IllegalArgumentException::new);
        String permanentBucketName = Arrays.stream(storages)
                .filter(x -> StorageType.PERMANENT.equals(x.getStorageType()))
                .findFirst()
                .map(Storage::getBucket)
                .orElseThrow(IllegalArgumentException::new);
        if (!s3Client.doesBucketExistV2(stagingBucketName)) {
            s3Client.createBucket(stagingBucketName);
            this.stagingBucketName = stagingBucketName;
        }
        if (!s3Client.doesBucketExistV2(permanentBucketName)) {
            s3Client.createBucket(permanentBucketName);
            this.permanentBucketName = permanentBucketName;
        }
    }

    private String getStorageServiceUrl() {
        return eurekaClient.getNextServerFromEureka(awsS3Config.getApiGatewayServiceName(), false)
                .getHomePageUrl() + awsS3Config.getStorageServicePath();
    }
}
