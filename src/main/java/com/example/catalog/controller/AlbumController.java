package com.example.catalog.controller;

import com.example.catalog.model.Album;
import com.example.catalog.model.Track;
import com.example.catalog.services.DataSourceService;
import com.example.catalog.utils.SpotifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final DataSourceService dataSourceService;

    @Autowired
    public AlbumController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() throws IOException {
        try {
            return ResponseEntity.ok(dataSourceService.getAllAlbums());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable String id) throws IOException {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Album album = dataSourceService.getAlbumById(id);
            return (album != null) ? ResponseEntity.ok(album) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addAlbum(@RequestBody Album album) {
        try {
            dataSourceService.addAlbum(album);
            return ResponseEntity.status(HttpStatus.CREATED).body("Album created successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add album.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAlbum(@PathVariable String id, @RequestBody Album album) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid album ID format.");
            }

            dataSourceService.updateAlbum(id, album);
            return ResponseEntity.ok("Album updated successfully.");

        } catch (IOException e) {
            // Check if the error is due to the album not being found (404)
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found.");
            }
            // Otherwise, it's a general internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable String id) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid album ID format.");
            }

            dataSourceService.deleteAlbum(id);
            return ResponseEntity.ok("Album deleted successfully.");

        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<List<Track>> getTracksByAlbum(@PathVariable String id) throws IOException {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().build();
            }

            List<Track> tracks = dataSourceService.getTracksByAlbum(id);
            return (!tracks.isEmpty()) ? ResponseEntity.ok(tracks) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/tracks")
    public ResponseEntity<String> addTrackToAlbum(@PathVariable String id, @RequestBody Track track) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid album ID format.");
            }

            dataSourceService.addTrackToAlbum(id, track);
            return ResponseEntity.status(HttpStatus.CREATED).body("Track added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found.");
        }
    }

    @PutMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<String> updateTrack(@PathVariable String id, @PathVariable String trackId, @RequestBody Track track) {
        try {
            if (!SpotifyUtils.isValidId(id) || !SpotifyUtils.isValidId(trackId)) {
                return ResponseEntity.badRequest().body("Invalid album or track ID format.");
            }

            dataSourceService.updateTrackInAlbum(id, trackId, track);
            return ResponseEntity.ok("Track updated successfully.");
        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Track not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<String> deleteTrack(@PathVariable String id, @PathVariable String trackId) {
        try {
            if (!SpotifyUtils.isValidId(id) || !SpotifyUtils.isValidId(trackId)) {
                return ResponseEntity.badRequest().body("Invalid album or track ID format.");
            }

            dataSourceService.deleteTrackFromAlbum(id, trackId);
            return ResponseEntity.ok("Track deleted successfully.");

        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Track not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }
}
