package spring.emailservice.controller;

import spring.emailservice.dto.EmailDto;
import spring.emailservice.dto.EmailProducer;
import spring.emailservice.entity.EmailEntity;
import spring.emailservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;
    private final EmailProducer emailProducer;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody @Valid EmailDto emailDto) {
        log.info("Received email request for: {}", emailDto.getEmailTo());

        EmailEntity emailEntity = new EmailEntity();
        BeanUtils.copyProperties(emailDto, emailEntity);
        emailEntity.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        emailEntity.setStatusEmail(spring.emailservice.dto.StatusEmail.PROCESSING);

        EmailEntity savedEmail = emailService.sendEmail(emailEntity);
        log.info("Email saved with ID: {}", savedEmail.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Email processing started");
        response.put("id", savedEmail.getId());
        response.put("status", savedEmail.getStatusEmail().toString());
        response.put("to", savedEmail.getEmailTo());
        response.put("subject", savedEmail.getSubject());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/send-async")
    public ResponseEntity<String> sendEmailAsync(@RequestBody @Valid EmailDto emailDto) {
        log.info("Sending email message to RabbitMQ for: {}", emailDto.getEmailTo());

        emailProducer.sendEmailMessage(emailDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Email message sent to RabbitMQ for processing");
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Ping endpoint called");
        return ResponseEntity.ok("Email service is running!");
    }
}