package suprim.sepay.order;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for cancelling an order.
 */
class CancelRequest {

    @JsonProperty("order_id")
    private final String orderId;

    CancelRequest(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() { return orderId; }
}
