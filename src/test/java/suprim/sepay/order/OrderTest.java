package suprim.sepay.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deserialize_allFields() throws Exception {
        String json = "{" +
                "\"id\": \"ord_123456\"," +
                "\"order_invoice_number\": \"INV-001\"," +
                "\"status\": \"completed\"," +
                "\"amount\": 100000," +
                "\"currency\": \"VND\"," +
                "\"customer_id\": \"cust_001\"," +
                "\"description\": \"Test payment\"," +
                "\"payment_method\": \"CARD\"," +
                "\"created_at\": \"2024-01-15T10:30:00\"," +
                "\"updated_at\": \"2024-01-15T10:35:00\"," +
                "\"reference_code\": \"TXN123\"," +
                "\"transaction_id\": \"txn_456\"," +
                "\"transaction_status\": \"success\"" +
                "}";

        Order order = mapper.readValue(json, Order.class);

        assertEquals("ord_123456", order.getId());
        assertEquals("INV-001", order.getInvoiceNumber());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(100000, order.getAmount());
        assertEquals("VND", order.getCurrency());
        assertEquals("cust_001", order.getCustomerId());
        assertEquals("Test payment", order.getDescription());
        assertEquals("CARD", order.getPaymentMethod());
        assertEquals("TXN123", order.getReferenceCode());
        assertEquals("txn_456", order.getTransactionId());
        assertEquals("success", order.getTransactionStatus());
    }

    @Test
    void deserialize_minimalFields() throws Exception {
        String json = "{\"id\": \"ord_123\", \"status\": \"pending\", \"amount\": 50000}";

        Order order = mapper.readValue(json, Order.class);

        assertEquals("ord_123", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(50000, order.getAmount());
        assertNull(order.getInvoiceNumber());
        assertNull(order.getCustomerId());
        assertNull(order.getCreatedAt());
    }

    @Test
    void deserialize_unknownFields_ignored() throws Exception {
        String json = "{\"id\": \"ord_123\", \"status\": \"completed\", " +
                "\"unknown_field\": \"value\", \"another_unknown\": 123}";

        Order order = mapper.readValue(json, Order.class);

        assertEquals("ord_123", order.getId());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void dateFormat_parsedCorrectly() throws Exception {
        String json = "{\"id\": \"ord_123\", \"status\": \"completed\", " +
                "\"created_at\": \"2024-06-15T14:30:45\"}";

        Order order = mapper.readValue(json, Order.class);

        assertNotNull(order.getCreatedAt());
        assertEquals(2024, order.getCreatedAt().getYear());
        assertEquals(6, order.getCreatedAt().getMonthValue());
        assertEquals(15, order.getCreatedAt().getDayOfMonth());
        assertEquals(14, order.getCreatedAt().getHour());
        assertEquals(30, order.getCreatedAt().getMinute());
    }

    @Test
    void status_enumMapping() throws Exception {
        for (OrderStatus status : OrderStatus.values()) {
            String json = String.format("{\"id\": \"ord_1\", \"status\": \"%s\"}", status.getValue());

            Order order = mapper.readValue(json, Order.class);
            assertEquals(status, order.getStatus());
        }
    }

    @Test
    void snakeCase_toCamelCase() throws Exception {
        String json = "{" +
                "\"id\": \"ord_123\"," +
                "\"status\": \"completed\"," +
                "\"order_invoice_number\": \"INV-001\"," +
                "\"customer_id\": \"CUST-001\"," +
                "\"payment_method\": \"BANK_TRANSFER\"," +
                "\"reference_code\": \"REF123\"," +
                "\"transaction_id\": \"TXN456\"," +
                "\"transaction_status\": \"done\"" +
                "}";

        Order order = mapper.readValue(json, Order.class);

        assertEquals("INV-001", order.getInvoiceNumber());
        assertEquals("CUST-001", order.getCustomerId());
        assertEquals("BANK_TRANSFER", order.getPaymentMethod());
        assertEquals("REF123", order.getReferenceCode());
        assertEquals("TXN456", order.getTransactionId());
        assertEquals("done", order.getTransactionStatus());
    }

    @Test
    void setters_work() {
        Order order = new Order();
        order.setId("test_id");
        order.setStatus(OrderStatus.PENDING);
        order.setAmount(12345);
        order.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

        assertEquals("test_id", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(12345, order.getAmount());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), order.getCreatedAt());
    }

    @Test
    void getUpdatedAt_setter() {
        Order order = new Order();
        LocalDateTime updated = LocalDateTime.of(2024, 6, 15, 10, 30);
        order.setUpdatedAt(updated);
        assertEquals(updated, order.getUpdatedAt());
    }
}
