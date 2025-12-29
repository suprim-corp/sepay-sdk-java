package suprim.sepay.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import suprim.sepay.client.SePayHttpClient;
import suprim.sepay.config.Environment;
import suprim.sepay.exception.SePayValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderResourceTest {

    private SePayHttpClient mockClient;
    private OrderResource orderResource;

    @BeforeEach
    void setUp() {
        mockClient = mock(SePayHttpClient.class);
        orderResource = new OrderResource(mockClient, Environment.SANDBOX);
    }

    @Test
    void retrieve_success() {
        Order expected = new Order();
        expected.setId("ord_123");
        expected.setStatus(OrderStatus.COMPLETED);

        when(mockClient.get(anyString(), eq(Order.class))).thenReturn(expected);

        Order result = orderResource.retrieve("ord_123");

        assertNotNull(result);
        assertEquals("ord_123", result.getId());
        assertEquals(OrderStatus.COMPLETED, result.getStatus());
    }

    @Test
    void retrieve_usesCorrectUrl() {
        Order order = new Order();
        when(mockClient.get(anyString(), eq(Order.class))).thenReturn(order);

        orderResource.retrieve("ord_456");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).get(urlCaptor.capture(), eq(Order.class));

        String url = urlCaptor.getValue();
        assertTrue(url.contains("sandbox"));
        assertTrue(url.contains("order/detail/ord_456"));
    }

    @Test
    void retrieve_nullOrderId_throws() {
        assertThrows(SePayValidationException.class,
                () -> orderResource.retrieve(null));
    }

    @Test
    void retrieve_emptyOrderId_throws() {
        assertThrows(SePayValidationException.class,
                () -> orderResource.retrieve(""));
    }

    @Test
    void list_noParams_usesDefaults() {
        OrderListResponse response = new OrderListResponse();
        when(mockClient.get(anyString(), eq(OrderListResponse.class))).thenReturn(response);

        orderResource.list();

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).get(urlCaptor.capture(), eq(OrderListResponse.class));

        String url = urlCaptor.getValue();
        assertTrue(url.contains("per_page=20"));
        assertTrue(url.contains("page=1"));
    }

    @Test
    void list_withFilters() {
        OrderListResponse response = new OrderListResponse();
        when(mockClient.get(anyString(), eq(OrderListResponse.class))).thenReturn(response);

        OrderListRequest request = OrderListRequest.builder()
                .perPage(50)
                .page(2)
                .status(OrderStatus.COMPLETED)
                .build();

        orderResource.list(request);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).get(urlCaptor.capture(), eq(OrderListResponse.class));

        String url = urlCaptor.getValue();
        assertTrue(url.contains("per_page=50"));
        assertTrue(url.contains("page=2"));
        assertTrue(url.contains("order_status=completed"));
    }

    @Test
    void list_pagination() {
        OrderListResponse response = new OrderListResponse();
        when(mockClient.get(anyString(), eq(OrderListResponse.class))).thenReturn(response);

        OrderListRequest request = OrderListRequest.builder()
                .perPage(10)
                .page(5)
                .build();

        orderResource.list(request);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).get(urlCaptor.capture(), eq(OrderListResponse.class));

        String url = urlCaptor.getValue();
        assertTrue(url.contains("per_page=10"));
        assertTrue(url.contains("page=5"));
    }

    @Test
    void voidTransaction_success() {
        Order voided = new Order();
        voided.setId("ord_123");
        voided.setStatus(OrderStatus.VOIDED);

        when(mockClient.post(anyString(), any(), eq(Order.class))).thenReturn(voided);

        Order result = orderResource.voidTransaction("ord_123");

        assertNotNull(result);
        assertEquals(OrderStatus.VOIDED, result.getStatus());
    }

    @Test
    void voidTransaction_withReason() {
        Order voided = new Order();
        voided.setStatus(OrderStatus.VOIDED);

        when(mockClient.post(anyString(), any(), eq(Order.class))).thenReturn(voided);

        orderResource.voidTransaction("ord_123", "Customer request");

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockClient).post(anyString(), bodyCaptor.capture(), eq(Order.class));

        assertTrue(bodyCaptor.getValue() instanceof VoidRequest);
        VoidRequest request = (VoidRequest) bodyCaptor.getValue();
        assertEquals("ord_123", request.getOrderId());
        assertEquals("Customer request", request.getReason());
    }

    @Test
    void voidTransaction_usesCorrectUrl() {
        Order voided = new Order();
        when(mockClient.post(anyString(), any(), eq(Order.class))).thenReturn(voided);

        orderResource.voidTransaction("ord_123");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).post(urlCaptor.capture(), any(), eq(Order.class));

        assertTrue(urlCaptor.getValue().contains("voidTransaction"));
    }

    @Test
    void voidTransaction_nullOrderId_throws() {
        assertThrows(SePayValidationException.class,
                () -> orderResource.voidTransaction(null));
    }

    @Test
    void cancel_success() {
        Order cancelled = new Order();
        cancelled.setId("ord_123");
        cancelled.setStatus(OrderStatus.CANCELLED);

        when(mockClient.post(anyString(), any(), eq(Order.class))).thenReturn(cancelled);

        Order result = orderResource.cancel("ord_123");

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancel_usesCorrectUrl() {
        Order cancelled = new Order();
        when(mockClient.post(anyString(), any(), eq(Order.class))).thenReturn(cancelled);

        orderResource.cancel("ord_123");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).post(urlCaptor.capture(), any(), eq(Order.class));

        assertTrue(urlCaptor.getValue().contains("cancel"));
    }

    @Test
    void cancel_sendsCorrectBody() {
        Order cancelled = new Order();
        when(mockClient.post(anyString(), any(), eq(Order.class))).thenReturn(cancelled);

        orderResource.cancel("ord_789");

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockClient).post(anyString(), bodyCaptor.capture(), eq(Order.class));

        assertTrue(bodyCaptor.getValue() instanceof CancelRequest);
        CancelRequest request = (CancelRequest) bodyCaptor.getValue();
        assertEquals("ord_789", request.getOrderId());
    }

    @Test
    void cancel_nullOrderId_throws() {
        assertThrows(SePayValidationException.class,
                () -> orderResource.cancel(null));
    }

    @Test
    void productionEnvironment_usesProductionUrls() {
        OrderResource prodResource = new OrderResource(mockClient, Environment.PRODUCTION);
        Order order = new Order();
        when(mockClient.get(anyString(), eq(Order.class))).thenReturn(order);

        prodResource.retrieve("ord_123");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockClient).get(urlCaptor.capture(), eq(Order.class));

        assertFalse(urlCaptor.getValue().contains("sandbox"));
    }

    @Test
    void list_emptyParams_appendQueryParams() throws Exception {
        // Test via reflection to cover empty params path
        OrderResource resource = new OrderResource(mockClient, Environment.SANDBOX);

        java.lang.reflect.Method method = OrderResource.class.getDeclaredMethod(
            "appendQueryParams", String.class, java.util.Map.class);
        method.setAccessible(true);

        // Test null params
        String result = (String) method.invoke(resource, "https://base.url", null);
        assertEquals("https://base.url", result);

        // Test empty params
        result = (String) method.invoke(resource, "https://base.url", java.util.Collections.emptyMap());
        assertEquals("https://base.url", result);
    }

    @Test
    void list_encodeParam_nullValue() throws Exception {
        // Test via reflection to cover null value encoding
        OrderResource resource = new OrderResource(mockClient, Environment.SANDBOX);

        java.lang.reflect.Method method = OrderResource.class.getDeclaredMethod(
            "encodeParam", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(resource, (String) null);
        assertEquals("", result);
    }
}
