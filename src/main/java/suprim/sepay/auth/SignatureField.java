package suprim.sepay.auth;

/**
 * Enumeration of fields used in signature generation.
 * Order must match PHP SDK exactly for cross-platform compatibility.
 */
public enum SignatureField {
    MERCHANT("merchant"),
    ENV("env"),
    OPERATION("operation"),
    PAYMENT_METHOD("payment_method"),
    ORDER_AMOUNT("order_amount"),
    CURRENCY("currency"),
    ORDER_INVOICE_NUMBER("order_invoice_number"),
    ORDER_DESCRIPTION("order_description"),
    CUSTOMER_ID("customer_id"),
    AGREEMENT_ID("agreement_id"),
    AGREEMENT_NAME("agreement_name"),
    AGREEMENT_TYPE("agreement_type"),
    AGREEMENT_PAYMENT_FREQUENCY("agreement_payment_frequency"),
    AGREEMENT_AMOUNT_PER_PAYMENT("agreement_amount_per_payment"),
    SUCCESS_URL("success_url"),
    ERROR_URL("error_url"),
    CANCEL_URL("cancel_url");

    private final String fieldName;

    SignatureField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
