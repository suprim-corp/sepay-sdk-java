package suprim.sepay.exception;

/**
 * Exception thrown when webhook payload validation fails.
 */
public class SePayValidationException extends SePayWebhookException {

    public SePayValidationException(String message) {
        super(message);
    }

    public SePayValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
