package suprim.sepay.exception;

/**
 * Exception thrown when webhook authentication fails.
 */
public class SePayAuthenticationException extends SePayWebhookException {

    public SePayAuthenticationException(String message) {
        super(message);
    }

    public static SePayAuthenticationException invalidToken() {
        return new SePayAuthenticationException("Invalid or missing API key token");
    }
}
