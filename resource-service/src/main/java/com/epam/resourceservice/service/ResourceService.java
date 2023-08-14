package com.epam.resourceservice.service;

import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.model.ResourceEntity;
import com.epam.resourceservice.repository.ResourceRepository;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final AmazonS3Service s3Service;
    private final EurekaClient eurekaClient;

    @Value("${song.service.name}")
    private String songServiceName;

    @Value("${song.service.path}")
    private String songServicePath;

    @SneakyThrows
    public ResourceEntity addResource(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ResourceEntity resourceEntity = s3Service.saveFile(file, file.getOriginalFilename());
        return resourceRepository.save(resourceEntity);
    }

    @SneakyThrows
    public byte[] getResource(Long id) {
        ResourceEntity resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        S3Object s3Object = s3Service.getFile(resource.getFileName());
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

    private String getSongServiceUrl() {
        return eurekaClient.getNextServerFromEureka(songServiceName, false).getHomePageUrl() + songServicePath;
    }
}
