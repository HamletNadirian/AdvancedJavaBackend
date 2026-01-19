package springboot.restapi.dto;

import lombok.Data;

@Data
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