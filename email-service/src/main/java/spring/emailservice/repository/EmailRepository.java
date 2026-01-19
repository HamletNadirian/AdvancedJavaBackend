package spring.emailservice.repository;

import spring.emailservice.dto.StatusEmail;
import spring.emailservice.entity.EmailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends ElasticsearchRepository<EmailEntity, String> {

    List<EmailEntity> findByStatusEmail(StatusEmail statusEmail);

    Page<EmailEntity> findAll(Pageable pageable);

    List<EmailEntity> findByStatusEmailAndRetryCountLessThan(StatusEmail statusEmail, Integer maxRetryCount);
}
