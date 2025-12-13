package suprim.sepay.client;

import org.junit.jupiter.api.Test;
import suprim.sepay.checkout.CheckoutBuilder;
import suprim.sepay.checkout.CheckoutResource;
import suprim.sepay.config.Environment;
import suprim.sepay.order.OrderResource;

import static org.junit.jupiter.api.Assertions.*;

class SePayClientTest {

    private static final String MERCHANT_ID = "TEST_MERCHANT";
    private static final String SECRET_KEY = "test_secret_key";

    @Test
    void builder_createsClient() {
        SePayClientConfig config = SePayClient.builder(MERCHANT_ID, SECRET_KEY).build();
        SePayClient client = SePayClient.create(config);

        assertNotNull(client);
        assertEquals(MERCHANT_ID, client.getMerchantId());
    }

    @Test
    void builder_nullMerchant_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SePayClient.builder(null, SECRET_KEY));
    }

    @Test
    void builder_emptyMerchant_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SePayClient.builder("", SECRET_KEY));
    }

    @Test
    void builder_nullSecret_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SePayClient.builder(MERCHANT_ID, null));
    }

    @Test
    void create_nullConfig_throws() {
        assertThrows(NullPointerException.class,
                () -> SePayClient.create(null));
    }

    @Test
    void checkout_lazyInit() {
        SePayClient client = createClient();

        CheckoutResource checkout1 = client.checkout();
        CheckoutResource checkout2 = client.checkout();

        assertNotNull(checkout1);
        assertSame(checkout1, checkout2);
    }

    @Test
    void orders_lazyInit() {
        SePayClient client = createClient();

        OrderResource orders1 = client.orders();
        OrderResource orders2 = client.orders();

        assertNotNull(orders1);
        assertSame(orders1, orders2);
    }

    @Test
    void newCheckout_preconfigured() {
        SePayClient client = createClient(Environment.PRODUCTION);

        CheckoutBuilder builder = client.newCheckout();

        assertNotNull(builder);
    }

    @Test
    void getEnvironment_returnsConfigValue() {
        SePayClient sandboxClient = createClient(Environment.SANDBOX);
        SePayClient prodClient = createClient(Environment.PRODUCTION);

        assertEquals(Environment.SANDBOX, sandboxClient.getEnvironment());
        assertEquals(Environment.PRODUCTION, prodClient.getEnvironment());
    }

    @Test
    void getMerchantId_returnsConfigValue() {
        SePayClient client = createClient();

        assertEquals(MERCHANT_ID, client.getMerchantId());
    }

    @Test
    void getHttpClient_returnsNonNull() {
        SePayClient client = createClient();

        assertNotNull(client.getHttpClient());
    }

    @Test
    void defaultEnvironment_isSandbox() {
        SePayClientConfig config = SePayClient.builder(MERCHANT_ID, SECRET_KEY).build();
        SePayClient client = SePayClient.create(config);

        assertEquals(Environment.SANDBOX, client.getEnvironment());
    }

    private SePayClient createClient() {
        return createClient(Environment.SANDBOX);
    }

    private SePayClient createClient(Environment environment) {
        SePayClientConfig config = SePayClient.builder(MERCHANT_ID, SECRET_KEY)
                .environment(environment)
                .build();
        return SePayClient.create(config);
    }
}
