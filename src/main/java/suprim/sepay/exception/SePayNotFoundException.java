package suprim.sepay.exception;

/**
 * Exception for 404 Not Found responses from SePay API.
 * Thrown when requested resource (order, transaction) doesn't exist.
 */
public class SePayNotFoundException extends SePayApiException {

    public SePayNotFoundException(String message) {
        super(message, 404, "NOT_FOUND");
    }

    public SePayNotFoundException(String message, Throwable cause) {
        super(message, 404, "NOT_FOUND", cause);
    }
}
