package suprim.sepay.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates HMAC-SHA256 signatures for SePay checkout forms.
 * Algorithm and field order match PHP SDK for cross-platform compatibility.
 */
public class SignatureGenerator {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private static final List<String> SIGNED_FIELDS = Arrays.stream(SignatureField.values())
            .map(SignatureField::getFieldName)
            .collect(Collectors.toList());

    private final byte[] secretKeyBytes;

    /**
     * Creates a signature generator with the given secret key.
     *
     * @param secretKey the secret key for HMAC computation
     * @throws IllegalArgumentException if secretKey is null or empty
     */
    public SignatureGenerator(String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        this.secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generates a signature from a map of field values.
     *
     * @param fields map of field names to values
     * @return Base64-encoded HMAC-SHA256 signature
     */
    public String generateSignature(Map<String, String> fields) {
        if (fields == null) {
            fields = Map.of();
        }
        String message = buildMessage(fields);
        return computeHmac(message);
    }

    /**
     * Builds the message string for signature computation.
     * Format: "field1=value1,field2=value2,..."
     * Missing fields default to empty string.
     *
     * @param fields map of field names to values
     * @return formatted message string
     */
    String buildMessage(Map<String, String> fields) {
        return SIGNED_FIELDS.stream()
                .map(field -> field + "=" + fields.getOrDefault(field, ""))
                .collect(Collectors.joining(","));
    }

    /**
     * Computes HMAC-SHA256 of the message and returns Base64-encoded result.
     *
     * @param message the message to sign
     * @return Base64-encoded signature
     */
    String computeHmac(String message) {
        return computeHmac(message, HMAC_ALGORITHM);
    }

    /**
     * Computes HMAC of the message using specified algorithm.
     * Package-private for testing.
     */
    String computeHmac(String message, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, algorithm);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to compute HMAC signature", e);
        }
    }

    /**
     * Returns the list of fields included in signature computation.
     *
     * @return unmodifiable list of field names
     */
    public static List<String> getSignedFields() {
        return List.copyOf(SIGNED_FIELDS);
    }
}
