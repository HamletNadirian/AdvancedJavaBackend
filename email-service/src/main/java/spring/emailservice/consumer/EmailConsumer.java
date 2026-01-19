package spring.emailservice.consumer;

import spring.emailservice.dto.EmailNotificationDto;
import spring.emailservice.entity.EmailEntity;
import spring.emailservice.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${spring.rabbitmq.email.queue}")
    public void consumeEmailMessage(String message) {
        try {
            log.info("Received email message: {}", message);

            EmailNotificationDto emailDto = objectMapper.readValue(message, EmailNotificationDto.class);

            EmailEntity emailEntity = new EmailEntity();

            emailEntity.setOwnerRef(emailDto.getOwnerRef());
            emailEntity.setEmailFrom(emailDto.getEmailFrom());
            emailEntity.setEmailTo(emailDto.getEmailTo());
            emailEntity.setSubject(emailDto.getSubject());
            emailEntity.setText(emailDto.getText());

            emailEntity.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            emailEntity.setStatusEmail(spring.emailservice.dto.StatusEmail.PROCESSING);

            emailService.sendEmail(emailEntity);

        } catch (Exception e) {
            log.error("Error processing email message: {}", e.getMessage(), e);
        }
    }
}