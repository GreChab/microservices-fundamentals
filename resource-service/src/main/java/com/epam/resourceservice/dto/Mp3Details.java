package com.epam.resourceservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(setterPrefix = "with")
@Getter
@Setter
public class Mp3Details {
    private String name;
    private String artist;
    private String album;
    private String length;
    private Integer year;
    private Long resourceId;
}

