package suprim.sepay.exception;

/**
 * Exception for webhook-specific errors.
 * Kept for backward compatibility.
 */
public class SePayWebhookException extends SePayException {

    public SePayWebhookException(String message) {
        super(message);
    }

    public SePayWebhookException(String message, Throwable cause) {
        super(message, cause);
    }
}
