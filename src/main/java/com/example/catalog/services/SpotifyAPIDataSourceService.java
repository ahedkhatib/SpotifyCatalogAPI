package com.example.catalog.services;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SpotifyAPIDataSourceService implements DataSourceService {

    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1/";

    /*@Value("${spotify.api.token}")
    private String accessToken;*/
    private final String accessToken = "BQAFOOpi0SmthrTd-hN57Oc4m8F-dgcqnLSCT2xe1gcEXMUp_rOqavZqN8bV9PrRr3ZZPCwp7QPnnKn_dKudsA_HmjQeN_Lfc0INeCeCOYuMhEheRHeDbSrG3tFbm-ZU5GOiB7Cq2YU";


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SpotifyAPIDataSourceService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    private JsonNode makeSpotifyRequest(String endpoint) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(SPOTIFY_API_BASE_URL + endpoint, HttpMethod.GET, entity, String.class);

        return objectMapper.readTree(response.getBody());
    }


    @Override
    public Artist getArtistById(String id) throws IOException {
        System.out.println(accessToken);
        try {
            JsonNode artistNode = makeSpotifyRequest("artists/" + id);
            return objectMapper.treeToValue(artistNode, Artist.class);
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw new IOException("Error fetching artist: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Artist> getAllArtists() throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot retrieve all artists from Spotify.");
    }

    @Override
    public List<Album> getAlbumsByArtist(String artistId) throws IOException {
        if (artistId == null || artistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid artist ID: Artist ID cannot be null or empty.");
        }

        try {
            JsonNode albumsNode = makeSpotifyRequest("artists/" + artistId + "/albums");

            if (albumsNode == null || !albumsNode.has("items")) {
                throw new IOException("Failed to retrieve albums: No data returned from Spotify API.");
            }

            List<Album> albums = Arrays.asList(objectMapper.treeToValue(albumsNode.get("items"), Album[].class));

            if (albums.isEmpty()) {
                throw new IOException("No albums found for artist with ID: " + artistId);
            }

            return albums;

        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                throw new IOException("Artist not found: No artist exists with ID: " + artistId);
            }
            throw new IOException("Failed to retrieve albums: Spotify API error.");
        }
    }

    @Override
    public List<Song> getSongsByArtist(String artistId) throws IOException {
        if (artistId == null || artistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid artist ID: Artist ID cannot be null or empty.");
        }

        try {
            List<Album> albums = getAlbumsByArtist(artistId);

            if (albums.isEmpty()) {
                throw new IOException("No albums found for artist with ID: " + artistId);
            }

            List<Song> songs = new ArrayList<>();

            for (Album album : albums) {
                try {
                    JsonNode tracksNode = makeSpotifyRequest("albums/" + album.getId() + "/tracks");

                    if (tracksNode == null || !tracksNode.has("items")) {
                        continue;
                    }

                    List<Song> albumSongs = Arrays.asList(objectMapper.treeToValue(tracksNode.get("items"), Song[].class));
                    songs.addAll(albumSongs);

                } catch (IOException e) {
                    // Log error but continue processing other albums
                    System.err.println("Failed to retrieve tracks for album ID: " + album.getId());
                }
            }

            // If no songs found, return an appropriate error
            if (songs.isEmpty()) {
                throw new IOException("No songs found for artist with ID: " + artistId);
            }

            return songs;

        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                throw new IOException("Artist not found: No artist exists with ID: " + artistId);
            }
            throw new IOException("Failed to retrieve songs: Spotify API error.");
        }
    }

    @Override
    public void addArtist(Artist artist) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot add a new artist to Spotify.");
    }

    @Override
    public void updateArtist(String id, Artist artist) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot update an existing artist on Spotify.");
    }

    @Override
    public void deleteArtist(String id) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot delete an artist from Spotify.");
    }

    @Override
    public List<Album> getAllAlbums() throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot retrieve all albums from Spotify.");
    }

    @Override
    public Album getAlbumById(String id) throws IOException {
        try {
            JsonNode albumNode = makeSpotifyRequest("albums/" + id);
            return objectMapper.treeToValue(albumNode, Album.class);
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw new IOException("Error fetching album: " + e.getMessage(), e);
        }
    }

    @Override
    public void addAlbum(Album album) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot add a new album to Spotify.");
    }

    @Override
    public void updateAlbum(String id, Album album) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot update an existing album on Spotify.");
    }

    @Override
    public void deleteAlbum(String id) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot delete an album from Spotify.");
    }

    @Override
    public List<Track> getTracksByAlbum(String albumId) throws IOException { /// ////
        try {
            JsonNode tracksNode = makeSpotifyRequest("albums/" + albumId + "/tracks");

            if (tracksNode == null || !tracksNode.has("items") || tracksNode.get("items").isEmpty()) {
                throw new IOException("No tracks found for album ID: " + albumId);
            }

            return Arrays.asList(objectMapper.treeToValue(tracksNode.get("items"), Track[].class));

        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();

            if (status == HttpStatus.NOT_FOUND) {
                throw new IOException("Album not found: " + albumId);
            }

            if (status == HttpStatus.UNAUTHORIZED) {
                throw new IOException("Unauthorized access - Invalid or expired token.");
            }

            throw new IOException("Spotify API error: " + e.getMessage());
        }
    }

    @Override
    public void addTrackToAlbum(String albumId, Track track) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot add a new track to Spotify.");
    }

    @Override
    public void updateTrackInAlbum(String albumId, String trackId, Track track) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot update a track in Spotify.");
    }

    @Override
    public void deleteTrackFromAlbum(String albumId, String trackId) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot update a track in Spotify.");
    }

    @Override
    public List<Song> getAllSongs() throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot retrieve all songs from Spotify.");
    }

    @Override
    public Song getSongById(String id) throws IOException {
        try {
            JsonNode trackNode = makeSpotifyRequest("tracks/" + id);
            return objectMapper.treeToValue(trackNode, Song.class);
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw new IOException("Error fetching song: " + e.getMessage(), e);
        }
    }

    @Override
    public void addSong(Song song) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot add a new song to Spotify.");
    }

    @Override
    public void updateSong(String id, Song song) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot update a song in Spotify.");
    }

    @Override
    public void deleteSong(String id) throws IOException {
        throw new UnsupportedOperationException("Operation not supported: Cannot delete a song from Spotify.");
    }

}
