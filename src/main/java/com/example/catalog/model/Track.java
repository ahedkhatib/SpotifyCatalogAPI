package com.example.catalog.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

    private String id;
    private String name;

    @JsonProperty("duration_ms")
    private int durationMs;

    private boolean explicit;
    private String uri;

    public Track() {}

    public Track(String id, String name, int durationMs, boolean explicit, String uri) {
        this.id = id;
        this.name = name;
        this.durationMs = durationMs;
        this.explicit = explicit;
        this.uri = uri;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
