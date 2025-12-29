package suprim.sepay.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UrlConfig.
 */
class UrlConfigTest {

    @Test
    void testSandboxApiBaseUrl() {
        String url = UrlConfig.getApiBaseUrl(Environment.SANDBOX);
        assertEquals("https://pgapi-sandbox.sepay.vn", url);
    }

    @Test
    void testProductionApiBaseUrl() {
        String url = UrlConfig.getApiBaseUrl(Environment.PRODUCTION);
        assertEquals("https://pgapi.sepay.vn", url);
    }

    @Test
    void testSandboxCheckoutBaseUrl() {
        String url = UrlConfig.getCheckoutBaseUrl(Environment.SANDBOX);
        assertEquals("https://pay-sandbox.sepay.vn", url);
    }

    @Test
    void testProductionCheckoutBaseUrl() {
        String url = UrlConfig.getCheckoutBaseUrl(Environment.PRODUCTION);
        assertEquals("https://pay.sepay.vn", url);
    }

    @Test
    void testCheckoutInitUrl() {
        String url = UrlConfig.getCheckoutInitUrl(Environment.SANDBOX);
        assertEquals("https://pay-sandbox.sepay.vn/v1/checkout/init", url);
    }

    @Test
    void testOrderDetailUrl() {
        String url = UrlConfig.getOrderDetailUrl(Environment.SANDBOX, "ORDER123");
        assertEquals("https://pgapi-sandbox.sepay.vn/v1/order/detail/ORDER123", url);
    }

    @Test
    void testOrderListUrl() {
        String url = UrlConfig.getOrderListUrl(Environment.PRODUCTION);
        assertEquals("https://pgapi.sepay.vn/v1/order", url);
    }

    @Test
    void testVoidUrl() {
        String url = UrlConfig.getVoidUrl(Environment.SANDBOX);
        assertEquals("https://pgapi-sandbox.sepay.vn/v1/order/voidTransaction", url);
    }

    @Test
    void testCancelUrl() {
        String url = UrlConfig.getCancelUrl(Environment.PRODUCTION);
        assertEquals("https://pgapi.sepay.vn/v1/order/cancel", url);
    }
}
