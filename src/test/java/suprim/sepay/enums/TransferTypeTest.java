package suprim.sepay.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransferType enum.
 */
class TransferTypeTest {

    @Test
    void testFromValue_validLowercase() {
        TransferType result = TransferType.fromValue("in");
        assertEquals(TransferType.IN, result);

        result = TransferType.fromValue("out");
        assertEquals(TransferType.OUT, result);
    }

    @Test
    void testFromValue_validUppercase() {
        TransferType result = TransferType.fromValue("IN");
        assertEquals(TransferType.IN, result);

        result = TransferType.fromValue("OUT");
        assertEquals(TransferType.OUT, result);
    }

    @Test
    void testFromValue_validMixedCase() {
        TransferType result = TransferType.fromValue("In");
        assertEquals(TransferType.IN, result);

        result = TransferType.fromValue("OuT");
        assertEquals(TransferType.OUT, result);
    }

    @Test
    void testFromValue_invalidValue() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TransferType.fromValue("invalid")
        );
        assertTrue(ex.getMessage().contains("Unknown transfer type"));
    }

    @Test
    void testFromValue_nullValue() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> TransferType.fromValue(null)
        );
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    void testGetValue() {
        assertEquals("in", TransferType.IN.getValue());
        assertEquals("out", TransferType.OUT.getValue());
    }

    @Test
    void testEnumValues() {
        TransferType[] values = TransferType.values();
        assertEquals(2, values.length);
        assertEquals(TransferType.IN, values[0]);
        assertEquals(TransferType.OUT, values[1]);
    }

    @Test
    void testEnumToString() {
        assertEquals("IN", TransferType.IN.toString());
        assertEquals("OUT", TransferType.OUT.toString());
    }
}
