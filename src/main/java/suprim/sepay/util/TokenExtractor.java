package suprim.sepay.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Extracts API key from custom "Apikey" authorization header.
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
 * </pre>
 */
public class TokenExtractor {

    private static final String APIKEY_PREFIX = "Apikey ";
    private static final int PREFIX_LENGTH = 7; // "Apikey ".length()

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
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }

        // Find "Apikey " prefix (case-insensitive)
        int position = authHeader.toLowerCase().lastIndexOf(APIKEY_PREFIX.toLowerCase());
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
