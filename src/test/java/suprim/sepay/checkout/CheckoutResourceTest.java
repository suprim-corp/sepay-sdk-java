package suprim.sepay.checkout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import suprim.sepay.config.Environment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutResourceTest {

    private static final String MERCHANT = "TEST_MERCHANT";
    private static final String SECRET = "test_secret_key";

    private CheckoutResource sandboxResource;
    private CheckoutResource productionResource;

    @BeforeEach
    void setUp() {
        sandboxResource = new CheckoutResource(Environment.SANDBOX);
        productionResource = new CheckoutResource(Environment.PRODUCTION);
    }

    @Test
    void getCheckoutUrl_sandbox() {
        String url = sandboxResource.getCheckoutUrl();

        assertTrue(url.contains("sandbox"));
        assertTrue(url.contains("sepay"));
    }

    @Test
    void getCheckoutUrl_production() {
        String url = productionResource.getCheckoutUrl();

        assertFalse(url.contains("sandbox"));
        assertTrue(url.contains("sepay"));
    }

    @Test
    void generateForm_correctUrl() {
        CheckoutRequest request = createValidRequest();
        CheckoutFormData formData = sandboxResource.generateForm(request);

        assertEquals(sandboxResource.getCheckoutUrl(), formData.getActionUrl());
    }

    @Test
    void generateForm_allFieldsIncluded() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .environment(Environment.SANDBOX)
                .operation(Operation.PURCHASE)
                .paymentMethod(PaymentMethod.CARD)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test payment")
                .customerId("CUST001")
                .successUrl("https://example.com/success")
                .errorUrl("https://example.com/error")
                .cancelUrl("https://example.com/cancel")
                .build();

        CheckoutFormData formData = sandboxResource.generateForm(request);
        Map<String, String> fields = formData.getFormFields();

        assertEquals(MERCHANT, fields.get("merchant"));
        assertEquals("sandbox", fields.get("env"));
        assertEquals("PURCHASE", fields.get("operation"));
        assertEquals("CARD", fields.get("payment_method"));
        assertEquals("10000", fields.get("order_amount"));
        assertEquals("VND", fields.get("currency"));
        assertEquals("INV001", fields.get("order_invoice_number"));
        assertEquals("Test payment", fields.get("order_description"));
        assertEquals("CUST001", fields.get("customer_id"));
        assertEquals("https://example.com/success", fields.get("success_url"));
        assertEquals("https://example.com/error", fields.get("error_url"));
        assertEquals("https://example.com/cancel", fields.get("cancel_url"));
    }

    @Test
    void generateForm_signatureIncluded() {
        CheckoutRequest request = createValidRequest();
        CheckoutFormData formData = sandboxResource.generateForm(request);

        assertNotNull(formData.getSignature());
        assertFalse(formData.getSignature().isEmpty());
        assertEquals(request.getSignature(), formData.getSignature());
    }

    @Test
    void buildHtmlForm_validHtml() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildHtmlForm(request);

        assertTrue(html.startsWith("<form"));
        assertTrue(html.contains("method=\"POST\""));
        assertTrue(html.contains("action=\""));
        assertTrue(html.contains("</form>"));
    }

    @Test
    void buildHtmlForm_containsHiddenInputs() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildHtmlForm(request);

        assertTrue(html.contains("type=\"hidden\""));
        assertTrue(html.contains("name=\"merchant\""));
        assertTrue(html.contains("name=\"signature\""));
        assertTrue(html.contains("name=\"operation\""));
    }

    @Test
    void buildHtmlForm_containsSubmitButton() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildHtmlForm(request);

        assertTrue(html.contains("<button type=\"submit\">"));
        assertTrue(html.contains("Pay Now"));
    }

    @Test
    void buildHtmlForm_customSubmitLabel() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildHtmlForm(request, "Thanh toan");

        assertTrue(html.contains("Thanh toan"));
    }

    @Test
    void buildHtmlForm_nullSubmitLabel() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildHtmlForm(request, null);

        assertTrue(html.contains("<button type=\"submit\">"));
        assertTrue(html.contains("</button>"));
    }

    @Test
    void buildHtmlForm_escapesHtmlChars() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test <script>alert('xss')</script>")
                .build();

        String html = sandboxResource.buildHtmlForm(request);

        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test
    void formFields_immutable() {
        CheckoutRequest request = createValidRequest();
        CheckoutFormData formData = sandboxResource.generateForm(request);

        assertThrows(UnsupportedOperationException.class,
                () -> formData.getFormFields().put("test", "value"));
    }

    private CheckoutRequest createValidRequest() {
        return CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test payment")
                .build();
    }

    @Test
    void constructor_nullEnvironment_defaultsToSandbox() {
        CheckoutResource resource = new CheckoutResource(null);
        String url = resource.getCheckoutUrl();
        assertTrue(url.contains("sandbox"));
    }

    // Custom URL tests

    @Test
    void customCheckoutUrl_used() {
        CheckoutResource resource = new CheckoutResource(
            Environment.SANDBOX, SECRET, "https://custom.example.com"
        );
        String url = resource.getCheckoutUrl();
        assertEquals("https://custom.example.com/v1/checkout/init", url);
    }

    @Test
    void customCheckoutUrl_null_usesDefault() {
        CheckoutResource resource = new CheckoutResource(Environment.SANDBOX, SECRET, null);
        String url = resource.getCheckoutUrl();
        assertTrue(url.contains("sandbox"));
        assertTrue(url.contains("sepay"));
    }

    // verifySignature tests

    @Test
    void verifySignature_validSignature_returnsTrue() {
        CheckoutResource resource = new CheckoutResource(Environment.SANDBOX, SECRET);
        CheckoutRequest request = createValidRequest();
        Map<String, String> fields = request.toFormFields();

        // Remove signature from fields for verification
        String signature = fields.remove("signature");

        assertTrue(resource.verifySignature(fields, signature));
    }

    @Test
    void verifySignature_invalidSignature_returnsFalse() {
        CheckoutResource resource = new CheckoutResource(Environment.SANDBOX, SECRET);
        CheckoutRequest request = createValidRequest();
        Map<String, String> fields = request.toFormFields();
        fields.remove("signature");

        assertFalse(resource.verifySignature(fields, "invalid_signature"));
    }

    @Test
    void verifySignature_nullSignature_returnsFalse() {
        CheckoutResource resource = new CheckoutResource(Environment.SANDBOX, SECRET);
        assertFalse(resource.verifySignature(Map.of("test", "value"), null));
    }

    @Test
    void verifySignature_emptySignature_returnsFalse() {
        CheckoutResource resource = new CheckoutResource(Environment.SANDBOX, SECRET);
        assertFalse(resource.verifySignature(Map.of("test", "value"), ""));
    }

    @Test
    void verifySignature_noSecretKey_throwsException() {
        CheckoutResource resource = new CheckoutResource(Environment.SANDBOX);
        assertThrows(IllegalStateException.class,
            () -> resource.verifySignature(Map.of("test", "value"), "sig"));
    }

    // generateAutoSubmitScript tests

    @Test
    void generateAutoSubmitScript_defaultFormId() {
        String script = sandboxResource.generateAutoSubmitScript();
        assertTrue(script.contains("<script>"));
        assertTrue(script.contains("sepay-checkout-form"));
        assertTrue(script.contains(".submit()"));
    }

    @Test
    void generateAutoSubmitScript_customFormId() {
        String script = sandboxResource.generateAutoSubmitScript("my-form");
        assertTrue(script.contains("<script>"));
        assertTrue(script.contains("my-form"));
        assertTrue(script.contains(".submit()"));
    }

    @Test
    void generateAutoSubmitScript_nullFormId_usesDefault() {
        String script = sandboxResource.generateAutoSubmitScript(null);
        assertTrue(script.contains("sepay-checkout-form"));
    }

    @Test
    void generateAutoSubmitScript_emptyFormId_usesDefault() {
        String script = sandboxResource.generateAutoSubmitScript("");
        assertTrue(script.contains("sepay-checkout-form"));
    }

    // buildAutoSubmitForm tests

    @Test
    void buildAutoSubmitForm_containsFormAndScript() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildAutoSubmitForm(request);

        assertTrue(html.contains("<form"));
        assertTrue(html.contains("id=\"sepay-checkout-form\""));
        assertTrue(html.contains("<script>"));
        assertTrue(html.contains(".submit()"));
    }

    @Test
    void buildAutoSubmitForm_customFormId() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildAutoSubmitForm(request, "custom-form");

        assertTrue(html.contains("id=\"custom-form\""));
        assertTrue(html.contains("getElementById(\"custom-form\")"));
    }

    @Test
    void buildAutoSubmitForm_noSubmitButton() {
        CheckoutRequest request = createValidRequest();
        String html = sandboxResource.buildAutoSubmitForm(request);

        assertFalse(html.contains("<button"));
    }
}
