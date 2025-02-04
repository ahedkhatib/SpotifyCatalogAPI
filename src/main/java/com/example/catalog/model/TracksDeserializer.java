package com.example.catalog.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TracksDeserializer extends JsonDeserializer<List<Track>> {

    @Override
    public List<Track> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        List<Track> tracks = new ArrayList<>();

        if (node.isArray()) {

            for (JsonNode trackNode : node) {
                Track track = jsonParser.getCodec().treeToValue(trackNode, Track.class);
                tracks.add(track);
            }
        } else if (node.isObject() && node.has("items")) {
            for (JsonNode trackNode : node.get("items")) {
                Track track = jsonParser.getCodec().treeToValue(trackNode, Track.class);
                tracks.add(track);
            }
        }

        return tracks;
    }
}
