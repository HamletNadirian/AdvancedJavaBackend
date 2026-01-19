package springboot.restapi.dto.producer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for producer response")
public class ProducerDto {
    @Schema(description = "Auto-generated ID",example = "1")
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255)
    @Schema(description = "Name of producer",example = "Christopher Nolan")
    private String name;

    @Size(max = 100)
    @Schema(description = "Country",example = "USA")
    private String country;
}
