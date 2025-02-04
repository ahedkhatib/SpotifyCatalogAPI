# SpotifyCatalogAPI

## Overview

SpotifyCatalogAPI is a Spring Boot RESTful API that provides a catalog of artists, albums, and songs. The API is designed with flexibility in mind, supporting multiple data sources such as:
- **JSON Files** (Local storage)
- **Spotify Web API** (Real-time data from Spotify)
- **Database** (Future expansion for persistence)

This modular approach ensures easy switching between different data sources without modifying the core logic.

---

## Features

The API allows full CRUD (Create, Read, Update, Delete) operations for:
- **Artists**
- **Albums**
- **Songs**
- **Tracks within albums**

Supported data sources:
- **JSONDataSourceService** (Reads and writes to JSON files)
- **SpotifyAPIDataSourceService** (Fetches data from Spotify API)
- **DatabaseDataSourceService** (Placeholder for database implementation)

---

## Endpoints

### **Artists**
- \`GET /artists\` - List all artists
- \`GET /artists/{id}\` - Retrieve an artist by their ID
- \`POST /artists\` - Add a new artist
- \`PUT /artists/{id}\` - Update artist details
- \`DELETE /artists/{id}\` - Delete an artist
- \`GET /artists/{id}/albums\` - List albums by the artist
- \`GET /artists/{id}/songs\` - List songs by the artist

### **Albums**
- \`GET /albums\` - List all albums
- \`GET /albums/{id}\` - Retrieve an album by its ID
- \`POST /albums\` - Add a new album
- \`PUT /albums/{id}\` - Update album details
- \`DELETE /albums/{id}\` - Delete an album
- \`GET /albums/{id}/tracks\` - List all tracks in an album
- \`POST /albums/{id}/tracks\` - Add a new track to an album
- \`PUT /albums/{id}/tracks/{track_id}\` - Update a track in an album
- \`DELETE /albums/{id}/tracks/{track_id}\` - Remove a track from an album

### **Songs**
- \`GET /songs\` - List all songs
- \`GET /songs/{id}\` - Retrieve a song by its ID
- \`POST /songs\` - Add a new song
- \`PUT /songs/{id}\` - Update a song's details
- \`DELETE /songs/{id}\` - Delete a song

---

## Data Source Configuration

The API uses \`application.properties\` to define the active data source.

\`\`\`properties 
- Available options: json, spotify_api, database
- datasource.type=json
\`\`\`

- **JSON Mode:** Reads from and writes to JSON files.
- **Spotify API Mode:** Fetches data from Spotify Web API (Read-only).
- **Database Mode:** Placeholder for future database support.

---

## How to Run

### Prerequisites
- Java 17+
- Maven

### Build and Run
\`\`\`sh
# Clone the repository
git clone https://github.com/ahedkhatib/SpotifyCatalogAPI.git
cd SpotifyCatalogAPI

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
\`\`\`

The API will be available at \`http://localhost:8080\`.

---

## Testing

Unit tests are implemented for controllers and data sources.
Run tests using:
\`\`\`sh
mvn test
\`\`\`

To test API responses, you can use **Postman** or **cURL**:
\`\`\`sh
# Get all artists
curl -X GET \"http://localhost:8080/artists\" -H \"Accept: application/json\"
\`\`\`

---

## Continuous Integration (CI)

A **GitHub Actions** pipeline is configured to:
1. Build the project
2. Run tests
3. Ensure API stability before deployment

---

## Notes

- **Some Spotify API operations are read-only** (e.g., adding artists, albums, or songs is not supported).
- **Error Handling:** Proper status codes are returned:
    - \`404 Not Found\` for missing resources.
    - \`400 Bad Request\` for invalid inputs.
    - \`500 Internal Server Error\` for unexpected failures.