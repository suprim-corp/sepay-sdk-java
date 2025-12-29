package suprim.sepay.dto;

/**
 * Standard error response DTO for API errors.
 */
public class ErrorResponse {

    private final String error;
    private final String message;

    private ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorResponse invalidToken() {
        return new ErrorResponse("INVALID_TOKEN", "Authorization token is invalid or missing");
    }

    public static ErrorResponse duplicateTransaction() {
        return new ErrorResponse("DUPLICATE_TRANSACTION", "Transaction already processed");
    }

    public static ErrorResponse validationError(String message) {
        return new ErrorResponse("VALIDATION_ERROR", message);
    }
}
