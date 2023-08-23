package com.epam.songservice.service;

import com.epam.songservice.model.Song;
import com.epam.songservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;

    public Song addSong(Song song) {
        if (song == null || song.getName().isBlank() || song.getArtist().isBlank() || song.getAlbum().isBlank()
                || song.getLength().isBlank() || song.getYear() == null || song.getResourceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        log.info("Song-service: song received. Id: " + song.getId() +  ", artist: " + song.getArtist() + ", name: " + song.getName());
        return songRepository.save(song);
    }

    public Song findSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Long> deleteAllByIds(List<Long> ids) {
        List<Song> songs = songRepository.findAllById(ids);
        List<Long> foundIds = songs.stream()
                .map(Song::getId)
                .collect(Collectors.toList());
        songRepository.deleteAllById(foundIds);
        return foundIds;
    }
}
