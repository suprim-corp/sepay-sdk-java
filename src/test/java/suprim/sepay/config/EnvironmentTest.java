package suprim.sepay.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Environment enum.
 */
class EnvironmentTest {

    @Test
    void testEnumValues() {
        Environment[] values = Environment.values();
        assertEquals(2, values.length);
    }

    @Test
    void testSandboxExists() {
        assertEquals("SANDBOX", Environment.SANDBOX.name());
    }

    @Test
    void testProductionExists() {
        assertEquals("PRODUCTION", Environment.PRODUCTION.name());
    }

    @Test
    void testValueOf() {
        assertEquals(Environment.SANDBOX, Environment.valueOf("SANDBOX"));
        assertEquals(Environment.PRODUCTION, Environment.valueOf("PRODUCTION"));
    }
}
