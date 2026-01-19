package springboot.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.restapi.data.Producer;

import java.util.Optional;

public interface ProducerRepository extends JpaRepository<Producer, Long> {
    Optional<Producer> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndId(String name, Long id);
}

