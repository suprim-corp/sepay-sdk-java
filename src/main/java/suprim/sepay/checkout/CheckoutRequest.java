package suprim.sepay.checkout;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Immutable checkout request containing all payment form data.
 */
public class CheckoutRequest {

    private final String merchant;
    private final String env;
    private final Operation operation;
    private final PaymentMethod paymentMethod;
    private final long orderAmount;
    private final String currency;
    private final String orderInvoiceNumber;
    private final String orderDescription;
    private final String customerId;
    private final String successUrl;
    private final String errorUrl;
    private final String cancelUrl;
    private final String signature;

    // Agreement fields (for recurring payments)
    private final String agreementId;
    private final String agreementName;
    private final String agreementType;
    private final String agreementPaymentFrequency;
    private final String agreementAmountPerPayment;

    CheckoutRequest(Builder builder) {
        this.merchant = builder.merchant;
        this.env = builder.env;
        this.operation = builder.operation;
        this.paymentMethod = builder.paymentMethod;
        this.orderAmount = builder.orderAmount;
        this.currency = builder.currency;
        this.orderInvoiceNumber = builder.orderInvoiceNumber;
        this.orderDescription = builder.orderDescription;
        this.customerId = builder.customerId;
        this.successUrl = builder.successUrl;
        this.errorUrl = builder.errorUrl;
        this.cancelUrl = builder.cancelUrl;
        this.signature = builder.signature;
        this.agreementId = builder.agreementId;
        this.agreementName = builder.agreementName;
        this.agreementType = builder.agreementType;
        this.agreementPaymentFrequency = builder.agreementPaymentFrequency;
        this.agreementAmountPerPayment = builder.agreementAmountPerPayment;
    }

    /**
     * Converts request to map for signature generation.
     * Field order matches PHP SDK specification.
     */
    public Map<String, String> toSignatureMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("merchant", nullToEmpty(merchant));
        map.put("env", nullToEmpty(env));
        map.put("operation", nonNull(operation) ? operation.getValue() : "");
        map.put("payment_method", nonNull(paymentMethod) ? paymentMethod.getValue() : "");
        map.put("order_amount", String.valueOf(orderAmount));
        map.put("currency", nullToEmpty(currency));
        map.put("order_invoice_number", nullToEmpty(orderInvoiceNumber));
        map.put("order_description", nullToEmpty(orderDescription));
        map.put("customer_id", nullToEmpty(customerId));
        map.put("agreement_id", nullToEmpty(agreementId));
        map.put("agreement_name", nullToEmpty(agreementName));
        map.put("agreement_type", nullToEmpty(agreementType));
        map.put("agreement_payment_frequency", nullToEmpty(agreementPaymentFrequency));
        map.put("agreement_amount_per_payment", nullToEmpty(agreementAmountPerPayment));
        map.put("success_url", nullToEmpty(successUrl));
        map.put("error_url", nullToEmpty(errorUrl));
        map.put("cancel_url", nullToEmpty(cancelUrl));
        return map;
    }

    /**
     * Converts request to form fields map (includes signature).
     */
    public Map<String, String> toFormFields() {
        Map<String, String> fields = toSignatureMap();
        fields.put("signature", nullToEmpty(signature));
        return fields;
    }

    private String nullToEmpty(String value) {
        return nonNull(value) ? value : "";
    }

    // Getters
    public String getMerchant() { return merchant; }
    public String getEnv() { return env; }
    public Operation getOperation() { return operation; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public long getOrderAmount() { return orderAmount; }
    public String getCurrency() { return currency; }
    public String getOrderInvoiceNumber() { return orderInvoiceNumber; }
    public String getOrderDescription() { return orderDescription; }
    public String getCustomerId() { return customerId; }
    public String getSuccessUrl() { return successUrl; }
    public String getErrorUrl() { return errorUrl; }
    public String getCancelUrl() { return cancelUrl; }
    public String getSignature() { return signature; }
    public String getAgreementId() { return agreementId; }
    public String getAgreementName() { return agreementName; }
    public String getAgreementType() { return agreementType; }
    public String getAgreementPaymentFrequency() { return agreementPaymentFrequency; }
    public String getAgreementAmountPerPayment() { return agreementAmountPerPayment; }

    static class Builder {
        String merchant;
        String env;
        Operation operation;
        PaymentMethod paymentMethod;
        long orderAmount;
        String currency = "VND";
        String orderInvoiceNumber;
        String orderDescription;
        String customerId;
        String successUrl;
        String errorUrl;
        String cancelUrl;
        String signature;
        String agreementId;
        String agreementName;
        String agreementType;
        String agreementPaymentFrequency;
        String agreementAmountPerPayment;

        CheckoutRequest build() {
            return new CheckoutRequest(this);
        }
    }
}
