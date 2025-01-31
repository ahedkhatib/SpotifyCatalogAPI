package com.example.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetArtistById_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/artists/1Xyo4u8uXC1ZmMpatF05PJ"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"id\":\"1Xyo4u8uXC1ZmMpatF05PJ\",\"name\":\"The Weeknd\",\"followers\":95105223,\"genres\":[\"canadian contemporary r&b\",\"canadian pop\",\"pop\"],\"images\":[{\"url\":\"ab6761610000e5eb9e528993a2820267b97f6aae.jpeg\",\"height\":640,\"width\":640},{\"url\":\"ab676161000051749e528993a2820267b97f6aae.jpeg\",\"height\":320,\"width\":320},{\"url\":\"ab6761610000f1789e528993a2820267b97f6aae.jpeg\",\"height\":160,\"width\":160}],\"popularity\":96,\"uri\":\"spotify:artist:1Xyo4u8uXC1ZmMpatF05PJ\"}"));
    }
}