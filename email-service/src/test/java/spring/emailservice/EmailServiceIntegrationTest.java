//package spring.emailservice;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.RabbitMQContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import jakarta.mail.internet.MimeMessage;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
//class EmailServiceIntegrationTest {
//
//    @Container
//    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(
//            "rabbitmq:3.12-management-alpine"
//    );
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
//        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
//        registry.add("spring.rabbitmq.username", () -> "guest");
//        registry.add("spring.rabbitmq.password", () -> "guest");
//
//        registry.add("spring.mail.enabled", () -> "false");
//    }
//
//    @Autowired
//    private EmailService emailService;
//
//    @MockBean
//    private JavaMailSender mailSender;
//
//    @MockBean
//    private MimeMessage mimeMessage;
//
//    @BeforeEach
//    void setUp() {
//        reset(mailSender, mimeMessage);
//    }
//
//    @Test
//    @DisplayName("Успешная отправка email без вложения")
//    void sendEmail_Success_WithoutAttachment() throws Exception {
//        EmailRequest request = new EmailRequest();
//        request.setTo(Arrays.asList("test@example.com", "admin@example.com"));
//        request.setSubject("Test Subject");
//        request.setBody("Test Body Content");
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//        emailService.sendEmail(request);
//        verify(mailSender, times(1)).createMimeMessage();
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    @DisplayName("Успешная отправка email с вложением")
//    void sendEmail_Success_WithAttachment() throws Exception {
//
//        EmailRequest request = new EmailRequest();
//        request.setTo(List.of("test@example.com"));
//        request.setSubject("Test Subject with Attachment");
//        request.setBody("Test Body");
//        request.setAttachmentName("document.pdf");
//        request.setAttachment("PDF content".getBytes());
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        emailService.sendEmail(request);
//
//        verify(mailSender, times(1)).createMimeMessage();
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    @DisplayName("Неуспешная отправка email - SMTP ошибка")
//    void sendEmail_Failure_SmtpError() throws Exception {
//
//        EmailRequest request = new EmailRequest();
//        request.setTo(List.of("test@example.com"));
//        request.setSubject("Test Subject");
//        request.setBody("Test Body");
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doThrow(new RuntimeException("SMTP server unavailable"))
//                .when(mailSender).send(any(MimeMessage.class));
//
//        assertThatThrownBy(() -> emailService.sendEmail(request))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Email sending failed");
//
//        verify(mailSender, times(1)).createMimeMessage();
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    @DisplayName("Неуспешная отправка email - неверный адрес получателя")
//    void sendEmail_Failure_InvalidEmailAddress() throws Exception {
//        EmailRequest request = new EmailRequest();
//        request.setTo(List.of("invalid-email"));
//        request.setSubject("Test Subject");
//        request.setBody("Test Body");
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doThrow(new jakarta.mail.internet.AddressException("Invalid address"))
//                .when(mailSender).send(any(MimeMessage.class));
//
//        assertThatThrownBy(() -> emailService.sendEmail(request))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Email sending failed");
//
//        verify(mailSender, times(1)).createMimeMessage();
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    @DisplayName("Отправка email пустому списку получателей")
//    void sendEmail_EmptyRecipientList() {
//        EmailRequest request = new EmailRequest();
//        request.setTo(List.of());
//        request.setSubject("Test Subject");
//        request.setBody("Test Body");
//
//        assertThatThrownBy(() -> emailService.sendEmail(request))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("No recipients specified");
//    }
//
//    @Test
//    @DisplayName("Отправка email с null телом")
//    void sendEmail_NullBody() {
//
//        EmailRequest request = new EmailRequest();
//        request.setTo(List.of("test@example.com"));
//        request.setSubject("Test Subject");
//        request.setBody(null);
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        assertThatThrownBy(() -> emailService.sendEmail(request))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Email body cannot be null or empty");
//    }
//}