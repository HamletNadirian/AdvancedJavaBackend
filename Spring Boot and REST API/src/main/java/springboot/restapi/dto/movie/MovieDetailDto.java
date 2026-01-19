package springboot.restapi.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springboot.restapi.dto.producer.ProducerDto;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailDto {
    private Long id;
    private String title;
    private Integer releaseDate;
    private String genre;
    private String description;
    private Double rating;
    private ProducerDto producer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
