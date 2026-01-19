package springboot.restapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.restapi.data.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>
{
    @Query("SELECT m FROM Movie m WHERE " +
            "(:producerId IS NULL OR m.producer.id = :producerId) AND " +
            "(:genre IS NULL OR m.genre = :genre) AND " +
            "(:minYear IS NULL OR m.releaseDate >= :minYear) AND " +
            "(:maxYear IS NULL OR m.releaseDate <= :maxYear)")
    Page<Movie> findByFilters(
            @Param("producerId") Long producerId,
            @Param("genre") String genre,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            Pageable pageable
    );

    @Query("SELECT m FROM Movie m WHERE " +
            "(:producerId IS NULL OR m.producer.id = :producerId) AND " +
            "(:genre IS NULL OR m.genre = :genre) AND " +
            "(:minYear IS NULL OR m.releaseDate >= :minYear) AND " +
            "(:maxYear IS NULL OR m.releaseDate <= :maxYear)")
    List<Movie> findAllByFilters(
            @Param("producerId") Long producerId,
            @Param("genre") String genre,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear
    );
}
