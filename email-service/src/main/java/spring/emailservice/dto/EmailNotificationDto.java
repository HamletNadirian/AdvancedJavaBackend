package spring.emailservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailNotificationDto {
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String subject;
    private String text;

    private String entityType;
    private Long entityId;
    private String entityName;
    private String action;
}