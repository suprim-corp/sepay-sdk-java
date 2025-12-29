package suprim.sepay.exception;

/**
 * Exception thrown when duplicate transaction is detected.
 */
public class SePayDuplicateTransactionException extends SePayWebhookException {

    private final Long transactionId;

    public SePayDuplicateTransactionException(Long transactionId) {
        super("Duplicate transaction detected: " + transactionId);
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }
}
