package springboot.restapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.restapi.data.Producer;
import springboot.restapi.dto.producer.ProducerDto;
import springboot.restapi.dto.producer.ProducerListDto;
import springboot.restapi.dto.producer.ProducerRequestDto;
import springboot.restapi.service.ProducerService;

import java.util.List;

@RestController
@RequestMapping("/api/producers")
@Getter
@Setter
@AllArgsConstructor
@Tag(name = "Producers", description = "API to managing producers")
public class ProducerController extends BaseController{

    private final ProducerService producerService;

    @Operation(summary = "Get all producers")
    @GetMapping
    public ResponseEntity<List<ProducerListDto>> getAll() {
        List<Producer> producers = producerService.getAllProducers();
        List<ProducerListDto> dtos = producers.stream()
                .map(this::toListDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Create a new producer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producer created"),
            @ApiResponse(responseCode = "400", description = "Invalid data or duplicate name")
    })
    @PostMapping
    public ResponseEntity<ProducerDto> create(
            @Valid @RequestBody ProducerRequestDto requestDto) {
        ProducerDto created = producerService.createProducer(requestDto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update producer")
    @PutMapping("/{id}")
    public ResponseEntity<ProducerDto> update(
            @Parameter(description = "Producer ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProducerRequestDto  dto) {
        ProducerDto updated = producerService.updateProducer(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remove producer")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Producer ID", example = "1")
            @PathVariable Long id) {
        producerService.deleteProducer(id);
        return ResponseEntity.noContent().build();
    }

    private ProducerListDto toListDto(Producer producer) {
        ProducerListDto dto = new ProducerListDto();
        dto.setId(producer.getId());
        dto.setName(producer.getName());
        dto.setCountry(producer.getCountry());
        return dto;
    }
    @Operation(summary = "Get producer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProducerDto> getById(
            @Parameter(description = "Producer ID", example = "1")
            @PathVariable Long id) {
        ProducerDto producer = producerService.getProducerById(id);
        return ResponseEntity.ok(producer);
    }
}
