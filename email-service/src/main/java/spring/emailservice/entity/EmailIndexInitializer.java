package spring.emailservice.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndex() {
        try {
            if (!elasticsearchOperations.indexOps(EmailEntity.class).exists()) {
                elasticsearchOperations.indexOps(EmailEntity.class).create();
                log.info("Email index created successfully");
            } else {
                log.info("Email index already exists");
            }
        } catch (Exception e) {
            log.error("Failed to create email index", e);
        }
    }
}