package suprim.sepay.exception;

/**
 * Base exception for all SePay SDK errors.
 */
public class SePayException extends RuntimeException {

    public SePayException(String message) {
        super(message);
    }

    public SePayException(String message, Throwable cause) {
        super(message, cause);
    }
}
