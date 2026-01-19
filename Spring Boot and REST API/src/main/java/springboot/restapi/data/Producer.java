package springboot.restapi.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producers")
@Setter
@Getter
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Producer name is required")
    @Size(min = 2,max = 52)
    @Column(nullable=false)

    private String name;

    @Size(min = 2,max = 52)
    private String country;

    @OneToMany(mappedBy = "producer",cascade = CascadeType.ALL)
    private List<Movie> movies = new ArrayList<Movie>();
}
