package com.example.catalog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String PRODUCTION_JSON_FILE = "src/main/resources/data/popular_artists.json";
    private final String TEST_JSON_FILE = "src/test/resources/test_artists.json";

    @BeforeEach
    void setUp() throws IOException {
        File testFile = new File(TEST_JSON_FILE);
        if (!testFile.exists()) {
            createEmptyTestJson();
        }
        copyProductionDataToTest();
    }

    private void createEmptyTestJson() throws IOException {
        try (FileWriter fileWriter = new FileWriter(TEST_JSON_FILE)) {
            fileWriter.write("{}");
        }
    }

    private void copyProductionDataToTest() throws IOException {
        File source= new File(PRODUCTION_JSON_FILE);
        File target = new File(TEST_JSON_FILE);
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testGetArtistById_Found() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/artists/1Xyo4u8uXC1ZmMpatF05PJ"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"id\":\"1Xyo4u8uXC1ZmMpatF05PJ\",\"name\":\"The Weeknd\",\"followers\":95105223,\"genres\":[\"canadian contemporary r&b\",\"canadian pop\",\"pop\"],\"images\":[{\"url\":\"ab6761610000e5eb9e528993a2820267b97f6aae.jpeg\",\"height\":640,\"width\":640},{\"url\":\"ab676161000051749e528993a2820267b97f6aae.jpeg\",\"height\":320,\"width\":320},{\"url\":\"ab6761610000f1789e528993a2820267b97f6aae.jpeg\",\"height\":160,\"width\":160}],\"popularity\":96,\"uri\":\"spotify:artist:1Xyo4u8uXC1ZmMpatF05PJ\"}"));
    }

    /*@AfterEach
    void tearDown() {
        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }*/
}