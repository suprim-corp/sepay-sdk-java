package suprim.sepay.order;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderListRequestTest {

    @Test
    void builder_defaults() {
        OrderListRequest request = OrderListRequest.builder().build();

        assertEquals(20, request.getPerPage());
        assertEquals(1, request.getPage());
        assertNull(request.getQuery());
        assertNull(request.getCustomerId());
        assertNull(request.getOrderStatus());
    }

    @Test
    void builder_allFields() {
        OrderListRequest request = OrderListRequest.builder()
                .perPage(50)
                .page(3)
                .query("test search")
                .customerId("cust_123")
                .status(OrderStatus.COMPLETED)
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 6, 30))
                .sort("created_at:desc")
                .build();

        assertEquals(50, request.getPerPage());
        assertEquals(3, request.getPage());
        assertEquals("test search", request.getQuery());
        assertEquals("cust_123", request.getCustomerId());
        assertEquals(OrderStatus.COMPLETED, request.getOrderStatus());
        assertEquals(LocalDate.of(2024, 1, 1), request.getFromCreatedAt());
        assertEquals(LocalDate.of(2024, 6, 30), request.getToCreatedAt());
        assertEquals("created_at:desc", request.getSort());
    }

    @Test
    void toQueryParams_allFields() {
        OrderListRequest request = OrderListRequest.builder()
                .perPage(10)
                .page(2)
                .query("invoice")
                .customerId("cust_001")
                .status(OrderStatus.PENDING)
                .fromDate(LocalDate.of(2024, 1, 15))
                .toDate(LocalDate.of(2024, 3, 15))
                .sort("amount:asc")
                .build();

        Map<String, String> params = request.toQueryParams();

        assertEquals("10", params.get("per_page"));
        assertEquals("2", params.get("page"));
        assertEquals("invoice", params.get("query"));
        assertEquals("cust_001", params.get("customer_id"));
        assertEquals("pending", params.get("order_status"));
        assertEquals("2024-01-15", params.get("from_created_at"));
        assertEquals("2024-03-15", params.get("to_created_at"));
        assertEquals("amount:asc", params.get("sort"));
    }

    @Test
    void toQueryParams_defaultsOnly() {
        OrderListRequest request = OrderListRequest.builder().build();

        Map<String, String> params = request.toQueryParams();

        assertEquals("20", params.get("per_page"));
        assertEquals("1", params.get("page"));
        assertFalse(params.containsKey("query"));
        assertFalse(params.containsKey("customer_id"));
        assertFalse(params.containsKey("order_status"));
    }

    @Test
    void toQueryParams_nullsOmitted() {
        OrderListRequest request = OrderListRequest.builder()
                .perPage(10)
                .status(OrderStatus.COMPLETED)
                .build();

        Map<String, String> params = request.toQueryParams();

        assertTrue(params.containsKey("per_page"));
        assertTrue(params.containsKey("page"));
        assertTrue(params.containsKey("order_status"));
        assertFalse(params.containsKey("query"));
        assertFalse(params.containsKey("customer_id"));
        assertFalse(params.containsKey("from_created_at"));
        assertFalse(params.containsKey("to_created_at"));
        assertFalse(params.containsKey("sort"));
    }

    @Test
    void dateFormat_correct() {
        OrderListRequest request = OrderListRequest.builder()
                .fromDate(LocalDate.of(2024, 12, 25))
                .toDate(LocalDate.of(2025, 1, 1))
                .build();

        Map<String, String> params = request.toQueryParams();

        assertEquals("2024-12-25", params.get("from_created_at"));
        assertEquals("2025-01-01", params.get("to_created_at"));
    }

    @Test
    void emptyStrings_omitted() {
        OrderListRequest request = OrderListRequest.builder()
                .query("")
                .customerId("")
                .sort("")
                .build();

        Map<String, String> params = request.toQueryParams();

        assertFalse(params.containsKey("query"));
        assertFalse(params.containsKey("customer_id"));
        assertFalse(params.containsKey("sort"));
    }
}
