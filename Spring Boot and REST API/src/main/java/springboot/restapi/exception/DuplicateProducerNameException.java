package springboot.restapi.exception;

public class DuplicateProducerNameException extends RuntimeException {
    public DuplicateProducerNameException(String message) {
        super(message);
    }
}