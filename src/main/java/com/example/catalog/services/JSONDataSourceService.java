package com.example.catalog.services;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JSONDataSourceService implements DataSourceService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${json.file.path:data/popular_artists.json}")
    private String jsonFilePath;

    @Value("${json.albums.file.path:data/albums.json}")
    private String albumsJsonFilePath;

    @Value("${json.songs.file.path:data/popular_songs.json}")
    private String songsJsonFilePath;


    @Override
    public Artist getArtistById(String id) throws IOException {
        JsonNode artists = loadJsonData(jsonFilePath);
        JsonNode artistNode = artists.get(id);
        if (artistNode == null) {
            return null;
        }
        return objectMapper.treeToValue(artistNode, Artist.class);
    }

    @Override
    public List<Artist> getAllArtists() throws IOException {
        JsonNode artists = loadJsonData(jsonFilePath);
        List<Artist> artistList = new ArrayList<>();
        artists.fields().forEachRemaining(entry -> {
            try {
                artistList.add(objectMapper.treeToValue(entry.getValue(), Artist.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return artistList;
    }

    @Override
    public List<Album> getAlbumsByArtist(String artistId) throws IOException {
        JsonNode songs = loadJsonData(songsJsonFilePath);
        JsonNode albums = loadJsonData(albumsJsonFilePath);

        Set<String> albumIds = new HashSet<>();
        List<Album> artistAlbums = new ArrayList<>();

        for (JsonNode songNode : songs) {
            JsonNode songArtists = songNode.get("artists");
            for (JsonNode artistNode : songArtists) {
                if (artistNode.get("id").asText().equals(artistId)) {
                    String albumId = songNode.get("album").get("id").asText();
                    albumIds.add(albumId);
                }
            }
        }

        for (String albumId : albumIds) {
            JsonNode albumNode = albums.get(albumId);
            if (albumNode != null) {
                artistAlbums.add(objectMapper.treeToValue(albumNode, Album.class));
            }
        }

        return artistAlbums;
    }

    @Override
    public List<Song> getSongsByArtist(String artistId) throws IOException {
        JsonNode songs = loadJsonData(songsJsonFilePath);
        List<Song> artistSongs = new ArrayList<>();

        for (JsonNode songNode : songs) {
            for (JsonNode artistNode : songNode.get("artists")) {
                if (artistNode.get("id").asText().equals(artistId)) {
                    artistSongs.add(objectMapper.treeToValue(songNode, Song.class));
                }
            }
        }

        return artistSongs;
    }

    @Override
    public void addArtist(Artist artist) throws IOException {
        JsonNode artists = loadJsonData(jsonFilePath);
        ((ObjectNode) artists).set(artist.getId(), objectMapper.valueToTree(artist));
        saveJsonData(jsonFilePath, artists);
    }

    @Override
    public void updateArtist(String id, Artist artist) throws IOException {
        JsonNode artists = loadJsonData(jsonFilePath);
        if (!artists.has(id)) {
            throw new IOException("Artist not found.");
        }
        ((ObjectNode) artists).set(id, objectMapper.valueToTree(artist));
        saveJsonData(jsonFilePath, artists);
    }

    @Override
    public void deleteArtist(String id) throws IOException {
        JsonNode artists = loadJsonData(jsonFilePath);
        if (!artists.has(id)) {
            throw new IOException("Artist not found.");
        }
        ((ObjectNode) artists).remove(id);
        saveJsonData(jsonFilePath, artists);
    }

    @Override
    public List<Album> getAllAlbums() throws IOException {
        JsonNode albums = loadJsonData(albumsJsonFilePath);
        List<Album> albumList = new ArrayList<>();
        albums.fields().forEachRemaining(entry -> {
            try {
                albumList.add(objectMapper.treeToValue(entry.getValue(), Album.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return albumList;
    }

    @Override
    public Album getAlbumById(String id) throws IOException {
        JsonNode albums = loadJsonData(albumsJsonFilePath);
        JsonNode albumNode = albums.get(id);
        return (albumNode != null) ? objectMapper.treeToValue(albumNode, Album.class) : null;
    }

    @Override
    public void addAlbum(Album album) throws IOException {
        JsonNode albums = loadJsonData(albumsJsonFilePath);
        ((ObjectNode) albums).set(album.getId(), objectMapper.valueToTree(album));
        saveJsonData(albumsJsonFilePath, albums);
    }

    @Override
    public void updateAlbum(String id, Album album) throws IOException {
        JsonNode albums = loadJsonData(albumsJsonFilePath);
        if (!albums.has(id)) {
            throw new IOException("Album not found.");
        }
        ((ObjectNode) albums).set(id, objectMapper.valueToTree(album));
        saveJsonData(albumsJsonFilePath, albums);
    }

    @Override
    public void deleteAlbum(String id) throws IOException {
        JsonNode albums = loadJsonData(albumsJsonFilePath);
        if (!albums.has(id)) {
            throw new IOException("Album not found.");
        }
        ((ObjectNode) albums).remove(id);
        saveJsonData(albumsJsonFilePath, albums);
    }

    @Override
    public List<Track> getTracksByAlbum(String albumId) throws IOException {
        Album album = getAlbumById(albumId);
        return (album != null) ? album.getTracks() : new ArrayList<>();
    }

    @Override
    public void addTrackToAlbum(String albumId, Track track) throws IOException {
        Album album = getAlbumById(albumId);
        if (album == null) {
            throw new IOException("Album not found.");
        }
        album.getTracks().add(track);
        updateAlbum(albumId, album);
    }

    @Override
    public void updateTrackInAlbum(String albumId, String trackId, Track track) throws IOException {
        Album album = getAlbumById(albumId);
        if (album == null) {
            throw new IOException("Album not found.");
        }
        for (Track t : album.getTracks()) {
            if (t.getId().equals(trackId)) {
                t.setName(track.getName());
                t.setDurationMs(track.getDurationMs());
                t.setExplicit(track.isExplicit());
                t.setUri(track.getUri());
                updateAlbum(albumId, album);
                return;
            }
        }
        throw new IOException("Track not found.");
    }

    @Override
    public void deleteTrackFromAlbum(String albumId, String trackId) throws IOException {
        Album album = getAlbumById(albumId);
        if (album == null) {
            throw new IOException("Album not found.");
        }
        album.getTracks().removeIf(t -> t.getId().equals(trackId));
        updateAlbum(albumId, album);
    }

    @Override
    public List<Song> getAllSongs() throws IOException {
        JsonNode songs = loadJsonData(songsJsonFilePath);
        List<Song> songList = new ArrayList<>();
        songs.elements().forEachRemaining(songNode -> {
            try {
                songList.add(objectMapper.treeToValue(songNode, Song.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return songList;
    }

    @Override
    public Song getSongById(String id) throws IOException {
        JsonNode songs = loadJsonData(songsJsonFilePath);
        for (JsonNode songNode : songs) {
            if (songNode.get("id").asText().equals(id)) {
                return objectMapper.treeToValue(songNode, Song.class);
            }
        }
        return null;
    }

    @Override
    public void addSong(Song song) throws IOException {
        JsonNode songsNode = loadJsonData(songsJsonFilePath);

        if (!(songsNode instanceof ArrayNode)) {
            throw new IOException("Invalid JSON format: Expected an array of songs.");
        }

        ArrayNode songsArray = (ArrayNode) songsNode;
        songsArray.add(objectMapper.valueToTree(song));

        saveJsonData(songsJsonFilePath, songsArray);
    }

    @Override
    public void updateSong(String id, Song song) throws IOException {
        JsonNode songs = loadJsonData(songsJsonFilePath);
        for (JsonNode songNode : songs) {
            if (songNode.get("id").asText().equals(id)) {
                ((ObjectNode) songNode).set("name", objectMapper.valueToTree(song.getName()));
                ((ObjectNode) songNode).set("duration_ms", objectMapper.valueToTree(song.getDurationMs()));
                ((ObjectNode) songNode).set("popularity", objectMapper.valueToTree(song.getPopularity()));
                ((ObjectNode) songNode).set("uri", objectMapper.valueToTree(song.getUri()));
                saveJsonData(songsJsonFilePath, songs);
                return;
            }
        }
        throw new IOException("Song not found.");
    }

    @Override
    public void deleteSong(String id) throws IOException {
        JsonNode songsNode = loadJsonData(songsJsonFilePath);

        if (!(songsNode instanceof ArrayNode)) {
            throw new IOException("Invalid JSON format: Expected an array of songs.");
        }

        ArrayNode songsArray = (ArrayNode) songsNode;
        ArrayNode updatedSongsArray = objectMapper.createArrayNode();

        boolean found = false;

        for (JsonNode songNode : songsArray) {
            if (!songNode.get("id").asText().equals(id)) {
                updatedSongsArray.add(songNode);
            } else {
                found = true;
            }
        }

        if (!found) {
            throw new IOException("Song not found.");
        }

        saveJsonData(songsJsonFilePath, updatedSongsArray);
    }

    private void saveJsonData(String path, JsonNode jsonData) throws IOException {
        File file = new ClassPathResource(path).getFile();
        try (FileWriter writer = new FileWriter(file)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, jsonData);
        }
    }

    private JsonNode loadJsonData(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        File file = resource.getFile();

        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }
        return objectMapper.readTree(resource.getFile());
    }
}
