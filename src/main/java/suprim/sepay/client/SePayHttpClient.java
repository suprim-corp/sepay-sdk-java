package suprim.sepay.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import suprim.sepay.exception.SePayApiException;
import suprim.sepay.exception.SePayException;
import suprim.sepay.exception.SePayRateLimitException;
import suprim.sepay.exception.SePayServerException;
import suprim.sepay.exception.SePayValidationException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * HTTP client for SePay API with retry logic and error handling.
 */
public class SePayHttpClient {

    private static final String USER_AGENT = "SePay-Java-SDK/0.2.0";
    private static final String CONTENT_TYPE = "application/json";

    private final HttpClient httpClient;
    private final SePayClientConfig config;
    private final String authHeader;
    private final ObjectMapper objectMapper;

    public SePayHttpClient(SePayClientConfig config) {
        this(config, new ObjectMapper());
    }

    public SePayHttpClient(SePayClientConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.authHeader = buildAuthHeader(config.getMerchantId(), config.getSecretKey());
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(config.getConnectTimeoutMs()))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    // For testing - allows injection of mock HttpClient
    SePayHttpClient(SePayClientConfig config, ObjectMapper objectMapper, HttpClient httpClient) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.authHeader = buildAuthHeader(config.getMerchantId(), config.getSecretKey());
        this.httpClient = httpClient;
    }

    /**
     * Execute GET request.
     */
    public <T> T get(String url, Class<T> responseType) {
        HttpRequest request = baseRequest(url)
            .GET()
            .build();
        return executeWithRetry(request, responseType);
    }

    /**
     * Execute POST request with JSON body.
     */
    public <T> T post(String url, Object body, Class<T> responseType) {
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new SePayException("Failed to serialize request body", e);
        }

        HttpRequest request = baseRequest(url)
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        return executeWithRetry(request, responseType);
    }

    /**
     * Execute GET request returning raw string response.
     */
    public String getRaw(String url) {
        HttpRequest request = baseRequest(url)
            .GET()
            .build();
        return executeWithRetryRaw(request);
    }

    private HttpRequest.Builder baseRequest(String url) {
        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
            .header("Authorization", authHeader)
            .header("Content-Type", CONTENT_TYPE)
            .header("Accept", CONTENT_TYPE)
            .header("User-Agent", USER_AGENT);
    }

    private <T> T executeWithRetry(HttpRequest request, Class<T> responseType) {
        String responseBody = executeWithRetryRaw(request);
        try {
            return objectMapper.readValue(responseBody, responseType);
        } catch (JsonProcessingException e) {
            throw new SePayException("Failed to parse response: " + responseBody, e);
        }
    }

    private String executeWithRetryRaw(HttpRequest request) {
        int maxRetries = config.getMaxRetries();
        SePayException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                String body = response.body();

                if (statusCode >= 200 && statusCode < 300) {
                    return body != null ? body : "";
                }

                // Not a success - check if retryable
                if (!isRetryable(statusCode) || attempt == maxRetries) {
                    throw mapStatusToException(statusCode, body);
                }

                // Retry with exponential backoff
                sleepBeforeRetry(attempt);

            } catch (IOException e) {
                lastException = new SePayException("HTTP request failed: " + e.getMessage(), e);
                if (attempt == maxRetries) {
                    throw lastException;
                }
                sleepBeforeRetry(attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SePayException("Request interrupted", e);
            }
        }

        // Should not reach here, but just in case
        throw lastException != null ? lastException : new SePayException("Request failed after retries");
    }

    private boolean isRetryable(int statusCode) {
        return statusCode == 429 || statusCode >= 500;
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            // Exponential backoff: delay * (attempt + 1)
            long delay = (long) config.getRetryDelayMs() * (attempt + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private SePayApiException mapStatusToException(int statusCode, String responseBody) {
        String errorMessage = parseErrorMessage(responseBody);
        String errorCode = parseErrorCode(responseBody);

        switch (statusCode) {
            case 400:
                return new SePayApiException(
                    errorMessage != null ? errorMessage : "Validation error",
                    statusCode,
                    errorCode
                );
            case 401:
                return new SePayApiException(
                    errorMessage != null ? errorMessage : "Authentication failed",
                    statusCode,
                    errorCode
                );
            case 404:
                return new SePayApiException(
                    errorMessage != null ? errorMessage : "Resource not found",
                    statusCode,
                    "NOT_FOUND"
                );
            case 429:
                Long retryAfter = parseRetryAfter(responseBody);
                return new SePayRateLimitException(
                    errorMessage != null ? errorMessage : "Rate limit exceeded",
                    retryAfter
                );
            default:
                if (statusCode >= 500) {
                    return new SePayServerException(
                        errorMessage != null ? errorMessage : "Server error",
                        statusCode
                    );
                }
                return new SePayApiException(
                    errorMessage != null ? errorMessage : "API error",
                    statusCode,
                    errorCode
                );
        }
    }

    private String parseErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        try {
            ApiErrorResponse error = objectMapper.readValue(responseBody, ApiErrorResponse.class);
            return error.getMessage();
        } catch (JsonProcessingException e) {
            return responseBody;
        }
    }

    private String parseErrorCode(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        try {
            ApiErrorResponse error = objectMapper.readValue(responseBody, ApiErrorResponse.class);
            return error.getError();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Long parseRetryAfter(String responseBody) {
        // Could parse from response headers or body if provided
        return null;
    }

    private static String buildAuthHeader(String merchantId, String secretKey) {
        String credentials = merchantId + ":" + secretKey;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    // Getter for testing
    String getAuthHeader() {
        return authHeader;
    }
}
