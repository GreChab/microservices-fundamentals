package com.epam.songservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="t_songs")
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String artist;
    String album;
    String length;
    @Column(name = "resource_id")
    Long resourceId;
    Long year;
}

