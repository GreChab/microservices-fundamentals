package com.epam.resourceprocessorservice.service;

import com.epam.resourceprocessorservice.config.ResourceProcessorConfig;
import com.epam.resourceprocessorservice.model.SongMetadata;
import com.epam.resourceprocessorservice.processor.Mp3MetadataExtractor;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceProcessorService {
    private RestTemplate restTemplate;
    private EurekaClient eurekaClient;
    private ResourceProcessorConfig config;
    private Mp3MetadataExtractor extractor;

    @KafkaListener(topics = "songs", groupId = "resource.processor")
    public void receive(String id) {
        log.info("Message received from topic: id=" + id);
        byte[] file = restTemplate.getForObject(getResourceServiceUrl() + "/" + id, byte[].class);
        SongMetadata songMetadata = extractor.extractMetadata(file);
        songMetadata.setResourceId(Integer.valueOf(id));
        restTemplate.postForObject(getSongServiceUrl(), songMetadata, SongMetadata.class);
    }

    @RabbitHandler
    public void receiveWithRetry(String id) {
        Decorators.ofRunnable(() -> receive(id))
                .withRetry(retry())
                .run();
    }

    private Retry retry() {
        Retry retry = Retry.of("resourceProcessorService", RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofSeconds(3))
                .build());

        retry.getEventPublisher().onEvent(event -> log.info("Retry event: " + event));
        return retry;
    }


    private String getSongServiceUrl() {
        return eurekaClient.getNextServerFromEureka(config.getApiGatewayName(), false)
                .getHomePageUrl() + config.getSongServicePath();
    }

    private String getResourceServiceUrl() {
        return eurekaClient.getNextServerFromEureka(config.getApiGatewayName(), false)
                .getHomePageUrl() + config.getResourceServicePath();
    }
}
