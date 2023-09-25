package com.epam.storageservice.controller;

import com.epam.storageservice.model.Storage;
import com.epam.storageservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storages")
public class StorageController {
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<List<Storage>> getStorages() {
        return ResponseEntity.ok(storageService.getStorages());
    }

    @PostMapping
    public ResponseEntity<Long> createStorage(@RequestParam Storage storage) {
        return ResponseEntity.ok(storageService.saveStorage(storage));
    }

    @DeleteMapping
    public ResponseEntity<List<Long>> deleteStorage(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(storageService.deleteStorages(ids));
    }
}
