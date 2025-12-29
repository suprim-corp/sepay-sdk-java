package suprim.sepay.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void allValues_exist() {
        assertEquals(5, OrderStatus.values().length);
    }

    @Test
    void pending_hasCorrectValue() {
        assertEquals("pending", OrderStatus.PENDING.getValue());
    }

    @Test
    void completed_hasCorrectValue() {
        assertEquals("completed", OrderStatus.COMPLETED.getValue());
    }

    @Test
    void failed_hasCorrectValue() {
        assertEquals("failed", OrderStatus.FAILED.getValue());
    }

    @Test
    void voided_hasCorrectValue() {
        assertEquals("voided", OrderStatus.VOIDED.getValue());
    }

    @Test
    void cancelled_hasCorrectValue() {
        assertEquals("cancelled", OrderStatus.CANCELLED.getValue());
    }

    @Test
    void fromValue_valid() {
        assertEquals(OrderStatus.PENDING, OrderStatus.fromValue("pending"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.fromValue("completed"));
        assertEquals(OrderStatus.FAILED, OrderStatus.fromValue("failed"));
        assertEquals(OrderStatus.VOIDED, OrderStatus.fromValue("voided"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromValue("cancelled"));
    }

    @Test
    void fromValue_caseInsensitive() {
        assertEquals(OrderStatus.PENDING, OrderStatus.fromValue("PENDING"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.fromValue("Completed"));
    }

    @Test
    void fromValue_null_returnsNull() {
        assertNull(OrderStatus.fromValue(null));
    }

    @Test
    void fromValue_invalid_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderStatus.fromValue("unknown"));
    }

    @Test
    void toJson_returnsSameAsGetValue() {
        for (OrderStatus status : OrderStatus.values()) {
            assertEquals(status.getValue(), status.toJson());
        }
    }
}
