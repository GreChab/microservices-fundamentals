package com.epam.storageservice;

import com.epam.storageservice.model.Storage;
import com.epam.storageservice.model.StorageType;
import com.epam.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class StorageServiceApplication implements CommandLineRunner {

    private final StorageRepository storageRepository;

    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Storage storage1 = new Storage(1L, StorageType.PERMANENT, "permanent-bucket", "/permanent_bucket");
        Storage storage2 = new Storage(2L, StorageType.STAGING, "staging-bucket", "/staging_bucket");
        storageRepository.save(storage1);
        storageRepository.save(storage2);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers( "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/storages").permitAll()
                        .requestMatchers(HttpMethod.POST, "/storages").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/storages").hasRole("ADMIN")
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
            return roles.stream()
                    .map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
