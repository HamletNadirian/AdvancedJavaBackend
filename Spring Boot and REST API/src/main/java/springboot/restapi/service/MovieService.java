package springboot.restapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import springboot.restapi.data.Movie;
import springboot.restapi.data.Producer;
import springboot.restapi.dto.movie.*;
import springboot.restapi.dto.producer.ProducerDto;
import springboot.restapi.repository.MovieRepository;
import springboot.restapi.repository.ProducerRepository;
import springboot.restapi.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ProducerRepository producerRepository;
    private final NotificationService notificationService;

    @Transactional
    public MovieDetailDto createMovie(MovieCreateDto dto) {
        Producer producer = producerRepository.findById(dto.getProducerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producer not found: " + dto.getProducerId())
                );
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setGenre(dto.getGenre());
        movie.setDescription(dto.getDescription());
        movie.setRating(dto.getRating());
        movie.setProducer(producer);
        movie.setCreatedAt(dto.getCreatedAt());

        Movie saved = movieRepository.save(movie);
        notificationService.sendMovieCreatedNotification(saved);
        return toDetailDto(saved);
    }

    public MovieDetailDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Movie not found: " + id
                ));
        return toDetailDto(movie);
    }

    @Transactional
    public MovieDetailDto updateMovie(Long id, MovieUpdateDto dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Movie not found: " + id));
        Producer producer = producerRepository.findById(dto.getProducerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producer not found: " + dto.getProducerId()));
        movie.setTitle(dto.getTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setGenre(dto.getGenre());
        movie.setDescription(dto.getDescription());
        movie.setRating(dto.getRating());
        movie.setProducer(producer);
        movie.setUpdatedAt(dto.getUpdatedAt());

        Movie updated = movieRepository.save(movie);
        return toDetailDto(updated);
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found: " + id));

        String movieTitle = movie.getTitle();

        movieRepository.deleteById(id);

        notificationService.sendMovieDeletedNotification(id, movieTitle);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public MovieListResponseDto getMovieList(MovieListRequestDto request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("releaseDate").descending()
        );

        Page<Movie> page = movieRepository.findByFilters(
                request.getProducerId(),
                request.getGenre(),
                request.getMinYear(),
                request.getMaxYear(),
                pageable
        );

        List<MovieListItemDto> items = page.getContent().stream()
                .map(this::toListItemDto)
                .collect(Collectors.toList());

        MovieListResponseDto response = new MovieListResponseDto();
        response.setList(items);
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setCurrentPage(page.getNumber());

        return response;
    }

    public List<Movie> getMoviesForExport(MovieListRequestDto request) {
        return movieRepository.findAllByFilters(
                request.getProducerId(),
                request.getGenre(),
                request.getMinYear(),
                request.getMaxYear()
        );
    }

    private MovieDetailDto toDetailDto(Movie movie) {
        MovieDetailDto dto = new MovieDetailDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setGenre(movie.getGenre());
        dto.setDescription(movie.getDescription());
        dto.setRating(movie.getRating());
        dto.setCreatedAt(movie.getCreatedAt());
        dto.setUpdatedAt(movie.getUpdatedAt());


        ProducerDto producerDto = new ProducerDto();
        producerDto.setId(movie.getProducer().getId());
        producerDto.setName(movie.getProducer().getName());
        producerDto.setCountry(movie.getProducer().getCountry());
        dto.setProducer(producerDto);
        return dto;
    }

    private MovieListItemDto toListItemDto(Movie movie) {
        MovieListItemDto dto = new MovieListItemDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setGenre(movie.getGenre());
        dto.setProducerName(movie.getProducer().getName());
        return dto;
    }
}

