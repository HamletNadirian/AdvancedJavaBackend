package springboot.restapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springboot.restapi.data.Movie;
import springboot.restapi.dto.*;
import springboot.restapi.dto.movie.*;
import springboot.restapi.service.ExportService;
import springboot.restapi.service.ImportService;
import springboot.restapi.service.MovieService;

@RestController
@RequestMapping("/api/movies")
@AllArgsConstructor
@Tag(name = "Movies", description = "API for managing movies")
public class MovieController extends BaseController {

    private final MovieService movieService;
    private final ExportService exportService;
    private final ImportService importService;

    @Operation(
            summary = "Create a new film",
            description = "Creates a new movie with the specified data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The film was successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Producer not found")
    })
    @PostMapping
    public ResponseEntity<MovieDetailDto> create(
            @Valid @RequestBody MovieCreateDto dto) {
        MovieDetailDto created = movieService.createMovie(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Get all movies")
    @GetMapping
    public ResponseEntity<List<MovieListItemDto>> getAll() {
        List<Movie> movies = movieService.getAllMovies();
        List<MovieListItemDto> dtos = movies.stream()
                .map(this::toListDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get a movie by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The film was found."),
            @ApiResponse(responseCode = "404", description = "The film wasn't found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDetailDto> getById(
            @Parameter(description = "Movie ID", example = "1")
            @PathVariable Long id) {
        MovieDetailDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Update movie")
    @PutMapping("/{id}")
    public ResponseEntity<MovieDetailDto> update(
            @Parameter(description = "Movie ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MovieUpdateDto dto) {
        MovieDetailDto updated = movieService.updateMovie(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete movie")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Movie ID", example = "1")
            @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a list of movies with pagination",
            description = "Returns a list of movies with pagination and filtering."
    )
    @PostMapping("/_list")
    public ResponseEntity<MovieListResponseDto> list(
            @Valid @RequestBody MovieListRequestDto request) {
        MovieListResponseDto response = movieService.getMovieList(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Generate a CSV report",
            description = "Generates and downloads a CSV file with movies based on specified filters"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The CSV successfully generated",
                    content = @Content(mediaType = "text/csv",
                            schema = @Schema(type = "string", format = "binary"))
            )
    })
    @PostMapping("/_report")
    public ResponseEntity<Resource> generateReport(
            @Valid @RequestBody MovieListRequestDto request) {
        Resource file = (Resource) exportService.exportToCSV(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=movies_report.csv")
                .body(file);
    }

    @Operation(
            summary = "Import movie(s) from JSON file",
            description = "Downloads movies from a JSON file"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully processed"),
            @ApiResponse(responseCode = "400", description = "Invalid file or format")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> upload(
            @Parameter(description = "JSON file with movies",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("file") MultipartFile file) {
        UploadResponseDto response = importService.importFromJson(file);
        return ResponseEntity.ok(response);
    }

    private MovieListItemDto toListDto(Movie movie) {
        MovieListItemDto dto = new MovieListItemDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setRating(movie.getRating());
        dto.setRating(movie.getRating());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setGenre(movie.getGenre());
        dto.setProducerName(movie.getProducer().getName());
        return dto;
    }
}
