package springboot.restapi.exception;


import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    // Конструкторы
    public ValidationErrorResponse() {
        super();
    }

    public ValidationErrorResponse(int status, String message,
                                   Map<String, String> errors,
                                   LocalDateTime timestamp) {
        super(status, message, timestamp);
        this.errors = errors;
    }

    // Getter and Setter
    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}