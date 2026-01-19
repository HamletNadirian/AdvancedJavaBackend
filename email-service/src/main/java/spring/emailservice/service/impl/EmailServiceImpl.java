package spring.emailservice.service.impl;

import spring.emailservice.dto.StatusEmail;
import spring.emailservice.entity.EmailEntity;
import spring.emailservice.repository.EmailRepository;
import spring.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;

    private static final DateTimeFormatter ELASTICSEARCH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public EmailEntity sendEmail(EmailEntity emailEntity) {
        emailEntity.setCreatedAt(formatForElasticsearch(LocalDateTime.now()));
        emailEntity.setStatusEmail(StatusEmail.PROCESSING);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailEntity.getEmailFrom());
            message.setTo(emailEntity.getEmailTo());
            message.setSubject(emailEntity.getSubject());
            message.setText(emailEntity.getText());

            emailSender.send(message);

            emailEntity.setStatusEmail(StatusEmail.SENT);
            emailEntity.setSendDateEmail(formatForElasticsearch(LocalDateTime.now()));
            emailEntity.setErrorMessage(null);

            log.info("Email sent successfully to: {}", emailEntity.getEmailTo());

        } catch (MailException e) {
            emailEntity.setStatusEmail(StatusEmail.ERROR);
            emailEntity.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
            emailEntity.setLastRetryTime(LocalDateTime.now() .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            emailEntity.setRetryCount(emailEntity.getRetryCount() != null ?
                    emailEntity.getRetryCount() + 1 : 1);
            log.error("Failed to send email: {}", e.getMessage());
        }

        return emailRepository.save(emailEntity);
    }
    private String formatForElasticsearch(LocalDateTime dateTime) {
        return dateTime.format(ELASTICSEARCH_DATE_FORMAT);
    }
    public EmailEntity retrySendEmail(EmailEntity emailEntity) {
        emailEntity.setLastRetryTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailEntity.getEmailFrom());
            message.setTo(emailEntity.getEmailTo());
            message.setSubject(emailEntity.getSubject());
            message.setText(emailEntity.getText());

            emailSender.send(message);

            emailEntity.setStatusEmail(StatusEmail.SENT);
            emailEntity.setSendDateEmail(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            emailEntity.setErrorMessage(null);

            log.info("Email resent successfully to: {} (attempt {})",
                    emailEntity.getEmailTo(), emailEntity.getRetryCount());

        } catch (MailException e) {
            emailEntity.setStatusEmail(StatusEmail.ERROR);
            emailEntity.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
            emailEntity.setRetryCount(emailEntity.getRetryCount() + 1);

            log.error("Failed to resend email (attempt {}): {}",
                    emailEntity.getRetryCount(), e.getMessage());
        }

        return emailRepository.save(emailEntity);
    }

    @Scheduled(fixedDelay = 300000)
    public void retryFailedEmails() {
        log.info("Starting retry of failed emails...");

        List<EmailEntity> failedEmails = emailRepository
                .findByStatusEmailAndRetryCountLessThan(StatusEmail.ERROR, 5);

        log.info("Found {} failed emails to retry", failedEmails.size());

        for (EmailEntity email : failedEmails) {
            try {
                log.info("Retrying email ID: {} to: {} (attempt {})",
                        email.getId(), email.getEmailTo(), email.getRetryCount() + 1);

                retrySendEmail(email);

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("Error retrying email ID: {}", email.getId(), e);
            }
        }

        log.info("Finished retry of failed emails");
    }

    @Override
    public Page<EmailEntity> findAll(Pageable pageable) {
        return emailRepository.findAll(pageable);
    }

    @Override
    public Optional<EmailEntity> findById(String id) {
        return emailRepository.findById(id);
    }

    public List<EmailEntity> findByStatusEmail(StatusEmail status) {
        return emailRepository.findByStatusEmail(status);
    }
}