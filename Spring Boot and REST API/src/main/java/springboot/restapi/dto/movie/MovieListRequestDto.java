package springboot.restapi.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for requesting a list of films with filtering")
public class MovieListRequestDto {
    @Schema(description = "Producer ID for filtering", example = "1")
    private Long producerId;

    @Schema(description = "Genre for filtering", example = "Sci-Fi")
    private String genre;

    @Schema(description = "Minimum year of manufacture", example = "1888")
    private Integer minYear;

    @Schema(description = "Maximum year of manufacture", example = "2052")
    private Integer maxYear;

    @Min(0)
    @Schema(description = "Page number (starts with 0)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Min(1)
    @Max(100)
    @Schema(description = "Number of items per page", example = "25", defaultValue = "25")
    private Integer size = 25;
}
