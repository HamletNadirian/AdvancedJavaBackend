package springboot.restapi.dto.movie;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieListItemDto {
    private Long id;
    private String title;
    private Integer releaseDate;
    private String producerName;
    private String genre;
    private String description;
    private double rating;

}
