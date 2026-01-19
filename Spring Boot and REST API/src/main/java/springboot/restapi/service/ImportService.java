package springboot.restapi.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springboot.restapi.data.Movie;
import springboot.restapi.data.Producer;
import springboot.restapi.dto.UploadResponseDto;
import springboot.restapi.repository.MovieRepository;
import springboot.restapi.repository.ProducerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ImportService {
    private final MovieRepository movieRepository;
    private final ProducerRepository producerRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public UploadResponseDto importFromJson(MultipartFile file) {
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(file.getInputStream());
            JsonNode movies = root.get("movies");

            if (movies == null || !movies.isArray()) {
                throw new IllegalArgumentException(
                        "Invalid JSON format");
            }

            for (JsonNode movieNode : movies) {
                try {
                    Movie movie = parseMovie(movieNode);
                    movieRepository.save(movie);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    errors.add("Line " + (successCount + failureCount) +
                            ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to import JSON", e);
        }

        UploadResponseDto response = new UploadResponseDto();
        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        response.setErrors(errors);
        return response;
    }

    private Movie parseMovie(JsonNode node) throws Exception {
        String title = node.get("title").asText();
        Integer year = node.get("releaseDate").asInt();
        String producerName = node.get("producerName").asText();
        String genre = node.get("genre").asText();

        Producer producer = producerRepository.findByName(producerName)
                .orElseThrow(() -> new Exception(
                        "Producer not found: " + producerName));

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setReleaseDate(year);
        movie.setProducer(producer);
        movie.setGenre(genre);

        if (node.has("description")) {
            movie.setDescription(node.get("description").asText());
        }
        if (node.has("rating")) {
            try {
                double rating = node.get("rating").asDouble();
                if (rating >= 0.0 && rating <= 10.0) {
                    movie.setRating(rating);
                }
            } catch (Exception e) {
                log.warn("Invalid rating value in movie: {}", title);
            }
        }
        return movie;
    }
}
