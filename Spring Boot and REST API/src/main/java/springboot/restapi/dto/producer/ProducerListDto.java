package springboot.restapi.dto.producer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerListDto {
    private Long id;
    private String name;
    private String country;
}
