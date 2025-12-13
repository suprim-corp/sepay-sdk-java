package suprim.sepay.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    // SePayException tests (base class)

    @Test
    void sePayException_message() {
        SePayException ex = new SePayException("Base error");
        assertEquals("Base error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void sePayException_messageAndCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        SePayException ex = new SePayException("Wrapper", cause);
        assertEquals("Wrapper", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    // SePayApiException tests

    @Test
    void sePayApiException_messageAndStatus() {
        SePayApiException ex = new SePayApiException("Bad request", 400);
        assertEquals("Bad request", ex.getMessage());
        assertEquals(400, ex.getStatusCode());
        assertNull(ex.getErrorCode());
    }

    @Test
    void sePayApiException_messageStatusAndErrorCode() {
        SePayApiException ex = new SePayApiException("Invalid param", 400, "INVALID_PARAM");
        assertEquals("Invalid param", ex.getMessage());
        assertEquals(400, ex.getStatusCode());
        assertEquals("INVALID_PARAM", ex.getErrorCode());
    }

    @Test
    void sePayApiException_messageStatusAndCause() {
        Exception cause = new Exception("IO error");
        SePayApiException ex = new SePayApiException("API failed", 500, cause);
        assertEquals("API failed", ex.getMessage());
        assertEquals(500, ex.getStatusCode());
        assertNull(ex.getErrorCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void sePayApiException_allParams() {
        Exception cause = new Exception("Network");
        SePayApiException ex = new SePayApiException("Error", 503, "SERVICE_UNAVAILABLE", cause);
        assertEquals("Error", ex.getMessage());
        assertEquals(503, ex.getStatusCode());
        assertEquals("SERVICE_UNAVAILABLE", ex.getErrorCode());
        assertEquals(cause, ex.getCause());
    }

    // SePayRateLimitException tests

    @Test
    void sePayRateLimitException_message() {
        SePayRateLimitException ex = new SePayRateLimitException("Rate limited");
        assertEquals("Rate limited", ex.getMessage());
        assertEquals(429, ex.getStatusCode());
        assertNull(ex.getRetryAfterSeconds());
    }

    @Test
    void sePayRateLimitException_messageAndRetryAfter() {
        SePayRateLimitException ex = new SePayRateLimitException("Too many requests", 60L);
        assertEquals("Too many requests", ex.getMessage());
        assertEquals(429, ex.getStatusCode());
        assertEquals(60L, ex.getRetryAfterSeconds());
    }

    @Test
    void sePayRateLimitException_messageAndCause() {
        Exception cause = new Exception("Throttled");
        SePayRateLimitException ex = new SePayRateLimitException("Rate limit", cause);
        assertEquals("Rate limit", ex.getMessage());
        assertEquals(429, ex.getStatusCode());
        assertNull(ex.getRetryAfterSeconds());
        assertEquals(cause, ex.getCause());
    }

    // SePayServerException tests

    @Test
    void sePayServerException_messageAndStatus() {
        SePayServerException ex = new SePayServerException("Internal error", 500);
        assertEquals("Internal error", ex.getMessage());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void sePayServerException_messageStatusAndCause() {
        Exception cause = new Exception("DB down");
        SePayServerException ex = new SePayServerException("Server error", 503, cause);
        assertEquals("Server error", ex.getMessage());
        assertEquals(503, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    // SePayWebhookException tests

    @Test
    void sePayWebhookException_message() {
        SePayWebhookException ex = new SePayWebhookException("Test error");
        assertEquals("Test error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void sePayWebhookException_messageAndCause() {
        RuntimeException cause = new RuntimeException("Original error");
        SePayWebhookException ex = new SePayWebhookException("Wrapper error", cause);
        assertEquals("Wrapper error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    // SePayAuthenticationException tests

    @Test
    void sePayAuthenticationException_message() {
        SePayAuthenticationException ex = new SePayAuthenticationException("Custom auth error");
        assertEquals("Custom auth error", ex.getMessage());
    }

    @Test
    void sePayAuthenticationException_invalidToken() {
        SePayAuthenticationException ex = SePayAuthenticationException.invalidToken();
        assertEquals("Invalid or missing API key token", ex.getMessage());
    }

    // SePayDuplicateTransactionException tests

    @Test
    void sePayDuplicateTransactionException_transactionId() {
        SePayDuplicateTransactionException ex = new SePayDuplicateTransactionException(123456L);
        assertEquals(123456L, ex.getTransactionId());
        assertEquals("Duplicate transaction detected: 123456", ex.getMessage());
    }

    // SePayValidationException tests

    @Test
    void sePayValidationException_message() {
        SePayValidationException ex = new SePayValidationException("Invalid amount");
        assertEquals("Invalid amount", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void sePayValidationException_messageAndCause() {
        NumberFormatException cause = new NumberFormatException("For input string");
        SePayValidationException ex = new SePayValidationException("Amount parse error", cause);
        assertEquals("Amount parse error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    // Inheritance tests

    @Test
    void inheritance_sePayApiException() {
        SePayApiException ex = new SePayApiException("Test", 400);
        assertTrue(ex instanceof SePayException);
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void inheritance_sePayRateLimitException() {
        SePayRateLimitException ex = new SePayRateLimitException("Test");
        assertTrue(ex instanceof SePayApiException);
        assertTrue(ex instanceof SePayException);
    }

    @Test
    void inheritance_sePayServerException() {
        SePayServerException ex = new SePayServerException("Test", 500);
        assertTrue(ex instanceof SePayApiException);
        assertTrue(ex instanceof SePayException);
    }

    @Test
    void inheritance_sePayWebhookException() {
        SePayWebhookException ex = new SePayWebhookException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void inheritance_sePayAuthenticationException() {
        SePayAuthenticationException ex = new SePayAuthenticationException("Test");
        assertTrue(ex instanceof SePayWebhookException);
    }

    @Test
    void inheritance_sePayDuplicateTransactionException() {
        SePayDuplicateTransactionException ex = new SePayDuplicateTransactionException(1L);
        assertTrue(ex instanceof SePayWebhookException);
    }

    @Test
    void inheritance_sePayValidationException() {
        SePayValidationException ex = new SePayValidationException("Test");
        assertTrue(ex instanceof SePayWebhookException);
    }
}
