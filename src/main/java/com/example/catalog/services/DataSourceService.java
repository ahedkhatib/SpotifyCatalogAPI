package com.example.catalog.services;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;

import java.io.IOException;
import java.util.List;

public interface DataSourceService {

    Artist getArtistById(String id) throws IOException;
    List<Artist> getAllArtists() throws IOException;
    List<Album> getAlbumsByArtist(String artistId) throws IOException;
    List<Song> getSongsByArtist(String artistId) throws IOException;
    void addArtist(Artist artist) throws IOException;
    void updateArtist(String id, Artist artist) throws IOException;
    void deleteArtist(String id) throws IOException;

    List<Album> getAllAlbums() throws IOException;
    Album getAlbumById(String id) throws IOException;
    void addAlbum(Album album) throws IOException;
    void updateAlbum(String id, Album album) throws IOException;
    void deleteAlbum(String id) throws IOException;

    List<Track> getTracksByAlbum(String albumId) throws IOException;
    void addTrackToAlbum(String albumId, Track track) throws IOException;
    void updateTrackInAlbum(String albumId, String trackId, Track track) throws IOException;
    void deleteTrackFromAlbum(String albumId, String trackId) throws IOException;

    List<Song> getAllSongs() throws IOException;
    Song getSongById(String id) throws IOException;
    void addSong(Song song) throws IOException;
    void updateSong(String id, Song song) throws IOException;
    void deleteSong(String id) throws IOException;
}
