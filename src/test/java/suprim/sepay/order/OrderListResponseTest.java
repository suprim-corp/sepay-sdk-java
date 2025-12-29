package suprim.sepay.order;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderListResponseTest {

    @Test
    void defaultConstructor() {
        OrderListResponse response = new OrderListResponse();
        assertNotNull(response);
    }

    @Test
    void getData_null_returnsEmptyList() {
        OrderListResponse response = new OrderListResponse();
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void getData_withData_returnsUnmodifiable() {
        OrderListResponse response = new OrderListResponse();
        Order order = new Order();
        order.setId("ord_1");
        response.setData(Arrays.asList(order));

        List<Order> data = response.getData();
        assertEquals(1, data.size());
        assertThrows(UnsupportedOperationException.class, () -> data.add(new Order()));
    }

    @Test
    void setAndGetTotal() {
        OrderListResponse response = new OrderListResponse();
        response.setTotal(150);
        assertEquals(150, response.getTotal());
    }

    @Test
    void setAndGetPage() {
        OrderListResponse response = new OrderListResponse();
        response.setPage(3);
        assertEquals(3, response.getPage());
    }

    @Test
    void setAndGetPerPage() {
        OrderListResponse response = new OrderListResponse();
        response.setPerPage(20);
        assertEquals(20, response.getPerPage());
    }

    @Test
    void setAndGetTotalPages() {
        OrderListResponse response = new OrderListResponse();
        response.setTotalPages(8);
        assertEquals(8, response.getTotalPages());
    }

    @Test
    void hasNextPage_true() {
        OrderListResponse response = new OrderListResponse();
        response.setPage(1);
        response.setTotalPages(5);
        assertTrue(response.hasNextPage());
    }

    @Test
    void hasNextPage_false_lastPage() {
        OrderListResponse response = new OrderListResponse();
        response.setPage(5);
        response.setTotalPages(5);
        assertFalse(response.hasNextPage());
    }

    @Test
    void hasPrevPage_true() {
        OrderListResponse response = new OrderListResponse();
        response.setPage(3);
        assertTrue(response.hasPrevPage());
    }

    @Test
    void hasPrevPage_false_firstPage() {
        OrderListResponse response = new OrderListResponse();
        response.setPage(1);
        assertFalse(response.hasPrevPage());
    }
}
