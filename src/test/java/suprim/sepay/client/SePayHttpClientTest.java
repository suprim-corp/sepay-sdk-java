package suprim.sepay.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import suprim.sepay.config.Environment;
import suprim.sepay.exception.SePayApiException;
import suprim.sepay.exception.SePayException;
import suprim.sepay.exception.SePayRateLimitException;
import suprim.sepay.exception.SePayServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SePayHttpClient.
 */
class SePayHttpClientTest {

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;
    private SePayClientConfig config;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        objectMapper = new ObjectMapper();
        config = SePayClientConfig.builder("SP-TEST-123", "secret-key-456")
            .environment(Environment.SANDBOX)
            .maxRetries(2)
            .retryDelay(10) // Short delay for tests
            .build();
    }

    private SePayHttpClient createClient() {
        return new SePayHttpClient(config, objectMapper, mockHttpClient);
    }

    // === Auth Header Tests ===

    @Test
    void testBasicAuthHeaderCorrectlyEncoded() {
        SePayHttpClient client = createClient();
        String expectedCredentials = "SP-TEST-123:secret-key-456";
        String expectedEncoded = Base64.getEncoder().encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));
        String expectedHeader = "Basic " + expectedEncoded;

        assertEquals(expectedHeader, client.getAuthHeader());
    }

    @Test
    void testGetRequestSendsCorrectHeaders() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"data\":\"test\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        client.getRaw("https://api.example.com/test");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest request = requestCaptor.getValue();
        assertTrue(request.headers().firstValue("Authorization").isPresent());
        assertTrue(request.headers().firstValue("Authorization").get().startsWith("Basic "));
        assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(""));
        assertEquals("application/json", request.headers().firstValue("Accept").orElse(""));
        assertTrue(request.headers().firstValue("User-Agent").orElse("").contains("SePay-Java-SDK"));
    }

    @Test
    void testPostRequestSendsJsonBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"success\":true}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        ApiResponse result = client.post("https://api.example.com/test", new TestRequest("value"), ApiResponse.class);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());

        HttpRequest request = requestCaptor.getValue();
        assertEquals("POST", request.method());
    }

    // === Success Response Tests ===

    @Test
    void testSuccessResponseParsedCorrectly() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"success\":true,\"message\":\"OK\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        ApiResponse result = client.get("https://api.example.com/test", ApiResponse.class);

        assertTrue(result.isSuccess());
        assertEquals("OK", result.getMessage());
    }

    @Test
    void testEmptyResponseBodyHandled() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        String result = client.getRaw("https://api.example.com/test");

        assertEquals("", result);
    }

    // === Error Response Tests ===

    @Test
    void test400ThrowsSePayApiException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("{\"error\":\"VALIDATION_ERROR\",\"message\":\"Invalid field\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(400, ex.getStatusCode());
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
        assertEquals("Invalid field", ex.getMessage());
    }

    @Test
    void test401ThrowsSePayApiException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("{\"message\":\"Unauthorized\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(401, ex.getStatusCode());
    }

    @Test
    void test404ThrowsSePayApiException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn("{\"message\":\"Not found\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(404, ex.getStatusCode());
        assertEquals("NOT_FOUND", ex.getErrorCode());
    }

    @Test
    void test429ThrowsSePayRateLimitException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(429);
        when(mockResponse.body()).thenReturn("{\"message\":\"Rate limit exceeded\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayRateLimitException ex = assertThrows(SePayRateLimitException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(429, ex.getStatusCode());
    }

    @Test
    void test500ThrowsSePayServerException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("{\"message\":\"Internal server error\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayServerException ex = assertThrows(SePayServerException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void test503ThrowsSePayServerException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(503);
        when(mockResponse.body()).thenReturn("");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayServerException ex = assertThrows(SePayServerException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(503, ex.getStatusCode());
    }

    // === Retry Tests ===

    @Test
    void testRetryOn429() throws Exception {
        when(mockResponse.statusCode()).thenReturn(429, 429, 200);
        when(mockResponse.body()).thenReturn("{}", "{}", "{\"success\":true}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        ApiResponse result = client.get("https://api.example.com/test", ApiResponse.class);

        assertTrue(result.isSuccess());
        verify(mockHttpClient, times(3)).send(any(), any());
    }

    @Test
    void testRetryOn500() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500, 200);
        when(mockResponse.body()).thenReturn("{}", "{\"success\":true}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        ApiResponse result = client.get("https://api.example.com/test", ApiResponse.class);

        assertTrue(result.isSuccess());
        verify(mockHttpClient, times(2)).send(any(), any());
    }

    @Test
    void testNoRetryOn400() throws Exception {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("{\"message\":\"Bad request\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        verify(mockHttpClient, times(1)).send(any(), any());
    }

    @Test
    void testNoRetryOn401() throws Exception {
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("{\"message\":\"Unauthorized\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        verify(mockHttpClient, times(1)).send(any(), any());
    }

    @Test
    void testMaxRetriesExceededThrows() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("{\"message\":\"Server error\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        assertThrows(SePayServerException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        // maxRetries=2, so 3 total attempts (0, 1, 2)
        verify(mockHttpClient, times(3)).send(any(), any());
    }

    @Test
    void testConnectionErrorTriggersRetry() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Connection refused"))
            .thenThrow(new IOException("Connection refused"))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"success\":true}");

        SePayHttpClient client = createClient();
        ApiResponse result = client.get("https://api.example.com/test", ApiResponse.class);

        assertTrue(result.isSuccess());
        verify(mockHttpClient, times(3)).send(any(), any());
    }

    @Test
    void testConnectionErrorExhaustsRetries() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Connection refused"));

        SePayHttpClient client = createClient();
        SePayException ex = assertThrows(SePayException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertTrue(ex.getMessage().contains("Connection refused"));
        verify(mockHttpClient, times(3)).send(any(), any());
    }

    // === Malformed Response Tests ===

    @Test
    void testMalformedJsonResponseHandled() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("not valid json");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayException ex = assertThrows(SePayException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertTrue(ex.getMessage().contains("Failed to parse response"));
    }

    @Test
    void testMalformedErrorResponseUsesRawBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("plain text error");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals("plain text error", ex.getMessage());
    }

    @Test
    void testNullResponseBodyReturnsEmpty() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        String result = client.getRaw("https://api.example.com/test");

        assertEquals("", result);
    }

    @Test
    void test400WithNullBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(400, ex.getStatusCode());
        assertEquals("Validation error", ex.getMessage());
    }

    @Test
    void test401WithNullBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(401, ex.getStatusCode());
        assertEquals("Authentication failed", ex.getMessage());
    }

    @Test
    void test404WithNullBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(404, ex.getStatusCode());
        assertEquals("Resource not found", ex.getMessage());
    }

    @Test
    void test429WithNullBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(429);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayRateLimitException ex = assertThrows(SePayRateLimitException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(429, ex.getStatusCode());
        assertEquals("Rate limit exceeded", ex.getMessage());
    }

    @Test
    void test500WithNullBody() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayServerException ex = assertThrows(SePayServerException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(500, ex.getStatusCode());
        assertEquals("Server error", ex.getMessage());
    }

    @Test
    void test403ThrowsGenericApiException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn(null);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(403, ex.getStatusCode());
        assertEquals("API error", ex.getMessage());
    }

    @Test
    void testInterruptedExceptionDuringRequest() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new InterruptedException("interrupted"));

        SePayHttpClient client = createClient();
        SePayException ex = assertThrows(SePayException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertTrue(ex.getMessage().contains("interrupted"));
    }

    @Test
    void testPostWithUnserializableBody() throws Exception {
        // Create ObjectMapper that will fail
        ObjectMapper failingMapper = mock(ObjectMapper.class);
        com.fasterxml.jackson.core.JsonProcessingException jsonEx =
            new com.fasterxml.jackson.databind.JsonMappingException(null, "Cannot serialize");
        when(failingMapper.writeValueAsString(any())).thenThrow(jsonEx);

        SePayHttpClient client = new SePayHttpClient(config, failingMapper, mockHttpClient);
        SePayException ex = assertThrows(SePayException.class, () ->
            client.post("https://api.example.com/test", new Object(), ApiResponse.class)
        );

        assertTrue(ex.getMessage().contains("Failed to serialize"));
    }

    @Test
    void testDefaultConstructor() {
        SePayHttpClient client = new SePayHttpClient(config);
        assertNotNull(client);
        assertNotNull(client.getAuthHeader());
    }

    @Test
    void testSleepBeforeRetry_interrupted() throws Exception {
        SePayHttpClient client = createClient();

        // Access sleepBeforeRetry via reflection
        java.lang.reflect.Method method = SePayHttpClient.class.getDeclaredMethod(
            "sleepBeforeRetry", int.class);
        method.setAccessible(true);

        // Interrupt the current thread before calling
        Thread.currentThread().interrupt();

        // Should catch InterruptedException and restore interrupt flag
        method.invoke(client, 0);

        // Verify interrupt flag is restored
        assertTrue(Thread.interrupted()); // clears the flag
    }

    @Test
    void testZeroRetries_noRetryLoop() throws Exception {
        // Test with maxRetries=0 to cover loop boundary
        SePayClientConfig zeroRetryConfig = SePayClientConfig.builder("SP-TEST-123", "secret-key-456")
            .environment(Environment.SANDBOX)
            .maxRetries(0)
            .build();

        SePayHttpClient client = new SePayHttpClient(zeroRetryConfig, objectMapper, mockHttpClient);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"success\":true}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        ApiResponse result = client.get("https://api.example.com/test", ApiResponse.class);
        assertTrue(result.isSuccess());
        verify(mockHttpClient, times(1)).send(any(), any());
    }

    @Test
    void test201StatusSuccess() throws Exception {
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn("{\"success\":true}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        ApiResponse result = client.get("https://api.example.com/test", ApiResponse.class);

        assertTrue(result.isSuccess());
    }

    @Test
    void test403WithErrorMessage() throws Exception {
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn("{\"message\":\"Forbidden access\"}");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        SePayHttpClient client = createClient();
        SePayApiException ex = assertThrows(SePayApiException.class, () ->
            client.get("https://api.example.com/test", ApiResponse.class)
        );

        assertEquals(403, ex.getStatusCode());
        assertEquals("Forbidden access", ex.getMessage());
    }

    // Helper class for POST tests
    static class TestRequest {
        private String field;

        TestRequest(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}
