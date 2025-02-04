package com.example.catalog;



import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import com.example.catalog.services.JSONDataSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class JSONDataSourceServiceTest {

    @Autowired
    private JSONDataSourceService jsonDataSourceService;

    @Test
    void testGetArtistById() throws IOException {
        Artist artist = jsonDataSourceService.getArtistById("1Xyo4u8uXC1ZmMpatF05PJ");
        assertNotNull(artist);
        assertEquals("The Weeknd", artist.getName());
    }

    @Test
    void testGetAllArtists() throws IOException {
        List<Artist> artists = jsonDataSourceService.getAllArtists();
        assertNotNull(artists);
        assertFalse(artists.isEmpty());
    }

    @Test
    void testGetAlbumsByArtist() throws IOException {
        List<Album> albums = jsonDataSourceService.getAlbumsByArtist("1Xyo4u8uXC1ZmMpatF05PJ");
        assertNotNull(albums);
        assertFalse(albums.isEmpty());
    }

    @Test
    void testGetSongsByArtist() throws IOException {
        List<Song> songs = jsonDataSourceService.getSongsByArtist("1Xyo4u8uXC1ZmMpatF05PJ");
        assertNotNull(songs);
        assertFalse(songs.isEmpty());
    }

    @Test
    void testAddArtist() throws IOException {
        Artist artist = new Artist();
        artist.setId("test123");
        artist.setName("Test Artist");
        jsonDataSourceService.addArtist(artist);

        Artist fetchedArtist = jsonDataSourceService.getArtistById("test123");
        assertNotNull(fetchedArtist);
        assertEquals("Test Artist", fetchedArtist.getName());
    }

    @Test
    void testUpdateArtist() throws IOException {
        Artist artist = new Artist();
        artist.setId("1Xyo4u8uXC1ZmMpatF05PJ");
        artist.setName("Updated Name");
        jsonDataSourceService.updateArtist("1Xyo4u8uXC1ZmMpatF05PJ", artist);

        Artist updatedArtist = jsonDataSourceService.getArtistById("1Xyo4u8uXC1ZmMpatF05PJ");
        assertNotNull(updatedArtist);
        assertEquals("Updated Name", updatedArtist.getName());
    }

    @Test
    void testDeleteArtist() throws IOException {
        jsonDataSourceService.deleteArtist("6eUKZXaKkcviH0Ku9w2n3V");
        Artist deletedArtist = jsonDataSourceService.getArtistById("6eUKZXaKkcviH0Ku9w2n3V");
        assertNull(deletedArtist);
    }

    @Test
    void testGetAlbumById() throws IOException {
        Album album = jsonDataSourceService.getAlbumById("4yP0hdKOZPNshxUOjY0cZj");
        assertNotNull(album);
        assertEquals("After Hours", album.getName());
    }

    @Test
    void testGetAllAlbums() throws IOException {
        List<Album> albums = jsonDataSourceService.getAllAlbums();
        assertNotNull(albums);
        assertFalse(albums.isEmpty());
    }

    @Test
    void testAddAlbum() throws IOException {
        Album album = new Album("testAlbum", "Test Album", "spotify:album:testAlbum", "2024-02-04", 10, null, null);
        jsonDataSourceService.addAlbum(album);

        Album fetchedAlbum = jsonDataSourceService.getAlbumById("testAlbum");
        assertNotNull(fetchedAlbum);
        assertEquals("Test Album", fetchedAlbum.getName());
    }

    @Test
    void testUpdateAlbum() throws IOException {
        Album album = new Album("5658aM19fA3JVwTK6eQX70", "Updated Album Name", "spotify:album:4yP0hdKOZPNshxUOjY0cZj", "2020-03-20", 14, null, null);
        jsonDataSourceService.updateAlbum("5658aM19fA3JVwTK6eQX70", album);

        Album updatedAlbum = jsonDataSourceService.getAlbumById("5658aM19fA3JVwTK6eQX70");
        assertNotNull(updatedAlbum);
        assertEquals("Updated Album Name", updatedAlbum.getName());
    }

    @Test
    void testDeleteAlbum() throws IOException {
        jsonDataSourceService.deleteAlbum("3T4tUhGYeRNVUGevb0wThu");
        Album deletedAlbum = jsonDataSourceService.getAlbumById("3T4tUhGYeRNVUGevb0wThu");
        assertNull(deletedAlbum);
    }

    @Test
    void testGetSongById() throws IOException {
        Song song = jsonDataSourceService.getSongById("0VjIjW4GlUZAMYd2vXMi3b");
        assertNotNull(song);
        assertEquals("Blinding Lights", song.getName());
    }

    @Test
    void testGetAllSongs() throws IOException {
        List<Song> songs = jsonDataSourceService.getAllSongs();
        assertNotNull(songs);
        assertFalse(songs.isEmpty());
    }

    @Test
    void testGetTracksByAlbum() throws IOException {
        List<Track> tracks = jsonDataSourceService.getTracksByAlbum("5r36AJ6VOJtp00oxSkBZ5h");
        assertNotNull(tracks);
        assertFalse(tracks.isEmpty());
    }

    @Test
    void testAddTrackToAlbum() throws IOException {
        Track track = new Track("5r36AJ6VOJtp00oxSkBZ5r", "New Track", 300000, false, "spotify:track:track456");
        jsonDataSourceService.addTrackToAlbum("5r36AJ6VOJtp00oxSkBZ5h", track);
        Album album = jsonDataSourceService.getAlbumById("5r36AJ6VOJtp00oxSkBZ5h");
        assertNotNull(album);
        assertTrue(album.getTracks().stream().anyMatch(t -> t.getId().equals("5r36AJ6VOJtp00oxSkBZ5r")));
    }

    @Test
    void testUpdateTrackInAlbum() throws IOException {
        Track track = new Track("5LYMamLv12UPbemOaTPyeV", "Updated Track Name", 250000, true, "spotify:track:track123");
        jsonDataSourceService.updateTrackInAlbum("5r36AJ6VOJtp00oxSkBZ5h", "5LYMamLv12UPbemOaTPyeV", track);
        Album album = jsonDataSourceService.getAlbumById("5r36AJ6VOJtp00oxSkBZ5h");
        assertNotNull(album);
        assertTrue(album.getTracks().stream().anyMatch(t -> t.getId().equals("5LYMamLv12UPbemOaTPyeV") && t.getName().equals("Updated Track Name")));
    }

    @Test
    void testDeleteTrackFromAlbum() throws IOException {
        jsonDataSourceService.deleteTrackFromAlbum("35s58BRTGAEWztPo9WqCIs", "5zsHmE2gO3RefVsPyw2e3T");
        Album album = jsonDataSourceService.getAlbumById("35s58BRTGAEWztPo9WqCIs");
        assertNotNull(album);
        assertFalse(album.getTracks().stream().anyMatch(t -> t.getId().equals("5zsHmE2gO3RefVsPyw2e3T")));
    }

    @Test
    void testAddSong() throws IOException {
        Song song = new Song("7qiZfU4dY1lWllzX7mPBI8", "New Song", 210000, 85, "spotify:song:song123", null, null);
        jsonDataSourceService.addSong(song);
        Song fetchedSong = jsonDataSourceService.getSongById("7qiZfU4dY1lWllzX7mPBI8");
        assertNotNull(fetchedSong);
        assertEquals("New Song", fetchedSong.getName());
    }

    @Test
    void testUpdateSong() throws IOException {
        Song song = new Song("7qiZfU4dY1lWllzX7mPBI3", "Updated Song Name", 220000, 90, "spotify:song:0VjIjW4GlUZAMYd2vXMi3b", null, null);
        jsonDataSourceService.updateSong("7qiZfU4dY1lWllzX7mPBI3", song);
        Song updatedSong = jsonDataSourceService.getSongById("7qiZfU4dY1lWllzX7mPBI3");
        assertNotNull(updatedSong);
        assertEquals("Updated Song Name", updatedSong.getName());
    }

    @Test
    void testDeleteSong() throws IOException {
        jsonDataSourceService.deleteSong("7qEHsqek33rTcFNT9PFqLf");
        Song deletedSong = jsonDataSourceService.getSongById("7qEHsqek33rTcFNT9PFqLf");
        assertNull(deletedSong);
    }
}
