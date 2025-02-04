package com.example.catalog.controller;

import com.example.catalog.model.Song;
import com.example.catalog.services.DataSourceService;
import com.example.catalog.utils.SpotifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final DataSourceService dataSourceService;

    @Autowired
    public SongController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() throws IOException {
        try {
            List<Song> songs = dataSourceService.getAllSongs();
            return songs.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(songs);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable String id) throws IOException {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Song song = dataSourceService.getSongById(id);
            return (song != null) ? ResponseEntity.ok(song) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> addSong(@RequestBody Song song) {
        try {
            dataSourceService.addSong(song);
            return ResponseEntity.status(HttpStatus.CREATED).body("Song created successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add song.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSong(@PathVariable String id, @RequestBody Song song) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid song ID format.");
            }

            dataSourceService.updateSong(id, song);
            return ResponseEntity.ok("Song updated successfully.");
        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Song not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable String id) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid song ID format.");
            }

            dataSourceService.deleteSong(id);
            return ResponseEntity.ok("Song deleted successfully.");
        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Song not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }
}
