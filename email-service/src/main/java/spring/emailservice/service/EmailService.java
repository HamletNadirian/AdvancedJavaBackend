package spring.emailservice.service;

import spring.emailservice.entity.EmailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmailService {
    EmailEntity sendEmail(EmailEntity emailEntity);

    Page<EmailEntity> findAll(Pageable pageable);

    Optional<EmailEntity> findById(String id);
}
