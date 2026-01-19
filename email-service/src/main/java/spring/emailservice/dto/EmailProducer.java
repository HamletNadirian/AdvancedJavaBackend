package spring.emailservice.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.email.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.email.routingkey}")
    private String routingKey;

    public void sendEmailMessage(EmailDto emailDto) {
        rabbitTemplate.convertAndSend(exchange, routingKey, emailDto);
        log.info("Email message sent to RabbitMQ exchange: {}, routing key: {}", exchange, routingKey);
    }
}