package suprim.sepay.order;

import suprim.sepay.client.SePayHttpClient;
import suprim.sepay.config.Environment;
import suprim.sepay.config.UrlConfig;
import suprim.sepay.exception.SePayValidationException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resource for managing orders via SePay API.
 */
public class OrderResource {

    private final SePayHttpClient httpClient;
    private final Environment environment;

    public OrderResource(SePayHttpClient httpClient, Environment environment) {
        this.httpClient = httpClient;
        this.environment = environment;
    }

    /**
     * Retrieves a single order by ID.
     *
     * @param orderId the order ID
     * @return the order details
     */
    public Order retrieve(String orderId) {
        validateOrderId(orderId);
        String url = UrlConfig.getOrderDetailUrl(environment, orderId);
        return httpClient.get(url, Order.class);
    }

    /**
     * Lists orders with default parameters.
     *
     * @return paginated order list
     */
    public OrderListResponse list() {
        return list(OrderListRequest.builder().build());
    }

    /**
     * Lists orders with custom filters.
     *
     * @param request the list request parameters
     * @return paginated order list
     */
    public OrderListResponse list(OrderListRequest request) {
        String baseUrl = UrlConfig.getOrderListUrl(environment);
        String url = appendQueryParams(baseUrl, request.toQueryParams());
        return httpClient.get(url, OrderListResponse.class);
    }

    /**
     * Voids a completed transaction.
     *
     * @param orderId the order ID to void
     * @return the updated order
     */
    public Order voidTransaction(String orderId) {
        return voidTransaction(orderId, null);
    }

    /**
     * Voids a completed transaction with a reason.
     *
     * @param orderId the order ID to void
     * @param reason  optional reason for voiding
     * @return the updated order
     */
    public Order voidTransaction(String orderId, String reason) {
        validateOrderId(orderId);
        String url = UrlConfig.getVoidUrl(environment);
        VoidRequest request = new VoidRequest(orderId, reason);
        return httpClient.post(url, request, Order.class);
    }

    /**
     * Cancels a pending order.
     *
     * @param orderId the order ID to cancel
     * @return the updated order
     */
    public Order cancel(String orderId) {
        validateOrderId(orderId);
        String url = UrlConfig.getCancelUrl(environment);
        CancelRequest request = new CancelRequest(orderId);
        return httpClient.post(url, request, Order.class);
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            throw new SePayValidationException("Order ID is required");
        }
    }

    private String appendQueryParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        String queryString = params.entrySet().stream()
                .map(e -> encodeParam(e.getKey()) + "=" + encodeParam(e.getValue()))
                .collect(Collectors.joining("&"));
        return baseUrl + "?" + queryString;
    }

    private String encodeParam(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
