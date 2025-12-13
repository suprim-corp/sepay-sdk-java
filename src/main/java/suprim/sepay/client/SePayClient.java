package suprim.sepay.client;

import suprim.sepay.checkout.CheckoutBuilder;
import suprim.sepay.checkout.CheckoutResource;
import suprim.sepay.config.Environment;
import suprim.sepay.order.OrderResource;

import java.util.Objects;

/**
 * Main entry point for SePay SDK.
 *
 * <p>Example usage:
 * <pre>{@code
 * SePayClient client = SePayClient.builder("MERCHANT_ID", "SECRET_KEY")
 *     .environment(Environment.SANDBOX)
 *     .build();
 *
 * // Create checkout
 * CheckoutRequest checkout = client.newCheckout()
 *     .operation(Operation.PURCHASE)
 *     .amount(100000)
 *     .invoiceNumber("INV-001")
 *     .description("Order payment")
 *     .build();
 *
 * // Get order
 * Order order = client.orders().retrieve("ord_123");
 * }</pre>
 */
public class SePayClient {

    private final SePayClientConfig config;
    private final SePayHttpClient httpClient;

    // Lazy-initialized resources
    private CheckoutResource checkoutResource;
    private OrderResource orderResource;

    private SePayClient(SePayClientConfig config) {
        this.config = config;
        this.httpClient = new SePayHttpClient(config);
    }

    /**
     * Creates a new client from configuration.
     *
     * @param config the client configuration
     * @return new SePayClient instance
     */
    public static SePayClient create(SePayClientConfig config) {
        Objects.requireNonNull(config, "config is required");
        return new SePayClient(config);
    }

    /**
     * Creates a configuration builder (shortcut).
     *
     * @param merchantId the merchant ID
     * @param secretKey  the secret key
     * @return configuration builder
     */
    public static SePayClientConfig.Builder builder(String merchantId, String secretKey) {
        return SePayClientConfig.builder(merchantId, secretKey);
    }

    /**
     * Returns the checkout resource for form generation.
     *
     * @return checkout resource
     */
    public CheckoutResource checkout() {
        if (checkoutResource == null) {
            checkoutResource = new CheckoutResource(config.getEnvironment());
        }
        return checkoutResource;
    }

    /**
     * Returns the orders resource for order management.
     *
     * @return orders resource
     */
    public OrderResource orders() {
        if (orderResource == null) {
            orderResource = new OrderResource(httpClient, config.getEnvironment());
        }
        return orderResource;
    }

    /**
     * Creates a new checkout builder pre-configured with client credentials.
     *
     * @return checkout builder
     */
    public CheckoutBuilder newCheckout() {
        return CheckoutBuilder.create(config.getMerchantId(), config.getSecretKey())
                .environment(config.getEnvironment());
    }

    /**
     * Returns the merchant ID.
     */
    public String getMerchantId() {
        return config.getMerchantId();
    }

    /**
     * Returns the current environment.
     */
    public Environment getEnvironment() {
        return config.getEnvironment();
    }

    /**
     * Returns the underlying HTTP client (for advanced use).
     */
    public SePayHttpClient getHttpClient() {
        return httpClient;
    }
}
