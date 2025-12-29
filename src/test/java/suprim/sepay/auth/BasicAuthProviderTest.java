package suprim.sepay.auth;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthProviderTest {

    @Test
    void constructor_nullMerchantId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BasicAuthProvider(null, "secret"));
    }

    @Test
    void constructor_nullSecretKey_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BasicAuthProvider("merchant", null));
    }

    @Test
    void constructor_encodesCredentials_correctly() {
        BasicAuthProvider provider = new BasicAuthProvider("test", "secret");

        String expected = Base64.getEncoder()
                .encodeToString("test:secret".getBytes(StandardCharsets.UTF_8));

        assertEquals(expected, provider.getEncodedCredentials());
    }

    @Test
    void getAuthorizationHeader_hasBasicPrefix() {
        BasicAuthProvider provider = new BasicAuthProvider("test", "secret");

        String header = provider.getAuthorizationHeader();

        assertTrue(header.startsWith("Basic "));
    }

    @Test
    void getAuthorizationHeader_knownTestVector() {
        // Base64("test:secret") = "dGVzdDpzZWNyZXQ="
        BasicAuthProvider provider = new BasicAuthProvider("test", "secret");

        assertEquals("Basic dGVzdDpzZWNyZXQ=", provider.getAuthorizationHeader());
    }

    @Test
    void getEncodedCredentials_withoutPrefix() {
        BasicAuthProvider provider = new BasicAuthProvider("test", "secret");

        assertEquals("dGVzdDpzZWNyZXQ=", provider.getEncodedCredentials());
    }

    @Test
    void specialCharacters_colonInPassword_handled() {
        BasicAuthProvider provider = new BasicAuthProvider("merchant", "pass:word");

        String decoded = new String(
                Base64.getDecoder().decode(provider.getEncodedCredentials()),
                StandardCharsets.UTF_8);

        assertEquals("merchant:pass:word", decoded);
    }

    @Test
    void specialCharacters_unicodeInCredentials_handled() {
        BasicAuthProvider provider = new BasicAuthProvider("merchant", "mat_khau");

        String header = provider.getAuthorizationHeader();

        assertTrue(header.startsWith("Basic "));
        // Should be decodable
        assertDoesNotThrow(() ->
                Base64.getDecoder().decode(provider.getEncodedCredentials()));
    }

    @Test
    void immutable_multipleCalls_sameValue() {
        BasicAuthProvider provider = new BasicAuthProvider("test", "secret");

        String header1 = provider.getAuthorizationHeader();
        String header2 = provider.getAuthorizationHeader();
        String header3 = provider.getAuthorizationHeader();

        assertEquals(header1, header2);
        assertEquals(header2, header3);
    }

    @Test
    void emptyCredentials_handled() {
        BasicAuthProvider provider = new BasicAuthProvider("", "");

        String decoded = new String(
                Base64.getDecoder().decode(provider.getEncodedCredentials()),
                StandardCharsets.UTF_8);

        assertEquals(":", decoded);
    }

    @Test
    void longCredentials_handled() {
        String longMerchant = "a".repeat(100);
        String longSecret = "b".repeat(100);

        BasicAuthProvider provider = new BasicAuthProvider(longMerchant, longSecret);

        String decoded = new String(
                Base64.getDecoder().decode(provider.getEncodedCredentials()),
                StandardCharsets.UTF_8);

        assertEquals(longMerchant + ":" + longSecret, decoded);
    }
}
