package com.epam.resourceservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AwsS3Config {
    @Value("${config.aws.s3.access-key}")
    private String accessKey;
    @Value("${config.aws.s3.secret-key}")
    private String secretKey;
    @Value("${config.aws.s3.url}")
    private String url;
    @Value("${config.aws.region}")
    private String region;
    @Value("${config.aws.s3.bucket-name")
    private String bucketName;

    @Bean
    public AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, region))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
