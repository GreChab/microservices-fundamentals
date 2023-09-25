package com.epam.resourceservice.controller;

import com.epam.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/resources")
    public ResponseEntity<Long> addResource(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(resourceService.addResource(file).getId());
    }

    @GetMapping("/resources/{id}")
    public ResponseEntity<Resource> getResource(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new ByteArrayResource(resourceService.getResource(id)));
    }

    @GetMapping("/processing/{id}")
    public ResponseEntity<Long> processResource(@PathVariable("id") Long id) {
        return ResponseEntity.ok(resourceService.processResource(id));
    }

    @DeleteMapping("/resources")
    public ResponseEntity<List<Long>> deleteResource(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(resourceService.deleteAllByIds(ids));
    }
}
