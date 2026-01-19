package springboot.restapi.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "DTO for updating a movie")
public class MovieUpdateDto {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "The description of the movie", example = "Inception 2", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotNull(message = "Release date is required")
    @Min(1888)
    @Max(2052)
    @Schema(description = "Year of release", example = "2015", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer releaseDate;

    @NotNull(message = "Producer ID is required")
    @Schema(description = "ID producer", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long producerId;

    @NotBlank(message = "Genre is required")
    @Schema(description = "Movie genre", example = "Sci-Fi", requiredMode = Schema.RequiredMode.REQUIRED)
    private String genre;

    @Size(max = 1000)
    @Schema(description = "The movie's description", example = "A thief steals corporate secrets through dream-sharing technology")
    private String description;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    @Schema(description = "Movie rating", example = "5.8")
    private Double rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
