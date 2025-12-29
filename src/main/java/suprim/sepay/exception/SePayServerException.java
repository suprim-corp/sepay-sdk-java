package suprim.sepay.exception;

/**
 * Exception for server-side errors (5xx).
 */
public class SePayServerException extends SePayApiException {

    public SePayServerException(String message, int statusCode) {
        super(message, statusCode);
    }

    public SePayServerException(String message, int statusCode, Throwable cause) {
        super(message, statusCode, cause);
    }
}
