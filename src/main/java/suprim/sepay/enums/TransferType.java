package suprim.sepay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Objects.isNull;

/**
 * Transaction direction: money IN (credit) or OUT (debit).
 */
public enum TransferType {
    /**
     * Money received (credit to account).
     */
    IN("in"),

    /**
     * Money sent (debit from account).
     */
    OUT("out");

    private final String value;

    TransferType(String value) {
        this.value = value;
    }

    /**
     * JSON serialization: "in" or "out".
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * JSON deserialization: case-insensitive.
     */
    @JsonCreator
    public static TransferType fromValue(String value) {
        if (isNull(value)) {
            throw new IllegalArgumentException("TransferType cannot be null");
        }
        for (TransferType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transfer type: " + value);
    }
}
