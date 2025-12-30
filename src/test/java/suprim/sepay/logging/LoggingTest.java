package suprim.sepay.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingTest {

    // SePayLoggerFactory tests

    @Test
    void factory_returnsNonNullLogger() {
        SePayLogger logger = SePayLoggerFactory.getLogger(LoggingTest.class);
        assertNotNull(logger);
    }

    @Test
    void factory_returnsLoggerForDifferentClasses() {
        SePayLogger logger1 = SePayLoggerFactory.getLogger(LoggingTest.class);
        SePayLogger logger2 = SePayLoggerFactory.getLogger(String.class);
        assertNotNull(logger1);
        assertNotNull(logger2);
    }

    // SePayLogger interface static method

    @Test
    void sePayLogger_staticGetLogger() {
        SePayLogger logger = SePayLogger.getLogger(LoggingTest.class);
        assertNotNull(logger);
    }

    @Test
    void sePayLogger_methodsDoNotThrow() {
        SePayLogger logger = SePayLogger.getLogger(LoggingTest.class);

        // All methods should not throw
        assertDoesNotThrow(() -> logger.debug("test"));
        assertDoesNotThrow(() -> logger.debug("test {}", "arg"));
        assertDoesNotThrow(() -> logger.info("test"));
        assertDoesNotThrow(() -> logger.info("test {}", "arg"));
        assertDoesNotThrow(() -> logger.warn("test"));
        assertDoesNotThrow(() -> logger.warn("test {}", "arg"));
        assertDoesNotThrow(() -> logger.error("test"));
        assertDoesNotThrow(() -> logger.error("test", new RuntimeException()));
    }

    @Test
    void sePayLogger_isDebugEnabled_returnsBooleanValue() {
        SePayLogger logger = SePayLogger.getLogger(LoggingTest.class);
        // Should return a boolean value (either true or false depending on SLF4J config)
        boolean result = logger.isDebugEnabled();
        assertTrue(result || !result); // Tautology, just verifies no exception
    }
}
