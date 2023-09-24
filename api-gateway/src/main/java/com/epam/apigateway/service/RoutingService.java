package com.epam.apigateway.service;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class RoutingService {
    private final EurekaClient eurekaClient;

    @Value("${resource.service.name}")
    private String resourceServiceName;

    @Value("${song.service.name}")
    private String songServiceName;

    @Value("${storage.service.name}")
    private String storageServiceName;


    public RoutingService(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    private String getServer(String serverName) {
        return eurekaClient.getNextServerFromEureka(serverName, false).getHomePageUrl();
    }

    @Bean
    @DependsOn("eurekaClient")
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .method(HttpMethod.GET)
                        .and()
                        .path("/resources/*")
                        .uri(getServer(resourceServiceName) + "resources/*"))
                .route(p -> p
                        .method(HttpMethod.POST)
                        .or()
                        .method(HttpMethod.DELETE)
                        .and()
                        .path("/resources")
                        .uri(getServer(resourceServiceName) + "resources"))
                .route(p -> p
                        .method(HttpMethod.GET)
                        .and()
                        .path("/processing/*")
                        .uri(getServer(resourceServiceName) + "processing/*"))
                .route(p -> p
                        .method(HttpMethod.GET)
                        .and()
                        .path("/songs/*")
                        .uri(getServer(songServiceName) + "songs/*"))
                .route(p -> p
                        .method(HttpMethod.POST)
                        .or()
                        .method(HttpMethod.DELETE)
                        .and()
                        .path("/songs")
                        .uri(getServer(songServiceName) + "songs"))
                .route(p -> p
                        .method(HttpMethod.GET)
                        .and()
                        .path("/storages")
                        .uri(getServer(songServiceName) + "storages"))
                .build();
    }

}
