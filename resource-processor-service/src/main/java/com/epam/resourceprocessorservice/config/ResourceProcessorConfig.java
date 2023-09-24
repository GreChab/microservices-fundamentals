package com.epam.resourceprocessorservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ResourceProcessorConfig {
    @Value("${resource.service.name}")
    private String resourceServiceName;
    @Value("${resource.service.path}")
    private String resourceServicePath;
    @Value("${song.service.name}")
    private String songServiceName;
    @Value("${song.service.path}")
    private String songServicePath;
    @Value("${api.gateway.service.name}")
    private String apiGatewayName;
    @Value("${resource.service.processing.path}")
    private String resourceServiceProcessingPath;
}
