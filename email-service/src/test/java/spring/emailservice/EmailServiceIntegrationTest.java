package spring.emailservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import spring.emailservice.dto.StatusEmail;
import spring.emailservice.entity.EmailEntity;
import spring.emailservice.repository.EmailRepository;
import spring.emailservice.service.impl.EmailServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceIntegrationTest {

    @Autowired
    private EmailServiceImpl emailService;

    @MockitoBean
    private EmailRepository emailRepository;

    @MockitoBean
    private JavaMailSender emailSender;

    private EmailEntity successfulEmail;
    private EmailEntity failedEmail;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        reset(emailRepository, emailSender);

        successfulEmail = createEmailEntity("success-id", StatusEmail.PROCESSING);
        failedEmail = createEmailEntity("failed-id", StatusEmail.ERROR);
        failedEmail.setErrorMessage("SMTP error");
        failedEmail.setRetryCount(1);
    }

    private EmailEntity createEmailEntity(String id, StatusEmail status) {
        EmailEntity entity = new EmailEntity();
        entity.setId(id);
        entity.setEmailFrom("sender@example.com");
        entity.setEmailTo("recipient@example.com");
        entity.setSubject("Test Subject");
        entity.setText("Test Body");
        entity.setStatusEmail(status);
        entity.setCreatedAt(LocalDateTime.now().format(formatter));
        return entity;
    }

    @Test
    @DisplayName("Інтеграційний тест: успішне відправлення email")
    void sendEmailIntegration_Success() {
        when(emailRepository.save(any(EmailEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        EmailEntity result = emailService.sendEmail(successfulEmail);

        assertThat(result.getStatusEmail()).isEqualTo(StatusEmail.SENT);
        assertThat(result.getErrorMessage()).isNull();
        assertThat(result.getSendDateEmail()).isNotNull();

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(any(EmailEntity.class));
    }

    @Test
    @DisplayName("Інтеграційний тест: неуспішне відправлення email с MailException")
    void sendEmailIntegration_Failure() {
        MailException mailException = new org.springframework.mail.MailSendException(
                "Connection refused: localhost:25"
        );

        when(emailRepository.save(any(EmailEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(mailException).when(emailSender).send(any(SimpleMailMessage.class));

        EmailEntity result = emailService.sendEmail(successfulEmail);

        assertThat(result.getStatusEmail()).isEqualTo(StatusEmail.ERROR);
        assertThat(result.getErrorMessage())
                .contains("MailSendException: Connection refused: localhost:25");
        assertThat(result.getRetryCount()).isEqualTo(1);
        assertThat(result.getLastRetryTime()).isNotNull();

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(any(EmailEntity.class));
    }

    @Test
    @DisplayName("Інтеграційний тест: повторне відправлення після помилки")
    void retrySendEmailIntegration_Success() {
        failedEmail.setRetryCount(2);

        when(emailRepository.save(any(EmailEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        EmailEntity result = emailService.retrySendEmail(failedEmail);

        assertThat(result.getStatusEmail()).isEqualTo(StatusEmail.SENT);
        assertThat(result.getErrorMessage()).isNull();
        assertThat(result.getSendDateEmail()).isNotNull();
        assertThat(result.getRetryCount()).isEqualTo(2);

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(any(EmailEntity.class));
    }

    @Test
    @DisplayName("Інтеграційний тест: пошук усіх email з пагінацією")
    void findAllIntegration_WithPagination() {

        List<EmailEntity> emails = Arrays.asList(successfulEmail, failedEmail);
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmailEntity> expectedPage = new PageImpl<>(emails, pageable, emails.size());

        when(emailRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<EmailEntity> result = emailService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(EmailEntity::getId)
                .contains("success-id", "failed-id");

        verify(emailRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Інтеграційний тест: пошук за статусом")
    void findByStatusEmailIntegration() {
        List<EmailEntity> errorEmails = Arrays.asList(failedEmail);
        when(emailRepository.findByStatusEmail(StatusEmail.ERROR))
                .thenReturn(errorEmails);

        List<EmailEntity> result = emailService.findByStatusEmail(StatusEmail.ERROR);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatusEmail()).isEqualTo(StatusEmail.ERROR);
        assertThat(result.get(0).getErrorMessage()).isNotNull();

        verify(emailRepository, times(1)).findByStatusEmail(StatusEmail.ERROR);
    }

    @Test
    @DisplayName("Інтеграційний тест: кілька невдалих спроб відправлення")
    void sendEmailIntegration_MultipleFailures() {
        MailException mailException = new org.springframework.mail.MailSendException("SMTP error");

        when(emailRepository.save(any(EmailEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(mailException).when(emailSender).send(any(SimpleMailMessage.class));

        EmailEntity firstAttempt = emailService.sendEmail(successfulEmail);
        assertThat(firstAttempt.getRetryCount()).isEqualTo(1);

        EmailEntity secondAttempt = emailService.retrySendEmail(firstAttempt);
        assertThat(secondAttempt.getRetryCount()).isEqualTo(2);

        EmailEntity thirdAttempt = emailService.retrySendEmail(secondAttempt);
        assertThat(thirdAttempt.getRetryCount()).isEqualTo(3);

        verify(emailSender, times(3)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(3)).save(any(EmailEntity.class));
    }

    @Test
    @DisplayName("Інтеграційний тест: перевірка логування за помилки")
    void sendEmailIntegration_ErrorLogging() {
        String errorMessage = "SMTP Authentication failed";
        MailException mailException = new org.springframework.mail.MailAuthenticationException(errorMessage);

        when(emailRepository.save(any(EmailEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(mailException).when(emailSender).send(any(SimpleMailMessage.class));

        EmailEntity result = emailService.sendEmail(successfulEmail);

        assertThat(result.getStatusEmail()).isEqualTo(StatusEmail.ERROR);
        assertThat(result.getErrorMessage())
                .contains("MailAuthenticationException: " + errorMessage);
        assertThat(result.getRetryCount()).isEqualTo(1);

        assertThat(result.getErrorMessage())
                .startsWith("org.springframework.mail.MailAuthenticationException");
    }
}