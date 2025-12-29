package suprim.sepay.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationTest {

    @Test
    void purchase_hasCorrectValue() {
        assertEquals("PURCHASE", Operation.PURCHASE.getValue());
    }

    @Test
    void verify_hasCorrectValue() {
        assertEquals("VERIFY", Operation.VERIFY.getValue());
    }

    @Test
    void toJson_returnsSameAsGetValue() {
        assertEquals(Operation.PURCHASE.getValue(), Operation.PURCHASE.toJson());
        assertEquals(Operation.VERIFY.getValue(), Operation.VERIFY.toJson());
    }

    @Test
    void fromValue_parsesCorrectly() {
        assertEquals(Operation.PURCHASE, Operation.fromValue("PURCHASE"));
        assertEquals(Operation.VERIFY, Operation.fromValue("VERIFY"));
    }

    @Test
    void fromValue_caseInsensitive() {
        assertEquals(Operation.PURCHASE, Operation.fromValue("purchase"));
        assertEquals(Operation.VERIFY, Operation.fromValue("Verify"));
    }

    @Test
    void fromValue_null_returnsNull() {
        assertNull(Operation.fromValue(null));
    }

    @Test
    void fromValue_invalid_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> Operation.fromValue("INVALID"));
    }
}
