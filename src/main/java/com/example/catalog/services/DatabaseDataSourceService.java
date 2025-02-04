package com.example.catalog.services;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class DatabaseDataSourceService implements DataSourceService {

    @Override
    public Artist getArtistById(String id) {
        return null;
        //return db.findById(id).orElse(null);
    }

    @Override
    public List<Artist> getAllArtists() throws IOException {
        return List.of();
    }

    @Override
    public List<Album> getAlbumsByArtist(String artistId) throws IOException {
        return List.of();
    }

    @Override
    public List<Song> getSongsByArtist(String artistId) throws IOException {
        return List.of();
    }

    @Override
    public void addArtist(Artist artist) throws IOException {

    }

    @Override
    public void updateArtist(String id, Artist artist) throws IOException {

    }

    @Override
    public void deleteArtist(String id) throws IOException {

    }

    @Override
    public List<Album> getAllAlbums() throws IOException {
        return List.of();
    }

    @Override
    public Album getAlbumById(String id) throws IOException {
        return null;
    }

    @Override
    public void addAlbum(Album album) throws IOException {

    }

    @Override
    public void updateAlbum(String id, Album album) throws IOException {

    }

    @Override
    public void deleteAlbum(String id) throws IOException {

    }

    @Override
    public List<Track> getTracksByAlbum(String albumId) throws IOException {
        return List.of();
    }

    @Override
    public void addTrackToAlbum(String albumId, Track track) throws IOException {

    }

    @Override
    public void updateTrackInAlbum(String albumId, String trackId, Track track) throws IOException {

    }

    @Override
    public void deleteTrackFromAlbum(String albumId, String trackId) throws IOException {

    }

    @Override
    public List<Song> getAllSongs() throws IOException {
        return List.of();
    }

    @Override
    public Song getSongById(String id) throws IOException {
        return null;
    }

    @Override
    public void addSong(Song song) throws IOException {

    }

    @Override
    public void updateSong(String id, Song song) throws IOException {

    }

    @Override
    public void deleteSong(String id) throws IOException {

    }
}

