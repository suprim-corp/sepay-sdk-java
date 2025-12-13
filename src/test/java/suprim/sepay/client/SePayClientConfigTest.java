package suprim.sepay.client;

import suprim.sepay.config.Environment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SePayClientConfig.
 */
class SePayClientConfigTest {

    @Test
    void testBuilderWithRequiredParams() {
        SePayClientConfig config = SePayClientConfig.builder("SP-TEST-123", "secret123").build();

        assertEquals("SP-TEST-123", config.getMerchantId());
        assertEquals("secret123", config.getSecretKey());
    }

    @Test
    void testDefaultValues() {
        SePayClientConfig config = SePayClientConfig.builder("merchant", "secret").build();

        assertEquals(Environment.SANDBOX, config.getEnvironment());
        assertEquals(10000, config.getConnectTimeoutMs());
        assertEquals(30000, config.getReadTimeoutMs());
        assertEquals(3, config.getMaxRetries());
        assertEquals(1000, config.getRetryDelayMs());
    }

    @Test
    void testCustomValues() {
        SePayClientConfig config = SePayClientConfig.builder("merchant", "secret")
            .environment(Environment.PRODUCTION)
            .connectTimeout(5000)
            .readTimeout(15000)
            .maxRetries(5)
            .retryDelay(2000)
            .build();

        assertEquals(Environment.PRODUCTION, config.getEnvironment());
        assertEquals(5000, config.getConnectTimeoutMs());
        assertEquals(15000, config.getReadTimeoutMs());
        assertEquals(5, config.getMaxRetries());
        assertEquals(2000, config.getRetryDelayMs());
    }

    @Test
    void testNullMerchantIdThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder(null, "secret")
        );
    }

    @Test
    void testEmptyMerchantIdThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("", "secret")
        );
    }

    @Test
    void testBlankMerchantIdThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("   ", "secret")
        );
    }

    @Test
    void testNullSecretKeyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", null)
        );
    }

    @Test
    void testEmptySecretKeyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "")
        );
    }

    @Test
    void testNullEnvironmentThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "secret").environment(null)
        );
    }

    @Test
    void testInvalidConnectTimeoutThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "secret").connectTimeout(0)
        );
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "secret").connectTimeout(-1)
        );
    }

    @Test
    void testInvalidReadTimeoutThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "secret").readTimeout(0)
        );
    }

    @Test
    void testNegativeMaxRetriesThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "secret").maxRetries(-1)
        );
    }

    @Test
    void testZeroMaxRetriesAllowed() {
        SePayClientConfig config = SePayClientConfig.builder("merchant", "secret")
            .maxRetries(0)
            .build();
        assertEquals(0, config.getMaxRetries());
    }

    @Test
    void testNegativeRetryDelayThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            SePayClientConfig.builder("merchant", "secret").retryDelay(-1)
        );
    }

    @Test
    void testZeroRetryDelayAllowed() {
        SePayClientConfig config = SePayClientConfig.builder("merchant", "secret")
            .retryDelay(0)
            .build();
        assertEquals(0, config.getRetryDelayMs());
    }
}
