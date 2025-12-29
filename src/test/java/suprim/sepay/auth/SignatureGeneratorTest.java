package suprim.sepay.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SignatureGeneratorTest {

    private SignatureGenerator generator;
    private static final String SECRET_KEY = "test_secret_key";

    @BeforeEach
    void setUp() {
        generator = new SignatureGenerator(SECRET_KEY);
    }

    @Test
    void constructor_nullSecretKey_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SignatureGenerator(null));
    }

    @Test
    void constructor_emptySecretKey_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SignatureGenerator(""));
    }

    @Test
    void buildMessage_allFieldsPresent_correctFormat() {
        Map<String, String> fields = new HashMap<>();
        fields.put("merchant", "MERCHANT123");
        fields.put("env", "sandbox");
        fields.put("operation", "PURCHASE");
        fields.put("payment_method", "BANK_TRANSFER");
        fields.put("order_amount", "100000");
        fields.put("currency", "VND");
        fields.put("order_invoice_number", "INV001");
        fields.put("order_description", "Test payment");
        fields.put("customer_id", "CUST001");
        fields.put("agreement_id", "AGR001");
        fields.put("agreement_name", "Monthly");
        fields.put("agreement_type", "SUBSCRIPTION");
        fields.put("agreement_payment_frequency", "MONTHLY");
        fields.put("agreement_amount_per_payment", "50000");
        fields.put("success_url", "https://example.com/success");
        fields.put("error_url", "https://example.com/error");
        fields.put("cancel_url", "https://example.com/cancel");

        String message = generator.buildMessage(fields);

        assertTrue(message.startsWith("merchant=MERCHANT123,env=sandbox,operation=PURCHASE"));
        assertTrue(message.contains("order_amount=100000"));
        assertTrue(message.endsWith("cancel_url=https://example.com/cancel"));
    }

    @Test
    void buildMessage_missingFields_defaultsToEmpty() {
        Map<String, String> fields = new HashMap<>();
        fields.put("merchant", "MERCHANT123");
        fields.put("env", "sandbox");

        String message = generator.buildMessage(fields);

        assertTrue(message.contains("merchant=MERCHANT123"));
        assertTrue(message.contains("env=sandbox"));
        assertTrue(message.contains("operation=,"));
        assertTrue(message.contains("payment_method=,"));
    }

    @Test
    void buildMessage_emptyMap_allFieldsEmpty() {
        String message = generator.buildMessage(new HashMap<>());

        assertTrue(message.startsWith("merchant=,env=,"));
        assertTrue(message.endsWith("cancel_url="));
    }

    @Test
    void buildMessage_fieldOrder_matchesSpecification() {
        Map<String, String> fields = new HashMap<>();
        String message = generator.buildMessage(fields);

        String[] parts = message.split(",");
        assertEquals("merchant=", parts[0]);
        assertEquals("env=", parts[1]);
        assertEquals("operation=", parts[2]);
        assertEquals("payment_method=", parts[3]);
        assertEquals("order_amount=", parts[4]);
        assertEquals("currency=", parts[5]);
        assertEquals("order_invoice_number=", parts[6]);
        assertEquals("order_description=", parts[7]);
        assertEquals("customer_id=", parts[8]);
        assertEquals("agreement_id=", parts[9]);
        assertEquals("agreement_name=", parts[10]);
        assertEquals("agreement_type=", parts[11]);
        assertEquals("agreement_payment_frequency=", parts[12]);
        assertEquals("agreement_amount_per_payment=", parts[13]);
        assertEquals("success_url=", parts[14]);
        assertEquals("error_url=", parts[15]);
        assertEquals("cancel_url=", parts[16]);
    }

    @Test
    void generateSignature_validInput_producesBase64() {
        Map<String, String> fields = new HashMap<>();
        fields.put("merchant", "MERCHANT123");
        fields.put("order_amount", "100000");

        String signature = generator.generateSignature(fields);

        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        // Base64 characters only
        assertTrue(signature.matches("^[A-Za-z0-9+/]+=*$"));
    }

    @Test
    void generateSignature_consistentOutput_sameInputSameResult() {
        Map<String, String> fields = new HashMap<>();
        fields.put("merchant", "MERCHANT123");
        fields.put("order_amount", "100000");

        String sig1 = generator.generateSignature(fields);
        String sig2 = generator.generateSignature(fields);

        assertEquals(sig1, sig2);
    }

    @Test
    void generateSignature_nullFields_treatedAsEmpty() {
        String signature = generator.generateSignature(null);

        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    void generateSignature_emptyFields_producesSignature() {
        String signature = generator.generateSignature(new HashMap<>());

        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    void generateSignature_specialCharacters_handled() {
        Map<String, String> fields = new HashMap<>();
        fields.put("order_description", "Test & payment <script>");
        fields.put("success_url", "https://example.com/success?order=123&status=ok");

        String signature = generator.generateSignature(fields);

        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    void generateSignature_differentKeys_differentSignatures() {
        SignatureGenerator gen1 = new SignatureGenerator("key1");
        SignatureGenerator gen2 = new SignatureGenerator("key2");

        Map<String, String> fields = new HashMap<>();
        fields.put("merchant", "TEST");

        String sig1 = gen1.generateSignature(fields);
        String sig2 = gen2.generateSignature(fields);

        assertNotEquals(sig1, sig2);
    }

    @Test
    void generateSignature_differentFields_differentSignatures() {
        Map<String, String> fields1 = new HashMap<>();
        fields1.put("merchant", "MERCHANT1");

        Map<String, String> fields2 = new HashMap<>();
        fields2.put("merchant", "MERCHANT2");

        String sig1 = generator.generateSignature(fields1);
        String sig2 = generator.generateSignature(fields2);

        assertNotEquals(sig1, sig2);
    }

    @Test
    void computeHmac_correctAlgorithm_sha256Length() {
        String message = "test message";
        String hmac = generator.computeHmac(message);

        // SHA256 produces 32 bytes, Base64 encoded = 44 chars (with padding)
        assertEquals(44, hmac.length());
    }

    @Test
    void getSignedFields_returnsCorrectCount() {
        assertEquals(17, SignatureGenerator.getSignedFields().size());
    }

    @Test
    void getSignedFields_containsAllFields() {
        var fields = SignatureGenerator.getSignedFields();

        assertTrue(fields.contains("merchant"));
        assertTrue(fields.contains("env"));
        assertTrue(fields.contains("cancel_url"));
    }

    @Test
    void generateSignature_knownTestVector() {
        // Test vector to validate against
        Map<String, String> fields = new HashMap<>();
        fields.put("merchant", "MERCHANT123");
        fields.put("env", "sandbox");
        fields.put("operation", "PURCHASE");
        fields.put("order_amount", "100000");
        fields.put("currency", "VND");
        fields.put("order_invoice_number", "INV001");
        fields.put("order_description", "Test payment");

        String message = generator.buildMessage(fields);
        String expectedMessage = "merchant=MERCHANT123,env=sandbox,operation=PURCHASE,payment_method=," +
                "order_amount=100000,currency=VND,order_invoice_number=INV001," +
                "order_description=Test payment,customer_id=,agreement_id=,agreement_name=," +
                "agreement_type=,agreement_payment_frequency=,agreement_amount_per_payment=," +
                "success_url=,error_url=,cancel_url=";

        assertEquals(expectedMessage, message);

        String signature = generator.generateSignature(fields);
        assertNotNull(signature);
        // Signature is deterministic - same input always produces same output
        assertEquals(signature, generator.generateSignature(fields));
    }

    @Test
    void computeHmac_invalidAlgorithm_throwsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> generator.computeHmac("test message", "InvalidAlgorithm"));

        assertEquals("Failed to compute HMAC signature", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}
