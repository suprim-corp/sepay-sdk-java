package suprim.sepay.checkout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment methods supported by SePay.
 */
public enum PaymentMethod {
    CARD("CARD"),
    BANK_TRANSFER("BANK_TRANSFER"),
    NAPAS_BANK_TRANSFER("NAPAS_BANK_TRANSFER");

    private final String value;

    PaymentMethod(String value) {
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
    public static PaymentMethod fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PaymentMethod method : values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + value);
    }
}
