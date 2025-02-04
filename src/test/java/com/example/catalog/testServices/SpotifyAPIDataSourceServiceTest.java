package com.example.catalog.testServices;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import com.example.catalog.services.SpotifyAPIDataSourceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpotifyAPIDataSourceServiceTest {

    @InjectMocks
    private SpotifyAPIDataSourceService spotifyService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final String sampleArtistId = "0TnOYISbd1XYRBk9myaseg";
    private final String sampleArtistJson = "{ \"id\": \"0TnOYISbd1XYRBk9myaseg\", \"name\": \"Pitbull\" }";

    private final String sampleAlbumId = "4yP0hdKOZPNshxUOjY0cZj"; // Example Album ID
    private final String sampleAlbumJson = "{ \"id\": \"4yP0hdKOZPNshxUOjY0cZj\", \"name\": \"After Hours\" }";

    private final String sampleSongId = "0VjIjW4GlUZAMYd2vXMi3b"; // Example Song ID
    private final String sampleSongJson = "{ \"id\": \"0VjIjW4GlUZAMYd2vXMi3b\", \"name\": \"Blinding Lights\" }";


    @BeforeEach
    void setUp() {
        // Ensure the service is correctly initialized with the mocks
        spotifyService = new SpotifyAPIDataSourceService();
    }

    @Test
    void getArtistById_Success() throws IOException {
        JsonNode mockResponse = new ObjectMapper().readTree(sampleArtistJson);
        Artist expectedArtist = new Artist();
        expectedArtist.setId("0TnOYISbd1XYRBk9myaseg");
        expectedArtist.setName("Pitbull");

        lenient().when(restTemplate.exchange(
                        contains("/artists/" + sampleArtistId),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(),
                        eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleArtistJson, HttpStatus.OK));

        lenient().when(objectMapper.readTree(sampleArtistJson)).thenReturn(mockResponse);
        lenient().when(objectMapper.treeToValue(mockResponse, Artist.class)).thenReturn(expectedArtist);

        Artist artist = spotifyService.getArtistById(sampleArtistId);

        assertNotNull(artist, "Artist should not be null");
        assertEquals(expectedArtist.getId(), artist.getId(), "Artist ID should match");
        assertEquals(expectedArtist.getName(), artist.getName(), "Artist name should match");
    }

    @Test
    void getAlbumById_Success() throws IOException {
        JsonNode mockResponse = new ObjectMapper().readTree(sampleAlbumJson);
        Album expectedAlbum = new Album();
        expectedAlbum.setId(sampleAlbumId);
        expectedAlbum.setName("After Hours");

        lenient().when(restTemplate.exchange(
                        eq("https://api.spotify.com/v1/albums/" + sampleAlbumId),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(),
                        eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleAlbumJson, HttpStatus.OK));

        lenient().when(objectMapper.readTree(sampleAlbumJson)).thenReturn(mockResponse);
        lenient().when(objectMapper.treeToValue(mockResponse, Album.class)).thenReturn(expectedAlbum);

        Album album = spotifyService.getAlbumById(sampleAlbumId);

        assertNotNull(album);
        assertEquals(expectedAlbum.getId(), album.getId());
        assertEquals(expectedAlbum.getName(), album.getName());
    }


    @Test
    void getSongById_Success() throws IOException {
        JsonNode mockResponse = new ObjectMapper().readTree(sampleSongJson);
        Song expectedSong = new Song();
        expectedSong.setId(sampleSongId);
        expectedSong.setName("Blinding Lights");

        lenient().when(restTemplate.exchange(
                        eq("https://api.spotify.com/v1/tracks/" + sampleSongId),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(),
                        eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleSongJson, HttpStatus.OK));

        lenient().when(objectMapper.readTree(sampleSongJson)).thenReturn(mockResponse);
        lenient().when(objectMapper.treeToValue(mockResponse, Song.class)).thenReturn(expectedSong);

        Song song = spotifyService.getSongById(sampleSongId);

        assertNotNull(song);
        assertEquals(expectedSong.getId(), song.getId());
        assertEquals(expectedSong.getName(), song.getName());
    }

    @Test
    void getTracksByAlbum_Success() throws IOException {
        String responseJson = "{ \"items\": [" + sampleSongJson + "] }";
        JsonNode mockResponse = new ObjectMapper().readTree(responseJson);

        lenient().when(restTemplate.exchange(
                        contains("/albums/" + sampleAlbumId + "/tracks"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(),
                        eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseJson, HttpStatus.OK));

        lenient().when(objectMapper.readTree(responseJson)).thenReturn(mockResponse);
        lenient().when(objectMapper.treeToValue(mockResponse.get("items"), Track[].class)).thenReturn(new Track[]{new Track()});

        List<Track> tracks = spotifyService.getTracksByAlbum(sampleAlbumId);

        assertNotNull(tracks);
        assertFalse(tracks.isEmpty());
    }
}



/*import com.example.catalog.model.Artist;
import com.example.catalog.model.Album;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import com.example.catalog.services.SpotifyAPIDataSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpotifyAPIDataSourceServiceTest {

    @Autowired
    private SpotifyAPIDataSourceService spotifyAPIDataSourceService;

    @Test
    public void testGetArtistById() throws IOException { //
        Artist artist = spotifyAPIDataSourceService.getArtistById("0TnOYISbd1XYRBk9myaseg");
        assertNotNull(artist);
        assertEquals("Pitbull", artist.getName());
    }

    @Test
    public void testGetAlbumsByArtist() throws IOException { //
        List<Album> albums = spotifyAPIDataSourceService.getAlbumsByArtist("0TnOYISbd1XYRBk9myaseg");
        assertNotNull(albums);
        assertFalse(albums.isEmpty());
    }

    @Test
    public void testGetAlbumById() throws IOException { //
        Album album = spotifyAPIDataSourceService.getAlbumById("4aawyAB9vmqN3uQ7FjRGTy"); // After Hours
        assertNotNull(album);
        assertEquals("Global Warming", album.getName());
    }

    @Test
    public void testGetSongsByArtist() throws IOException { //
        List<Song> songs = spotifyAPIDataSourceService.getSongsByArtist("0TnOYISbd1XYRBk9myaseg");
        assertNotNull(songs);
    }

    @Test
    public void testGetTracksByAlbum() throws IOException {
        List<Track> tracks = spotifyAPIDataSourceService.getTracksByAlbum("4aawyAB9vmqN3uQ7FjRGTy");
        assertNotNull(tracks);
        assertFalse(tracks.isEmpty());
        assertTrue(tracks.stream().anyMatch(track -> track.getName() != null));
    }

    @Test
    public void testGetSongById() throws IOException {
        // Given a valid track (song) ID from Spotify
        String trackId = "3n3Ppam7vgaVa1iaRUc9Lp";

        // When calling the getSongById method
        Song song = spotifyAPIDataSourceService.getSongById(trackId);

        // Then the result should not be null
        assertNotNull(song);

        // The song should have a name and valid details
        assertNotNull(song.getName());
        assertNotNull(song.getDurationMs());
        assertTrue(song.getDurationMs() > 0);
    }
}*/
