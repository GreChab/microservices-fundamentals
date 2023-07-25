package com.epam.songservice.controller;

import com.epam.songservice.model.Song;
import com.epam.songservice.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping
    public ResponseEntity<Long> addSong(@RequestBody Song song) {
        return ResponseEntity.ok(songService.addSong(song).getId());
    }

    @GetMapping
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.findSongById(id));
    }

    @DeleteMapping
    public ResponseEntity<List<Long>> deleteByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(songService.deleteAllByIds(ids));
    }
}
