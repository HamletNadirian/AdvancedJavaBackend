
package springboot.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long producerId;
    private static Long movieId;

    @Test
    @Order(1)
    @DisplayName("1. POST /api/producers - Create Producer (Success)")
    void testCreateProducer() throws Exception {
        String json = """
                {
                  "name": "Test Studio",
                  "country": "USA"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/producers")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Studio"))
                .andExpect(jsonPath("$.country").value("USA"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        producerId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("2. POST /api/producers - Duplicate Name (Should Fail)")
    void testCreateProducerDuplicateName() throws Exception {
        String json = """
                {"name":"Test Studio 2","country":"USA"}
                """;

        mockMvc.perform(post("/api/producers")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/producers")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));

    }

    @Test
    @Order(3)
    @DisplayName("3. POST /api/producers - Invalid Data (Should Fail)")
    void testCreateProducerInvalidData() throws Exception {
        String json = """
                {
                  "name": "",
                  "country": "USA"
                }
                """;

        mockMvc.perform(post("/api/producers")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("4. GET /api/producers - Get All Producers")
    void testGetAllProducers() throws Exception {
        mockMvc.perform(get("/api/producers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].country").exists());
    }

    @Test
    @Order(5)
    @DisplayName("5. PUT /api/producers/{id} - Update Producer (Success)")
    void testUpdateProducer() throws Exception {
        String json = """
                {
                  "name": "Updated Studio",
                  "country": "UK"
                }
                """;

        mockMvc.perform(put("/api/producers/" + producerId)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producerId))
                .andExpect(jsonPath("$.name").value("Updated Studio"))
                .andExpect(jsonPath("$.country").value("UK"));
    }

    @Test
    @Order(6)
    @DisplayName("6. PUT /api/producers/{id} - Update Non-existent (Should Fail)")
    void testUpdateProducerNotFound() throws Exception {
        String json = """
                {
                  "name": "Updated Studio",
                  "country": "UK"
                }
                """;

        mockMvc.perform(put("/api/producers/99999")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @DisplayName("7. POST /api/movies - Create Movie (Success)")
    void testCreateMovie() throws Exception {
        String json = String.format("""
                {
                  "title": "Test Movie",
                  "releaseDate": 2024,
                  "producerId": %d,
                  "genre": "Action",
                  "description": "A test movie",
                  "rating": 8.5
                }
                """, producerId);

        MvcResult result = mockMvc.perform(post("/api/movies")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.releaseDate").value(2024))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.rating").value(8.5))
                .andExpect(jsonPath("$.description").value("A test movie"))
                .andExpect(jsonPath("$.producer.id").value(producerId))
                .andExpect(jsonPath("$.producer.name").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        movieId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(8)
    @DisplayName("8. POST /api/movies - Invalid Data (Should Fail)")
    void testCreateMovieInvalidData() throws Exception {
        String json = """
                {
                  "title": "",
                  "releaseDate": 1800,
                  "genre": "Action"
                }
                """;

        mockMvc.perform(post("/api/movies")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("9. POST /api/movies - Non-existent Producer (Should Fail)")
    void testCreateMovieNonExistentProducer() throws Exception {
        String json = """
                {
                  "title": "Test Movie",
                  "releaseDate": 2024,
                  "producerId": 99999,
                  "genre": "Action",
                  "rating": 8.5
                }
                """;

        mockMvc.perform(post("/api/movies")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Producer not found")));
    }

    @Test
    @Order(10)
    @DisplayName("10. GET /api/movies/{id} - Get Movie By ID (Success)")
    void testGetMovieById() throws Exception {
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.releaseDate").value(2024))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.rating").value(8.5))
                .andExpect(jsonPath("$.producer.id").value(producerId))
                .andExpect(jsonPath("$.producer.name").exists())
                .andExpect(jsonPath("$.producer.country").exists());
    }

    @Test
    @Order(11)
    @DisplayName("11. GET /api/movies/{id} - Not Found")
    void testGetMovieByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Movie not found")));
    }

    @Test
    @Order(12)
    @DisplayName("12. PUT /api/movies/{id} - Update Movie (Success)")
    void testUpdateMovie() throws Exception {
        String json = String.format("""
                {
                  "title": "Updated Movie",
                  "releaseDate": 2025,
                  "producerId": %d,
                  "genre": "Drama",
                  "description": "Updated description",
                  "rating": 9.0
                }
                """, producerId);

        mockMvc.perform(put("/api/movies/" + movieId)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Updated Movie"))
                .andExpect(jsonPath("$.releaseDate").value(2025))
                .andExpect(jsonPath("$.genre").value("Drama"))
                .andExpect(jsonPath("$.rating").value(9.0))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @Order(13)
    @DisplayName("13. PUT /api/movies/{id} - Update Non-existent (Should Fail)")
    void testUpdateMovieNotFound() throws Exception {
        String json = String.format("""
                {
                  "title": "Updated Movie",
                  "releaseDate": 2025,
                  "producerId": %d,
                  "genre": "Drama",
                  "rating": 9.0
                }
                """, producerId);

        mockMvc.perform(put("/api/movies/99999")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    @DisplayName("14. POST /api/movies/_list - List Without Filters")
    void testListMoviesNoFilters() throws Exception {
        String json = """
                {
                  "page": 0,
                  "size": 10
                }
                """;

        mockMvc.perform(post("/api/movies/_list")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.list", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.list[0].id").exists())
                .andExpect(jsonPath("$.list[0].title").exists())
                .andExpect(jsonPath("$.list[0].releaseDate").exists())
                .andExpect(jsonPath("$.list[0].producerName").exists())
                .andExpect(jsonPath("$.list[0].genre").exists());
    }

    @Test
    @Order(15)
    @DisplayName("15. POST /api/movies/_list - List With All Filters")
    void testListMoviesWithFilters() throws Exception {
        String json = String.format("""
                {
                  "producerId": %d,
                  "genre": "Drama",
                  "minYear": 2020,
                  "maxYear": 2030,
                  "page": 0,
                  "size": 10
                }
                """, producerId);

        mockMvc.perform(post("/api/movies/_list")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @Order(16)
    @DisplayName("16. POST /api/movies/_list - Test Pagination")
    void testListMoviesPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            String json = String.format("""
                    {
                      "title": "Pagination Test Movie %d",
                      "releaseDate": 2024,
                      "producerId": %d,
                      "genre": "Action",
                      "rating": 7.5
                    }
                    """, i, producerId);

            mockMvc.perform(post("/api/movies")
                            .contentType(APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated());
        }

        String listJson = """
                {
                  "page": 0,
                  "size": 3
                }
                """;

        mockMvc.perform(post("/api/movies/_list")
                        .contentType(APPLICATION_JSON)
                        .content(listJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list", hasSize(3)))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.currentPage").value(0));

        String listJson2 = """
                {
                  "page": 1,
                  "size": 3
                }
                """;

        mockMvc.perform(post("/api/movies/_list")
                        .contentType(APPLICATION_JSON)
                        .content(listJson2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    @Order(17)
    @DisplayName("17. POST /api/movies/_report - Export to CSV")
    void testExportMoviesToCSV() throws Exception {
        String json = String.format("""
                {
                  "producerId": %d
                }
                """, producerId);

        MvcResult result = mockMvc.perform(post("/api/movies/_report")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        containsString("attachment")))
                .andExpect(header().string("Content-Disposition",
                        containsString("movies_report.csv")))
                .andExpect(content().contentType("text/csv"))
                .andReturn();

        String csvContent = result.getResponse().getContentAsString();

        assert csvContent.contains("ID");
        assert csvContent.contains("Title");
        assert csvContent.contains("Release Date");

    }

    @Test
    @Order(18)
    @DisplayName("18. POST /api/movies/upload - Import JSON (Success)")
    void testUploadMoviesJson() throws Exception {
        String producerJson = """
                {
                  "name": "Import Studio",
                  "country": "USA"
                }
                """;

        mockMvc.perform(post("/api/producers")
                        .contentType(APPLICATION_JSON)
                        .content(producerJson))
                .andExpect(status().isCreated());

        String jsonContent = """
                {
                  "movies": [
                    {
                      "title": "Imported Movie 1",
                      "releaseDate": 2023,
                      "producerName": "Import Studio",
                      "genre": "Sci-Fi",
                      "description": "First imported movie",
                      "rating": 8.0
                    },
                    {
                      "title": "Imported Movie 2",
                      "releaseDate": 2023,
                      "producerName": "Import Studio",
                      "genre": "Horror",
                      "description": "Second imported movie",
                      "rating": 7.5
                    }
                  ]
                }
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movies.json",
                "application/json",
                jsonContent.getBytes()
        );

        mockMvc.perform(multipart("/api/movies/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.failureCount").value(0))
                .andExpect(jsonPath("$.errors").isEmpty());

    }

    @Test
    @Order(19)
    @DisplayName("19. POST /api/movies/upload - Import with Errors")
    void testUploadMoviesJsonWithErrors() throws Exception {
        String jsonContent = """
                {
                  "movies": [
                    {
                      "title": "Good Movie",
                      "releaseDate": 2023,
                      "producerName": "Import Studio",
                      "genre": "Action",
                      "rating": 8.0
                    },
                    {
                      "title": "Bad Movie",
                      "releaseDate": 2023,
                      "producerName": "Non-existent Studio",
                      "genre": "Drama",
                      "rating": 7.0
                    }
                  ]
                }
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movies.json",
                "application/json",
                jsonContent.getBytes()
        );

        mockMvc.perform(multipart("/api/movies/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(1))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value(
                        containsString("Producer not found")));
    }

    @Test
    @Order(20)
    @DisplayName("20. DELETE /api/movies/{id} - Delete Movie (Success)")
    void testDeleteMovie() throws Exception {
        mockMvc.perform(delete("/api/movies/" + movieId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(21)
    @DisplayName("21. DELETE /api/movies/{id} - Delete Non-existent (Should Fail)")
    void testDeleteMovieNotFound() throws Exception {
        mockMvc.perform(delete("/api/movies/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(22)
    @DisplayName("22. DELETE /api/producers/{id} - Delete Producer (Success)")
    void testDeleteProducer() throws Exception {
        mockMvc.perform(delete("/api/producers/" + producerId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/producers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + producerId + ")]")
                        .doesNotExist());
    }

    @Test
    @Order(23)
    @DisplayName("23. DELETE /api/producers/{id} - Delete Non-existent (Should Fail)")
    void testDeleteProducerNotFound() throws Exception {
        mockMvc.perform(delete("/api/producers/99999"))
                .andExpect(status().isNotFound());
    }
}
