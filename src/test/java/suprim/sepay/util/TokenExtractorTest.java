package suprim.sepay.util;

import org.junit.jupiter.api.Test;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TokenExtractor.
 */
class TokenExtractorTest {

    private final TokenExtractor extractor = new TokenExtractor();

    @Test
    void testExtractToken_validApikey() {
        String token = extractor.extractTokenFromHeader("Apikey test_token_123");
        assertEquals("test_token_123", token);
    }

    @Test
    void testExtractToken_caseInsensitive() {
        String token = extractor.extractTokenFromHeader("apikey MyToken");
        assertEquals("MyToken", token);

        token = extractor.extractTokenFromHeader("APIKEY AnotherToken");
        assertEquals("AnotherToken", token);

        token = extractor.extractTokenFromHeader("ApIkEy MixedCase");
        assertEquals("MixedCase", token);
    }

    @Test
    void testExtractToken_withComma() {
        // Should take first token before comma
        String token = extractor.extractTokenFromHeader("Apikey token1,token2");
        assertEquals("token1", token);

        token = extractor.extractTokenFromHeader("Apikey first,second,third");
        assertEquals("first", token);
    }

    @Test
    void testExtractToken_withExtraSpaces() {
        String token = extractor.extractTokenFromHeader("Apikey   token_with_spaces   ");
        assertEquals("token_with_spaces", token);

        token = extractor.extractTokenFromHeader("Apikey token");
        assertEquals("token", token);
    }

    @Test
    void testExtractToken_bearerFormat() {
        // Should return null for Bearer tokens (not Apikey)
        String token = extractor.extractTokenFromHeader("Bearer some_token");
        assertNull(token, "Should not extract Bearer tokens");
    }

    @Test
    void testExtractToken_nullHeader() {
        String token = extractor.extractTokenFromHeader(null);
        assertNull(token);
    }

    @Test
    void testExtractToken_emptyHeader() {
        String token = extractor.extractTokenFromHeader("");
        assertNull(token);

        token = extractor.extractTokenFromHeader("   ");
        assertNull(token);
    }

    @Test
    void testExtractToken_onlyPrefix() {
        String token = extractor.extractTokenFromHeader("Apikey ");
        assertNull(token);

        token = extractor.extractTokenFromHeader("Apikey");
        assertNull(token);
    }

    @Test
    void testExtractToken_multipleApikeyOccurrences() {
        // Should use last occurrence
        String token = extractor.extractTokenFromHeader("Apikey old Apikey new");
        assertEquals("new", token);
    }

    @Test
    void testExtractToken_withSpecialCharacters() {
        String token = extractor.extractTokenFromHeader("Apikey abc-def_123.xyz");
        assertEquals("abc-def_123.xyz", token);
    }

    @Test
    void testExtractToken_longToken() {
        String longToken = "a".repeat(100);
        String token = extractor.extractTokenFromHeader("Apikey " + longToken);
        assertEquals(longToken, token);
    }

    @Test
    void testExtractToken_withOtherAuthScheme() {
        // Should not extract from other auth schemes
        String token = extractor.extractTokenFromHeader("Basic dXNlcjpwYXNz");
        assertNull(token);

        token = extractor.extractTokenFromHeader("Digest username=\"test\"");
        assertNull(token);
    }

    @Test
    void testExtractToken_fromRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Apikey my_token");

        String token = extractor.extractToken(request);

        assertEquals("my_token", token);
    }

    @Test
    void testExtractToken_fromRequest_noHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = extractor.extractToken(request);

        assertNull(token);
    }
}
