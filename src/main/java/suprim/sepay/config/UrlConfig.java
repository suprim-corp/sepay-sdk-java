package suprim.sepay.config;

/**
 * URL configuration for SePay API endpoints.
 */
public final class UrlConfig {

    // Sandbox URLs
    private static final String SANDBOX_API_BASE = "https://pgapi-sandbox.sepay.vn";
    private static final String SANDBOX_CHECKOUT_BASE = "https://pay-sandbox.sepay.vn";

    // Production URLs
    private static final String PROD_API_BASE = "https://pgapi.sepay.vn";
    private static final String PROD_CHECKOUT_BASE = "https://pay.sepay.vn";

    // API version prefix
    private static final String API_VERSION = "/v1";

    private UrlConfig() {
        // Utility class
    }

    /**
     * Get API base URL for environment.
     */
    public static String getApiBaseUrl(Environment environment) {
        return environment == Environment.PRODUCTION ? PROD_API_BASE : SANDBOX_API_BASE;
    }

    /**
     * Get checkout base URL for environment.
     */
    public static String getCheckoutBaseUrl(Environment environment) {
        return environment == Environment.PRODUCTION ? PROD_CHECKOUT_BASE : SANDBOX_CHECKOUT_BASE;
    }

    /**
     * Get checkout init URL.
     */
    public static String getCheckoutInitUrl(Environment environment) {
        return getCheckoutBaseUrl(environment) + API_VERSION + "/checkout/init";
    }

    /**
     * Get order detail URL.
     */
    public static String getOrderDetailUrl(Environment environment, String orderId) {
        return getApiBaseUrl(environment) + API_VERSION + "/order/detail/" + orderId;
    }

    /**
     * Get order list URL.
     */
    public static String getOrderListUrl(Environment environment) {
        return getApiBaseUrl(environment) + API_VERSION + "/order";
    }

    /**
     * Get void transaction URL.
     */
    public static String getVoidUrl(Environment environment) {
        return getApiBaseUrl(environment) + API_VERSION + "/order/voidTransaction";
    }

    /**
     * Get cancel order URL.
     */
    public static String getCancelUrl(Environment environment) {
        return getApiBaseUrl(environment) + API_VERSION + "/order/cancel";
    }
}
