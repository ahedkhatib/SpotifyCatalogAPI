package com.example.catalog.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceSelector {

    @Value("${datasource.type:json}") // Default to 'json' if not specified
    private String dataSourceType;

    @Bean
    public DataSourceService dataSourceService(JSONDataSourceService jsonDataSourceService,
                                               DatabaseDataSourceService databaseDataSourceService,
                                               SpotifyAPIDataSourceService spotifyAPIDataSourceService) {
        if ("json".equalsIgnoreCase(dataSourceType)) {
            return jsonDataSourceService;
        } else if ("database".equalsIgnoreCase(dataSourceType)) {
            return databaseDataSourceService;
        } else if ("spotify_api".equalsIgnoreCase(dataSourceType)) {
            return spotifyAPIDataSourceService;
        } else {
            throw new IllegalArgumentException("Invalid data source type: " + dataSourceType);
        }
    }
}