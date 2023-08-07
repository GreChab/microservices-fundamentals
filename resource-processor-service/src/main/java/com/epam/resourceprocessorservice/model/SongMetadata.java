package com.epam.resourceprocessorservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class SongMetadata {
    private Long id;
    private String name;
    private String artist;
    private String album;
    private String length;
    private Integer resourceId;
    private Integer year;
}
