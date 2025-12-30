package suprim.sepay.util;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static java.util.Objects.isNull;

/**
 * Extracts and validates API key from custom "Apikey" authorization header.
 *
 * <p>SePay uses format: <code>Authorization: Apikey {token}</code>
 * NOT standard Bearer token format.
 *
 * <p>Usage:
 * <pre>
 * TokenExtractor extractor = new TokenExtractor();
 * String token = extractor.extractToken(request);
 * // or
 * String token = extractor.extractTokenFromHeader(authHeader);
 *
 * // Validate with constant-time comparison (prevents timing attacks)
 * if (TokenExtractor.isValidToken(token, expectedToken)) {
 *     // process webhook
 * }
 * </pre>
 */
public class TokenExtractor {

    private static final String APIKEY_PREFIX = "Apikey ";
    private static final int PREFIX_LENGTH = 7; // "Apikey ".length()

    /**
     * Validates token using constant-time comparison to prevent timing attacks.
     *
     * @param provided the token from request
     * @param expected the expected token
     * @return true if tokens match, false otherwise
     */
    public static boolean isValidToken(String provided, String expected) {
        if (isNull(provided) || isNull(expected)) {
            return false;
        }
        return MessageDigest.isEqual(
            provided.getBytes(StandardCharsets.UTF_8),
            expected.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Extract token from Authorization header.
     *
     * @param request HTTP request
     * @return token if found, null otherwise
     */
    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return extractTokenFromHeader(authHeader);
    }

    /**
     * Extract token from header value.
     *
     * @param authHeader Authorization header value
     * @return token if found, null otherwise
     */
    public String extractTokenFromHeader(String authHeader) {
        if (isNull(authHeader) || authHeader.isEmpty()) {
            return null;
        }

        // Find first "Apikey " prefix (case-insensitive)
        int position = authHeader.toLowerCase().indexOf(APIKEY_PREFIX.toLowerCase());
        if (position == -1) {
            return null;
        }

        // Extract token after "Apikey "
        String token = authHeader.substring(position + PREFIX_LENGTH);

        // Handle comma-separated tokens (take first)
        if (token.contains(",")) {
            token = token.substring(0, token.indexOf(',')).trim();
        }

        return token.isEmpty() ? null : token.trim();
    }
}
