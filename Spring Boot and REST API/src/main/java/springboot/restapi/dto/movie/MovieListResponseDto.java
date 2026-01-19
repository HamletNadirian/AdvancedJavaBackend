package springboot.restapi.dto.movie;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class MovieListResponseDto {
    private List<MovieListItemDto> list;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
}
