package com.epam.resourceservice.service;

import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.model.ResourceEntity;
import com.epam.resourceservice.model.StorageType;
import com.epam.resourceservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final AmazonS3Service s3Service;
    private final KafkaService kafkaService;

    public ResourceEntity addResource(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ResourceEntity resourceEntity = s3Service.saveFileToStaging(file, file.getOriginalFilename());
        log.info("Resource service: File '" + file.getOriginalFilename() + "' was saved in staging bucket");
        resourceEntity.setStorageType(StorageType.STAGING);
        ResourceEntity savedResourceEntity = resourceRepository.save(resourceEntity);
        kafkaService.sendMessage(String.valueOf(savedResourceEntity.getId()));
        log.info("Kafka service: message sent, id=" + savedResourceEntity.getId());
        return savedResourceEntity;
    }

    public Long processResource(Long id) {
        resourceRepository.findById(id)
                .ifPresent(resourceEntity -> {
                    s3Service.processToPermBucket(resourceEntity.getFileName());
                    S3Object s3Object = s3Service.getFileFromPerm(resourceEntity.getFileName());
                    resourceEntity.setFileUrl(s3Object.getObjectContent().getHttpRequest().getURI().toString());
                    resourceEntity.setStorageType(StorageType.PERMANENT);
                    resourceRepository.save(resourceEntity);
                    log.info("Resource service: File '" + resourceEntity.getFileName() + "' was moved to perm bucket");
                }
        );
        return id;
    }

    @SneakyThrows
    public byte[] getResource(Long id) {
        ResourceEntity resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        S3Object s3Object = s3Service.getFileFromStaging(resource.getFileName());
        return s3Object.getObjectContent().readAllBytes();
    }

    public List<Long> deleteAllByIds(List<Long> ids) {
        List<ResourceEntity> resourceEntities = resourceRepository.findAllById(ids);

        resourceEntities.stream()
                .map(ResourceEntity::getFileName)
                .forEach(s3Service::removeFile);

        List<Long> foundIds = resourceEntities.stream()
                .map(ResourceEntity::getId)
                .collect(Collectors.toList());
        resourceRepository.deleteAllById(foundIds);
        return foundIds;
    }
}
