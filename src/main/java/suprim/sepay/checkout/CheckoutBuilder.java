package suprim.sepay.checkout;

import suprim.sepay.auth.SignatureGenerator;
import suprim.sepay.config.Environment;
import suprim.sepay.exception.SePayValidationException;

import java.net.MalformedURLException;
import java.net.URL;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Fluent builder for creating checkout requests with validation.
 */
public class CheckoutBuilder {

    private static final String CURRENCY = "VND";
    private static final int MAX_INVOICE_LENGTH = 100;

    private final String merchant;
    private final String secretKey;
    private Environment environment = Environment.SANDBOX;

    private Operation operation;
    private PaymentMethod paymentMethod;
    private long orderAmount;
    private String orderInvoiceNumber;
    private String orderDescription;
    private String customerId;
    private String successUrl;
    private String errorUrl;
    private String cancelUrl;

    // Agreement fields
    private String agreementId;
    private String agreementName;
    private String agreementType;
    private String agreementPaymentFrequency;
    private String agreementAmountPerPayment;

    private CheckoutBuilder(String merchant, String secretKey) {
        this.merchant = merchant;
        this.secretKey = secretKey;
    }

    /**
     * Creates a new checkout builder.
     *
     * @param merchant  the merchant ID
     * @param secretKey the secret key for signature generation
     * @return new builder instance
     */
    public static CheckoutBuilder create(String merchant, String secretKey) {
        if (isNull(merchant) || merchant.isEmpty()) {
            throw new SePayValidationException("Merchant ID is required");
        }
        if (isNull(secretKey) || secretKey.isEmpty()) {
            throw new SePayValidationException("Secret key is required");
        }
        return new CheckoutBuilder(merchant, secretKey);
    }

    public CheckoutBuilder environment(Environment env) {
        this.environment = nonNull(env) ? env : Environment.SANDBOX;
        return this;
    }

    public CheckoutBuilder operation(Operation operation) {
        this.operation = operation;
        return this;
    }

    public CheckoutBuilder paymentMethod(PaymentMethod method) {
        this.paymentMethod = method;
        return this;
    }

    public CheckoutBuilder amount(long amount) {
        this.orderAmount = amount;
        return this;
    }

    public CheckoutBuilder invoiceNumber(String invoice) {
        this.orderInvoiceNumber = invoice;
        return this;
    }

    public CheckoutBuilder description(String description) {
        this.orderDescription = description;
        return this;
    }

    public CheckoutBuilder customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public CheckoutBuilder successUrl(String url) {
        this.successUrl = url;
        return this;
    }

    public CheckoutBuilder errorUrl(String url) {
        this.errorUrl = url;
        return this;
    }

    public CheckoutBuilder cancelUrl(String url) {
        this.cancelUrl = url;
        return this;
    }

    public CheckoutBuilder agreementId(String id) {
        this.agreementId = id;
        return this;
    }

    public CheckoutBuilder agreementName(String name) {
        this.agreementName = name;
        return this;
    }

    public CheckoutBuilder agreementType(String type) {
        this.agreementType = type;
        return this;
    }

    public CheckoutBuilder agreementPaymentFrequency(String frequency) {
        this.agreementPaymentFrequency = frequency;
        return this;
    }

    public CheckoutBuilder agreementAmountPerPayment(String amount) {
        this.agreementAmountPerPayment = amount;
        return this;
    }

    /**
     * Builds the checkout request with validation and signature computation.
     *
     * @return validated checkout request
     * @throws SePayValidationException if validation fails
     */
    public CheckoutRequest build() {
        validate();

        CheckoutRequest.Builder builder = new CheckoutRequest.Builder();
        builder.merchant = merchant;
        builder.env = environment.name().toLowerCase();
        builder.operation = operation;
        builder.paymentMethod = paymentMethod;
        builder.orderAmount = orderAmount;
        builder.currency = CURRENCY;
        builder.orderInvoiceNumber = orderInvoiceNumber;
        builder.orderDescription = orderDescription;
        builder.customerId = customerId;
        builder.successUrl = successUrl;
        builder.errorUrl = errorUrl;
        builder.cancelUrl = cancelUrl;
        builder.agreementId = agreementId;
        builder.agreementName = agreementName;
        builder.agreementType = agreementType;
        builder.agreementPaymentFrequency = agreementPaymentFrequency;
        builder.agreementAmountPerPayment = agreementAmountPerPayment;

        // Build request without signature first to get signature map
        CheckoutRequest tempRequest = builder.build();
        SignatureGenerator sigGen = new SignatureGenerator(secretKey);
        builder.signature = sigGen.generateSignature(tempRequest.toSignatureMap());

        return builder.build();
    }

    /**
     * Convenience method for PURCHASE operation.
     */
    public CheckoutRequest purchase(long amount, String invoice, String description) {
        return this.operation(Operation.PURCHASE)
                .amount(amount)
                .invoiceNumber(invoice)
                .description(description)
                .build();
    }

    /**
     * Convenience method for VERIFY operation.
     */
    public CheckoutRequest verify(String description) {
        return this.operation(Operation.VERIFY)
                .amount(0)
                .description(description)
                .build();
    }

    private void validate() {
        if (isNull(operation)) {
            throw new SePayValidationException("Operation is required");
        }

        if (isNull(orderDescription) || orderDescription.isEmpty()) {
            throw new SePayValidationException("Order description is required");
        }

        if (operation == Operation.PURCHASE) {
            validatePurchase();
        } else if (operation == Operation.VERIFY) {
            validateVerify();
        }

        validateUrls();
    }

    private void validatePurchase() {
        if (orderAmount <= 0) {
            throw new SePayValidationException("PURCHASE requires amount > 0");
        }

        if (isNull(orderInvoiceNumber) || orderInvoiceNumber.isEmpty()) {
            throw new SePayValidationException("PURCHASE requires invoice number");
        }

        if (orderInvoiceNumber.length() > MAX_INVOICE_LENGTH) {
            throw new SePayValidationException(
                    "Invoice number must be max " + MAX_INVOICE_LENGTH + " characters");
        }

        if (!isAlphanumericWithDash(orderInvoiceNumber)) {
            throw new SePayValidationException(
                    "Invoice number must be alphanumeric (hyphens and underscores allowed)");
        }
    }

    private void validateVerify() {
        if (orderAmount != 0) {
            throw new SePayValidationException("VERIFY requires amount = 0");
        }
    }

    private void validateUrls() {
        if (nonNull(successUrl) && !isValidUrl(successUrl)) {
            throw new SePayValidationException("Invalid success URL");
        }
        if (nonNull(errorUrl) && !isValidUrl(errorUrl)) {
            throw new SePayValidationException("Invalid error URL");
        }
        if (nonNull(cancelUrl) && !isValidUrl(cancelUrl)) {
            throw new SePayValidationException("Invalid cancel URL");
        }
    }

    private boolean isAlphanumericWithDash(String str) {
        return str.matches("^[a-zA-Z0-9_-]+$");
    }

    private boolean isValidUrl(String urlStr) {
        try {
            new URL(urlStr);
            return urlStr.startsWith("http://") || urlStr.startsWith("https://");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
