package com.epam.storageservice;

import com.epam.storageservice.model.Storage;
import com.epam.storageservice.model.StorageType;
import com.epam.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
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

}
