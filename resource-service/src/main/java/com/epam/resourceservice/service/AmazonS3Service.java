package com.epam.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.config.AwsS3Config;
import com.epam.resourceservice.model.ResourceEntity;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@Service
public class AmazonS3Service {
    private final AmazonS3 s3Client;
    private final AwsS3Config awsS3Config;

    @Autowired
    public AmazonS3Service(AmazonS3 s3Client, AwsS3Config awsS3Config) {
        this.s3Client = s3Client;
        this.awsS3Config = awsS3Config;
    }

    @SneakyThrows
    public ResourceEntity saveFile(MultipartFile file, String fileName) {
        if (!s3Client.doesBucketExistV2(awsS3Config.getBucketName())) {
            s3Client.createBucket(awsS3Config.getBucketName());
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        s3Client.putObject(awsS3Config.getBucketName(), fileName, file.getInputStream(), objectMetadata);
        return ResourceEntity.builder()
                .withFileName(fileName)
                .withFileUrl(getUrl(fileName).toString())
                .build();
    }

    @SneakyThrows
    public S3Object getFile(String fileName) {
        return s3Client.getObject(awsS3Config.getBucketName(), fileName);
    }

    public void removeFile(String fileName) {
        s3Client.deleteObject(awsS3Config.getBucketName(), fileName);
    }

    private URL getUrl(String fileName) {
        return s3Client.getUrl(awsS3Config.getBucketName(), fileName);
    }
}
