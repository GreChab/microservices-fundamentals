package com.epam.resourceservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="t_storage")
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    StorageType storageType;
    String bucket;
    String path;
}
