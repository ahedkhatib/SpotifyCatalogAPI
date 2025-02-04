package com.example.catalog.controller;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
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
@RequestMapping("/artists")
public class ArtistController {

    private final DataSourceService dataSourceService;

    @Autowired
    public ArtistController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists() throws IOException {
        try {
            List<Artist> artists = dataSourceService.getAllArtists();
            return ResponseEntity.ok(artists);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable String id) throws IOException {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Artist artist = dataSourceService.getArtistById(id);
            return (artist != null) ? ResponseEntity.ok(artist) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable String id) throws IOException {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            List<Album> albums = dataSourceService.getAlbumsByArtist(id);
            return !albums.isEmpty() ? ResponseEntity.ok(albums) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Song>> getSongsByArtist(@PathVariable String id) throws IOException {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            List<Song> songs = dataSourceService.getSongsByArtist(id);
            return !songs.isEmpty() ? ResponseEntity.ok(songs) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> addArtist(@RequestBody Artist artist) {
        try {
            dataSourceService.addArtist(artist);
            return ResponseEntity.status(HttpStatus.CREATED).body("Artist created successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add artist.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateArtist(@PathVariable String id, @RequestBody Artist artist) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid artist ID format.");
            }

            dataSourceService.updateArtist(id, artist);
            return ResponseEntity.ok("Artist updated successfully.");
        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artist not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArtist(@PathVariable String id) {
        try {
            if (!SpotifyUtils.isValidId(id)) {
                return ResponseEntity.badRequest().body("Invalid artist ID format.");
            }

            dataSourceService.deleteArtist(id);
            return ResponseEntity.ok("Artist deleted successfully.");

        } catch (IOException e) {
            if (!e.getMessage().contains("Spotify")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artist not found.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Spotify API error occurred.");
        }
    }
}