package com.example.catalog;

import com.example.catalog.utils.CatalogUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogUtilsTest {

    private CatalogUtils catalogUtils;
    private List<JsonNode> songs;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        catalogUtils = new CatalogUtils();
        objectMapper = new ObjectMapper();

        // Sample song data for testing. TODO - Add more songs
        String jsonData = """
                    [
                        {
                            "duration_ms": 200040,
                            "name": "Blinding Lights",
                            "popularity": 87,
                            "album": {
                                "name": "After Hours",
                                "release_date": "2020-03-20",
                                "total_tracks": 14
                            },
                            "artists": [
                                {
                                    "name": "The Weeknd"
                                }
                            ]
                        },
                        {
                            "duration_ms": 180000,
                            "name": "Shape of You",
                            "popularity": 92,
                            "album": {
                                "name": "÷ (Divide)",
                                "release_date": "2017-01-06",
                                "total_tracks": 16
                            },
                            "artists": [
                                {
                                    "name": "Ed Sheeran"
                                }
                            ]
                        },
                        {
                            "duration_ms": 220050,
                            "name": "Rolling in the Deep",
                            "popularity": 85,
                            "album": {
                                "name": "21",
                                "release_date": "2011-01-24",
                                "total_tracks": 11
                            },
                            "artists": [
                                {
                                    "name": "Adele"
                                }
                            ]
                        },
                        {
                            "duration_ms": 195030,
                            "name": "Dance Monkey",
                            "popularity": 79,
                            "album": {
                                "name": "Tales From the Sip",
                                "release_date": "2019-05-03",
                                "total_tracks": 9
                            },
                            "artists": [
                                {
                                    "name": "Tones and I"
                                }
                            ]
                        },
                        {
                            "duration_ms": 165045,
                            "name": "Bad Guy",
                            "popularity": 88,
                            "album": {
                                "name": "When We All Fall Asleep, Where Do We Go?",
                                "release_date": "2019-03-29",
                                "total_tracks": 14
                            },
                            "artists": [
                                {
                                    "name": "Billie Eilish"
                                }
                            ]
                        },
                        {
                            "duration_ms": 210060,
                            "name": "Someone You Loved",
                            "popularity": 84,
                            "album": {
                                "name": "No.6 Collaborations Project",
                                "release_date": "2019-07-12",
                                "total_tracks": 15
                            },
                            "artists": [
                                {
                                    "name": "Lewis Capaldi"
                                }
                            ]
                        },
                        {
                            "duration_ms": 240075,
                            "name": "Old Town Road",
                            "popularity": 90,
                            "album": {
                                "name": "7",
                                "release_date": "2019-03-01",
                                "total_tracks": 18
                            },
                            "artists": [
                                {
                                    "name": "Lil Nas X"
                                }
                            ]
                        },
                        {
                            "duration_ms": 175020,
                            "name": "Shallow",
                            "popularity": 86,
                            "album": {
                                "name": "A Star Is Born Soundtrack",
                                "release_date": "2018-09-27",
                                "total_tracks": 34
                            },
                            "artists": [
                                {
                                    "name": "Lady Gaga"
                                }
                            ]
                        },
                        {
                            "duration_ms": 300000,
                            "name": "VERY Long Song",
                            "popularity": 50,
                            "album": {
                                "name": "Experimental Album",
                                "release_date": "2022-12-31",
                                "total_tracks": 5
                            },
                            "artists": [
                                {
                                    "name": "Experimental Artist"
                                }
                            ]
                        },
                        {
                            "duration_ms": 120000,
                            "name": "Short Song",
                            "popularity": 30,
                            "album": {
                                "name": "Minimal Album",
                                "release_date": "2010-01-01",
                                "total_tracks": 3
                            },
                            "artists": [
                                {
                                    "name": "Minimalist"
                                }
                            ]
                        },
                        {
                            "duration_ms": 200000,
                            "name": "Duplicate name",
                            "popularity": 70,
                            "album": {
                                "name": "First Album",
                                "release_date": "2015-06-15",
                                "total_tracks": 10
                            },
                            "artists": [
                                {
                                    "name": "Artist One"
                                }
                            ]
                        },
                        {
                            "duration_ms": 220000,
                            "name": "Duplicate name",
                            "popularity": 75,
                            "album": {
                                "name": "Second Album",
                                "release_date": "2016-07-20",
                                "total_tracks": 12
                            },
                            "artists": [
                                {
                                    "name": "Artist Two"
                                }
                            ]
                        },
                        {
                            "duration_ms": 190000,
                            "name": "Multiple Artists Song",
                            "popularity": 80,
                            "album": {
                                "name": "Collaboration Album",
                                "release_date": "2020-11-15",
                                "total_tracks": 8
                            },
                            "artists": [
                                {
                                    "name": "Artist A"
                                }
                            ]
                        },
                        {
                            "duration_ms": 180000,
                            "name": "Case Sensitivity Test",
                            "popularity": 65,
                            "album": {
                                "name": "Mixed Case Album",
                                "release_date": "2018-04-10",
                                "total_tracks": 6
                            },
                            "artists": [
                                {
                                    "name": "Artist A"
                                }
                            ]
                        },
                        {
                            "duration_ms": 250000,
                            "name": "Zero Popularity Song",
                            "popularity": 0,
                            "album": {
                                "name": "Obscure Album",
                                "release_date": "2005-03-05",
                                "total_tracks": 7
                            },
                            "artists": [
                                {
                                    "name": "Unknown Artist"
                                }
                            ]
                        }
                    ]
                """;
        songs = new ArrayList<>();
        objectMapper.readTree(jsonData).forEach(songs::add);
    }


    @Test
    void testSortSongsByName() {
        List<JsonNode> sortedSongs = catalogUtils.sortSongsByName(songs);
        //The first name
        assertEquals("Bad Guy", sortedSongs.get(0).get("name").asText());

        //Duplicate names
        assertEquals("Duplicate name", sortedSongs.get(4).get("name").asText());
        assertEquals("Duplicate name", sortedSongs.get(5).get("name").asText());

        //The last name
        assertEquals("Zero Popularity Song", sortedSongs.get(14).get("name").asText());

        //List not null
        assertNotNull(sortedSongs);
    }

    @Test
    void testFilterSongsByPopularity() {
        List<JsonNode> filteredSongs = catalogUtils.filterSongsByPopularity(songs, 89);
        assertEquals(2, filteredSongs.size());
        assertEquals("Shape of You", filteredSongs.get(0).get("name").asText());
        assertEquals("Old Town Road", filteredSongs.get(1).get("name").asText());

        //Filter by min popularity
        List<JsonNode> filteredSongs_2 = catalogUtils.filterSongsByPopularity(songs, 0);
        assertEquals(15, filteredSongs_2.size());
        assertEquals("Blinding Lights", filteredSongs_2.get(0).get("name").asText());

        //Filter by max popularity
        List<JsonNode> filteredSongs_3 = catalogUtils.filterSongsByPopularity(songs, 92);
        assertEquals(1, filteredSongs_3.size());
        assertEquals("Shape of You", filteredSongs_3.get(0).get("name").asText());

    }

    @Test
    void testDoesSongExistByName(){
        assertTrue(catalogUtils.doesSongExistByName(songs, "Blinding Lights"));
        assertTrue(catalogUtils.doesSongExistByName(songs, "ShaPe Of YoU"));
        assertFalse(catalogUtils.doesSongExistByName(songs, "Ahed"));
        assertFalse(catalogUtils.doesSongExistByName(songs, ""));

    }

    @Test
    void testcountSongsByArtist(){
        assertEquals(2, catalogUtils.countSongsByArtist(songs, "Artist A"));
        assertEquals(0, catalogUtils.countSongsByArtist(songs, "Ahed"));
        assertEquals(1, catalogUtils.countSongsByArtist(songs, "Unknown Artist"));
        assertEquals(1, catalogUtils.countSongsByArtist(songs, "UNkNown ArtIst"));
    }

    @Test
    void testgetLongestSong(){
        JsonNode longestSong = catalogUtils.getLongestSong(songs);
        assertEquals("300000", longestSong.get("duration_ms").asText());
        assertNotNull(longestSong);
    }

    @Test
    void testgetSongByYear(){
        assertEquals("Zero Popularity Song", catalogUtils.getSongByYear(songs, 2005).get(0).get("name").asText());
        assertEquals(0, catalogUtils.getSongByYear(songs, 2024).size());
        assertEquals("Shallow", catalogUtils.getSongByYear(songs, 2018).get(0).get("name").asText());
    }

    @Test
    void testgetMostRecentSong(){
        JsonNode mostRecentSong = catalogUtils.getMostRecentSong(songs);
        assertEquals("VERY Long Song", mostRecentSong.get("name").asText());
        assertNotNull(mostRecentSong);
    }

}