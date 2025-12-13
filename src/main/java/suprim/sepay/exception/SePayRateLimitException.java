package suprim.sepay.exception;

/**
 * Exception thrown when rate limit (429) is exceeded.
 */
public class SePayRateLimitException extends SePayApiException {

    private final Long retryAfterSeconds;

    public SePayRateLimitException(String message) {
        super(message, 429);
        this.retryAfterSeconds = null;
    }

    public SePayRateLimitException(String message, Long retryAfterSeconds) {
        super(message, 429);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public SePayRateLimitException(String message, Throwable cause) {
        super(message, 429, cause);
        this.retryAfterSeconds = null;
    }

    public Long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
