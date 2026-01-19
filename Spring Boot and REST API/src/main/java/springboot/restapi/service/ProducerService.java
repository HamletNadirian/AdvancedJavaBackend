package springboot.restapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import springboot.restapi.data.Producer;
import springboot.restapi.dto.producer.ProducerDto;
import springboot.restapi.dto.producer.ProducerRequestDto;
import springboot.restapi.exception.DuplicateProducerNameException;
import springboot.restapi.repository.ProducerRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;

    public List<Producer> getAllProducers() {
        return producerRepository.findAll();
    }
    private final NotificationService notificationService;

    @Transactional
    public ProducerDto createProducer(ProducerRequestDto requestDto) {
        if (producerRepository.existsByName(requestDto.getName())) {
            throw new DuplicateProducerNameException(
                    "Producer with name " + requestDto.getName() + " already exists");
        }
        Producer producer = new Producer();
        producer.setName(requestDto.getName());
        producer.setCountry(requestDto.getCountry());
        Producer saved = producerRepository.save(producer);

        notificationService.sendProducerCreatedNotification(saved);

        return convertToDto(saved);
    }

    @Transactional
    public ProducerDto updateProducer(Long id, ProducerRequestDto requestDto) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producer with id" + id + " not found"
                ));
        if (producerRepository.existsByNameAndId(requestDto.getName(), id)) {
            throw new DuplicateProducerNameException("Producer with name" + requestDto.getName() + " already exists");
        }
        producer.setName(requestDto.getName());
        producer.setCountry(requestDto.getCountry());
        Producer updated = producerRepository.save(producer);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteProducer(Long id) {

        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producer not found with id: " + id
                ));
        String producerName = producer.getName();

        try {
            producerRepository.deleteById(id);
            notificationService.sendProducerDeletedNotification(id, producerName);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot delete producer because it has associated movies.", e
            );
        }
    }

    public ProducerDto getProducerById(Long id) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producer not found"));
        return convertToDto(producer);
    }

    public ProducerDto getProducerByName(String name) {
        Producer producer = producerRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Producer with name" + name + " not found"
                ));
        return convertToDto(producer);
    }

    private ProducerDto convertToDto(Producer producer) {
        ProducerDto dto = new ProducerDto();
        dto.setId(producer.getId());
        dto.setName(producer.getName());
        dto.setCountry(producer.getCountry());
        return dto;
    }

    public boolean existById(Long id) {
        return producerRepository.existsById(id);
    }

    public boolean existByName(String name) {
        return producerRepository.existsByName(name);
    }
}