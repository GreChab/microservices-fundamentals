package com.epam.resourceservice.service;

import ch.qos.logback.core.util.Duration;
import com.epam.resourceservice.dto.Mp3Details;
import com.epam.resourceservice.model.ResourceEntity;
import com.epam.resourceservice.repository.ResourceRepository;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;

    @Autowired
    EurekaClient eurekaClient;

    @Value("${song.service.name}")
    private String songServiceName;

    @Value("${song.service.path}")
    private String songServicePath;


    @SneakyThrows
    public ResourceEntity addResource(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ResourceEntity resourceEntity = ResourceEntity.builder()
                .withAudioData(file.getBytes())
                .build();
        ResourceEntity savedResourceEntity = resourceRepository.save(resourceEntity);
        Mp3Details mp3Details = extractMetadata(file);
        mp3Details.setResourceId(savedResourceEntity.getId());
        restTemplate.postForEntity(getSongServiceUrl(), new HttpEntity<>(mp3Details), String.class);
        return savedResourceEntity;
    }

    public byte[] getResource(Long id) {
        ResourceEntity resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return resource.getAudioData();
    }

    public List<Long> deleteAllByIds(List<Long> ids) {
        List<ResourceEntity> resourceEntities = resourceRepository.findAllById(ids);
        List<Long> foundIds = resourceEntities.stream()
                .map(ResourceEntity::getId)
                .collect(Collectors.toList());
        resourceRepository.deleteAllById(foundIds);
        return foundIds;
    }

    @SneakyThrows
    private Mp3Details extractMetadata(MultipartFile file) {
        Metadata metadata = new Metadata();
        new Mp3Parser().parse(file.getInputStream(), new BodyContentHandler(), metadata, new ParseContext());

        return Mp3Details.builder()
                .withName(metadata.get("dc:title"))
                .withArtist(metadata.get("xmpDM:artist"))
                .withAlbum(metadata.get("xmpDM:album"))
                .withYear(Integer.valueOf(metadata.get("xmpDM:releaseDate")))
                .withLength(formatSecondsToMinutesAndSeconds(metadata.get("xmpDM:duration")))
                .build();
    }

    private static String formatSecondsToMinutesAndSeconds(String s) {
        return DurationFormatUtils.formatDuration(
                Duration.buildBySeconds(Double.parseDouble(s)).getMilliseconds(),
                "mm:ss");
    }


    private String getSongServiceUrl() {
        return eurekaClient.getNextServerFromEureka(songServiceName, false).getHomePageUrl() + songServicePath;
    }
}
