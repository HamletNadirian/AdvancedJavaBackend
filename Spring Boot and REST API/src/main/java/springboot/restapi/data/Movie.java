package springboot.restapi.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.sql.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_producer_id", columnList = "producer_id"),
        @Index(name = "idx_release_date", columnList = "release_date"),
        @Index(name = "idx_genre", columnList = "genre")
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(name = "release_date", nullable = false)
    @NotNull(message = "Release year is required")
    @Min(1888)
    @Max(2052)
    private Integer releaseDate;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false)
    private String genre;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double rating;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 1000)
    private String description;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @NotNull(message = "Producer is required")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "producer_id", nullable = false)
    private Producer producer;

}
