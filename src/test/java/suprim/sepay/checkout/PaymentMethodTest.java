package suprim.sepay.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void card_hasCorrectValue() {
        assertEquals("CARD", PaymentMethod.CARD.getValue());
    }

    @Test
    void bankTransfer_hasCorrectValue() {
        assertEquals("BANK_TRANSFER", PaymentMethod.BANK_TRANSFER.getValue());
    }

    @Test
    void napasBankTransfer_hasCorrectValue() {
        assertEquals("NAPAS_BANK_TRANSFER", PaymentMethod.NAPAS_BANK_TRANSFER.getValue());
    }

    @Test
    void toJson_returnsSameAsGetValue() {
        for (PaymentMethod method : PaymentMethod.values()) {
            assertEquals(method.getValue(), method.toJson());
        }
    }

    @Test
    void fromValue_parsesCorrectly() {
        assertEquals(PaymentMethod.CARD, PaymentMethod.fromValue("CARD"));
        assertEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.fromValue("BANK_TRANSFER"));
    }

    @Test
    void fromValue_null_returnsNull() {
        assertNull(PaymentMethod.fromValue(null));
    }

    @Test
    void fromValue_invalid_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> PaymentMethod.fromValue("INVALID"));
    }

    @Test
    void allValuesExist() {
        assertEquals(3, PaymentMethod.values().length);
    }
}
