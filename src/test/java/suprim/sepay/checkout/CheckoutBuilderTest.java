package suprim.sepay.checkout;

import org.junit.jupiter.api.Test;
import suprim.sepay.config.Environment;
import suprim.sepay.exception.SePayValidationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutBuilderTest {

    private static final String MERCHANT = "TEST_MERCHANT";
    private static final String SECRET = "test_secret_key";

    @Test
    void create_nullMerchant_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(null, SECRET));
    }

    @Test
    void create_emptyMerchant_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create("", SECRET));
    }

    @Test
    void create_nullSecret_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, null));
    }

    @Test
    void create_emptySecret_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, ""));
    }

    @Test
    void create_setsDefaultEnvironment() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .build();

        assertEquals("sandbox", request.getEnv());
    }

    @Test
    void build_missingOperation_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .amount(10000)
                        .description("Test")
                        .build());
    }

    @Test
    void build_missingDescription_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("INV001")
                        .build());
    }

    @Test
    void purchase_zeroAmount_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(0)
                        .invoiceNumber("INV001")
                        .description("Test")
                        .build());
    }

    @Test
    void purchase_negativeAmount_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(-100)
                        .invoiceNumber("INV001")
                        .description("Test")
                        .build());
    }

    @Test
    void purchase_missingInvoice_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .description("Test")
                        .build());
    }

    @Test
    void purchase_emptyInvoice_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("")
                        .description("Test")
                        .build());
    }

    @Test
    void purchase_invoiceTooLong_throws() {
        String longInvoice = "a".repeat(101);
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber(longInvoice)
                        .description("Test")
                        .build());
    }

    @Test
    void purchase_invoiceSpecialChars_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("INV@001")
                        .description("Test")
                        .build());
    }

    @Test
    void purchase_invoiceWithHyphen_allowed() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV-2024-001")
                .description("Test")
                .build();

        assertEquals("INV-2024-001", request.getOrderInvoiceNumber());
    }

    @Test
    void purchase_invoiceWithUnderscore_allowed() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV_001")
                .description("Test")
                .build();

        assertEquals("INV_001", request.getOrderInvoiceNumber());
    }

    @Test
    void verify_nonZeroAmount_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.VERIFY)
                        .amount(100)
                        .description("Test")
                        .build());
    }

    @Test
    void verify_valid_succeeds() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.VERIFY)
                .amount(0)
                .description("Card verification")
                .build();

        assertEquals(Operation.VERIFY, request.getOperation());
        assertEquals(0, request.getOrderAmount());
    }

    @Test
    void invalidSuccessUrl_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("INV001")
                        .description("Test")
                        .successUrl("not-a-url")
                        .build());
    }

    @Test
    void invalidErrorUrl_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("INV001")
                        .description("Test")
                        .errorUrl("ftp://invalid")
                        .build());
    }

    @Test
    void invalidCancelUrl_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("INV001")
                        .description("Test")
                        .cancelUrl("invalid")
                        .build());
    }

    @Test
    void validUrls_accepted() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .successUrl("https://example.com/success")
                .errorUrl("https://example.com/error")
                .cancelUrl("http://example.com/cancel")
                .build();

        assertEquals("https://example.com/success", request.getSuccessUrl());
        assertEquals("https://example.com/error", request.getErrorUrl());
        assertEquals("http://example.com/cancel", request.getCancelUrl());
    }

    @Test
    void fluentChaining_returnsBuilder() {
        CheckoutBuilder builder = CheckoutBuilder.create(MERCHANT, SECRET);

        assertSame(builder, builder.environment(Environment.PRODUCTION));
        assertSame(builder, builder.operation(Operation.PURCHASE));
        assertSame(builder, builder.amount(10000));
        assertSame(builder, builder.invoiceNumber("INV001"));
        assertSame(builder, builder.description("Test"));
        assertSame(builder, builder.paymentMethod(PaymentMethod.CARD));
        assertSame(builder, builder.customerId("CUST001"));
        assertSame(builder, builder.successUrl("https://example.com"));
    }

    @Test
    void build_computesSignature() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .build();

        assertNotNull(request.getSignature());
        assertFalse(request.getSignature().isEmpty());
    }

    @Test
    void build_signatureConsistent() {
        CheckoutBuilder builder = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test");

        String sig1 = builder.build().getSignature();
        String sig2 = builder.build().getSignature();

        assertEquals(sig1, sig2);
    }

    @Test
    void toSignatureMap_allFieldsPresent() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .environment(Environment.PRODUCTION)
                .operation(Operation.PURCHASE)
                .paymentMethod(PaymentMethod.CARD)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test payment")
                .customerId("CUST001")
                .successUrl("https://example.com/success")
                .build();

        Map<String, String> map = request.toSignatureMap();

        assertEquals(MERCHANT, map.get("merchant"));
        assertEquals("production", map.get("env"));
        assertEquals("PURCHASE", map.get("operation"));
        assertEquals("CARD", map.get("payment_method"));
        assertEquals("10000", map.get("order_amount"));
        assertEquals("VND", map.get("currency"));
        assertEquals("INV001", map.get("order_invoice_number"));
        assertEquals("Test payment", map.get("order_description"));
        assertEquals("CUST001", map.get("customer_id"));
    }

    @Test
    void purchase_convenienceMethod() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .purchase(50000, "INV-123", "Order payment");

        assertEquals(Operation.PURCHASE, request.getOperation());
        assertEquals(50000, request.getOrderAmount());
        assertEquals("INV-123", request.getOrderInvoiceNumber());
        assertEquals("Order payment", request.getOrderDescription());
    }

    @Test
    void verify_convenienceMethod() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .verify("Card verification");

        assertEquals(Operation.VERIFY, request.getOperation());
        assertEquals(0, request.getOrderAmount());
        assertEquals("Card verification", request.getOrderDescription());
    }

    @Test
    void currencyAlwaysVND() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .build();

        assertEquals("VND", request.getCurrency());
    }

    @Test
    void agreementFields_setAndGet() {
        CheckoutBuilder builder = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .agreementId("AGR-001")
                .agreementName("Monthly Plan")
                .agreementType("RECURRING")
                .agreementPaymentFrequency("MONTHLY")
                .agreementAmountPerPayment("50000");

        CheckoutRequest request = builder.build();

        assertEquals("AGR-001", request.getAgreementId());
        assertEquals("Monthly Plan", request.getAgreementName());
        assertEquals("RECURRING", request.getAgreementType());
        assertEquals("MONTHLY", request.getAgreementPaymentFrequency());
        assertEquals("50000", request.getAgreementAmountPerPayment());
    }

    @Test
    void agreementFields_fluentChaining() {
        CheckoutBuilder builder = CheckoutBuilder.create(MERCHANT, SECRET);

        assertSame(builder, builder.agreementId("AGR-001"));
        assertSame(builder, builder.agreementName("Name"));
        assertSame(builder, builder.agreementType("Type"));
        assertSame(builder, builder.agreementPaymentFrequency("Freq"));
        assertSame(builder, builder.agreementAmountPerPayment("1000"));
    }

    @Test
    void getMerchant_returnsValue() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .build();

        assertEquals(MERCHANT, request.getMerchant());
    }

    @Test
    void getPaymentMethod_returnsValue() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        assertEquals(PaymentMethod.BANK_TRANSFER, request.getPaymentMethod());
    }

    @Test
    void getCustomerId_returnsValue() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .customerId("CUST-123")
                .build();

        assertEquals("CUST-123", request.getCustomerId());
    }

    @Test
    void environment_null_defaultsToSandbox() {
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .environment(null)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .build();

        assertEquals("sandbox", request.getEnv());
    }

    @Test
    void build_emptyDescription_throws() {
        assertThrows(SePayValidationException.class,
                () -> CheckoutBuilder.create(MERCHANT, SECRET)
                        .operation(Operation.PURCHASE)
                        .amount(10000)
                        .invoiceNumber("INV001")
                        .description("")
                        .build());
    }

    @Test
    void toSignatureMap_nullOperation() {
        // Test branch coverage for null operation
        CheckoutRequest request = CheckoutBuilder.create(MERCHANT, SECRET)
                .operation(Operation.PURCHASE)
                .amount(10000)
                .invoiceNumber("INV001")
                .description("Test")
                .build();

        java.util.Map<String, String> map = request.toSignatureMap();
        assertEquals("PURCHASE", map.get("operation"));
    }
}
