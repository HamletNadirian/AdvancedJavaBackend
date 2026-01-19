package springboot.restapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UploadResponseDto {
    private Integer successCount;
    private Integer failureCount;
    private List<String> errors;
}
