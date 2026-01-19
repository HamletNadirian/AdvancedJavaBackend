package spring.emailservice.entity;

import spring.emailservice.dto.StatusEmail;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "emails")
public class EmailEntity {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String ownerRef;

    @Field(type = FieldType.Keyword)
    private String emailFrom;

    @Field(type = FieldType.Keyword)
    private String emailTo;

    @Field(type = FieldType.Text)
    private String subject;

    @Field(type = FieldType.Text)
    private String text;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String sendDateEmail;

    @Field(type = FieldType.Keyword)
    private StatusEmail statusEmail;

    @Field(type = FieldType.Text)
    private String errorMessage;

    @Field(type = FieldType.Integer)
    private Integer retryCount = 0;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String lastRetryTime;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String createdAt;
}