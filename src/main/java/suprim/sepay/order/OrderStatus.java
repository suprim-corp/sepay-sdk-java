package suprim.sepay.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Objects.isNull;

/**
 * Order status values from SePay API.
 */
public enum OrderStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed"),
    VOIDED("voided"),
    CANCELLED("cancelled");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonValue
    public String toJson() {
        return value;
    }

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        if (isNull(value)) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + value);
    }
}
