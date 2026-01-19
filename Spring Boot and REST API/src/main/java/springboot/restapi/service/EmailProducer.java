package springboot.restapi.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springboot.restapi.dto.EmailNotificationDto;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.email.exchange:email-exchange}")
    private String exchange;

    @Value("${rabbitmq.email.routing-key:email.routing.key}")
    private String routingKey;

    public void sendEmailNotification(EmailNotificationDto emailDto) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, emailDto);
            log.info("Email notification sent to RabbitMQ for: {}", emailDto.getEmailTo());
        } catch (Exception e) {
            log.error("Failed to send email notification to RabbitMQ: {}", e.getMessage());
        }
    }

}