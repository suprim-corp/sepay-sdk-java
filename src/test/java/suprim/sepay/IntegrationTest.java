package suprim.sepay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import suprim.sepay.checkout.CheckoutRequest;
import suprim.sepay.checkout.Operation;
import suprim.sepay.client.SePayClient;
import suprim.sepay.config.Environment;
import suprim.sepay.order.OrderListResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that require real SePay credentials.
 * Run manually with environment variables set.
 *
 * <p>Set SEPAY_MERCHANT_ID and SEPAY_SECRET_KEY before running.
 */
@Tag("integration")
@Disabled("Requires real credentials - set SEPAY_MERCHANT_ID and SEPAY_SECRET_KEY")
class IntegrationTest {

    private SePayClient client;

    @BeforeEach
    void setUp() {
        String merchantId = System.getenv("SEPAY_MERCHANT_ID");
        String secretKey = System.getenv("SEPAY_SECRET_KEY");

        if (merchantId == null || secretKey == null) {
            fail("Set SEPAY_MERCHANT_ID and SEPAY_SECRET_KEY environment variables");
        }

        client = SePayClient.create(
                SePayClient.builder(merchantId, secretKey)
                        .environment(Environment.SANDBOX)
                        .build()
        );
    }

    @Test
    void sandbox_createCheckout_success() {
        CheckoutRequest checkout = client.newCheckout()
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INT-TEST-001")
                .description("Integration test payment")
                .build();

        assertNotNull(checkout);
        assertNotNull(checkout.getSignature());
        assertEquals(10000, checkout.getOrderAmount());
    }

    @Test
    void sandbox_listOrders_success() {
        OrderListResponse response = client.orders().list();

        assertNotNull(response);
        assertNotNull(response.getData());
    }

    @Test
    void sandbox_generateCheckoutForm_success() {
        CheckoutRequest checkout = client.newCheckout()
                .operation(Operation.PURCHASE)
                .amount(50000)
                .invoiceNumber("INT-TEST-002")
                .description("Form generation test")
                .successUrl("https://example.com/success")
                .build();

        var formData = client.checkout().generateForm(checkout);

        assertNotNull(formData);
        assertNotNull(formData.getActionUrl());
        assertTrue(formData.getActionUrl().contains("sepay"));
        assertNotNull(formData.getSignature());
    }

    @Test
    void production_urlsCorrect() {
        SePayClient prodClient = SePayClient.create(
                SePayClient.builder("TEST", "TEST")
                        .environment(Environment.PRODUCTION)
                        .build()
        );

        String url = prodClient.checkout().getCheckoutUrl();

        assertNotNull(url);
        assertFalse(url.contains("sandbox"));
        assertTrue(url.contains("pay.sepay.vn"));
    }
}
