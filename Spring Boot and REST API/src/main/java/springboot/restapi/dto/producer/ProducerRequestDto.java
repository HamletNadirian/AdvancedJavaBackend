package springboot.restapi.dto.producer;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for creating/updating a producer")
public class ProducerRequestDto {

    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "Name of producer",example = "Christopher Nolan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 100)
    @Schema(description = "Country",example = "USA")
    private String country;
}
