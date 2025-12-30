package suprim.sepay.checkout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Objects.isNull;

/**
 * Payment operation types supported by SePay.
 */
public enum Operation {
    PURCHASE("PURCHASE"),
    VERIFY("VERIFY");

    private final String value;

    Operation(String value) {
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
    public static Operation fromValue(String value) {
        if (isNull(value)) {
            return null;
        }
        for (Operation op : values()) {
            if (op.value.equalsIgnoreCase(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operation: " + value);
    }
}
