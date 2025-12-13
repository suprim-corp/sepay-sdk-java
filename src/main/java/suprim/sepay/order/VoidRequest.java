package suprim.sepay.order;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for voiding a transaction.
 */
class VoidRequest {

    @JsonProperty("order_id")
    private final String orderId;

    private final String reason;

    VoidRequest(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getOrderId() { return orderId; }
    public String getReason() { return reason; }
}
