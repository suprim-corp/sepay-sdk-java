package suprim.sepay.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.Objects.isNull;

/**
 * Provides HTTP Basic Authentication header for SePay API requests.
 * Format: "Basic base64(merchantId:secretKey)"
 */
public class BasicAuthProvider {

    private static final String BASIC_PREFIX = "Basic ";

    private final String authHeader;

    /**
     * Creates a basic auth provider with the given credentials.
     *
     * @param merchantId the merchant identifier
     * @param secretKey the secret key
     * @throws IllegalArgumentException if merchantId or secretKey is null
     */
    public BasicAuthProvider(String merchantId, String secretKey) {
        if (isNull(merchantId)) {
            throw new IllegalArgumentException("Merchant ID cannot be null");
        }
        if (isNull(secretKey)) {
            throw new IllegalArgumentException("Secret key cannot be null");
        }

        String credentials = merchantId + ":" + secretKey;
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        this.authHeader = BASIC_PREFIX + encoded;
    }

    /**
     * Returns the complete Authorization header value.
     *
     * @return the authorization header (e.g., "Basic dGVzdDpzZWNyZXQ=")
     */
    public String getAuthorizationHeader() {
        return authHeader;
    }

    /**
     * Returns only the encoded credentials portion (without "Basic " prefix).
     *
     * @return Base64-encoded credentials
     */
    public String getEncodedCredentials() {
        return authHeader.substring(BASIC_PREFIX.length());
    }
}
