package suprim.sepay.client;

import suprim.sepay.config.Environment;
import suprim.sepay.config.UrlConfig;

import java.net.MalformedURLException;
import java.net.URL;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Configuration for SePay client.
 */
public final class SePayClientConfig {

    // Required
    private final String merchantId;
    private final String secretKey;

    // Optional with defaults
    private final Environment environment;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final int maxRetries;
    private final int retryDelayMs;
    private final boolean debugMode;
    private final String customApiBaseUrl;
    private final String customCheckoutBaseUrl;

    // Defaults
    private static final Environment DEFAULT_ENVIRONMENT = Environment.SANDBOX;
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 10000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 30000;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_RETRY_DELAY_MS = 1000;
    private static final boolean DEFAULT_DEBUG_MODE = false;

    private SePayClientConfig(Builder builder) {
        this.merchantId = builder.merchantId;
        this.secretKey = builder.secretKey;
        this.environment = builder.environment;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.maxRetries = builder.maxRetries;
        this.retryDelayMs = builder.retryDelayMs;
        this.debugMode = builder.debugMode;
        this.customApiBaseUrl = builder.customApiBaseUrl;
        this.customCheckoutBaseUrl = builder.customCheckoutBaseUrl;
    }

    public static Builder builder(String merchantId, String secretKey) {
        return new Builder(merchantId, secretKey);
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getRetryDelayMs() {
        return retryDelayMs;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Returns the API base URL. Uses custom URL if set, otherwise environment default.
     */
    public String getApiBaseUrl() {
        return nonNull(customApiBaseUrl) ? customApiBaseUrl
            : UrlConfig.getApiBaseUrl(environment);
    }

    /**
     * Returns the checkout base URL. Uses custom URL if set, otherwise environment default.
     */
    public String getCheckoutBaseUrl() {
        return nonNull(customCheckoutBaseUrl) ? customCheckoutBaseUrl
            : UrlConfig.getCheckoutBaseUrl(environment);
    }

    /**
     * Returns string representation with secret key redacted for security.
     */
    @Override
    public String toString() {
        return "SePayClientConfig{" +
                "merchantId='" + merchantId + '\'' +
                ", secretKey=<REDACTED>" +
                ", environment=" + environment +
                ", connectTimeoutMs=" + connectTimeoutMs +
                ", readTimeoutMs=" + readTimeoutMs +
                ", maxRetries=" + maxRetries +
                ", retryDelayMs=" + retryDelayMs +
                ", debugMode=" + debugMode +
                (nonNull(customApiBaseUrl) ? ", customApiBaseUrl=" + customApiBaseUrl : "") +
                (nonNull(customCheckoutBaseUrl) ? ", customCheckoutBaseUrl=" + customCheckoutBaseUrl : "") +
                '}';
    }

    public static final class Builder {
        private final String merchantId;
        private final String secretKey;
        private Environment environment = DEFAULT_ENVIRONMENT;
        private int connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
        private int readTimeoutMs = DEFAULT_READ_TIMEOUT_MS;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private int retryDelayMs = DEFAULT_RETRY_DELAY_MS;
        private boolean debugMode = DEFAULT_DEBUG_MODE;
        private String customApiBaseUrl;
        private String customCheckoutBaseUrl;

        private Builder(String merchantId, String secretKey) {
            if (isNull(merchantId) || merchantId.trim().isEmpty()) {
                throw new IllegalArgumentException("merchantId cannot be null or empty");
            }
            if (isNull(secretKey) || secretKey.trim().isEmpty()) {
                throw new IllegalArgumentException("secretKey cannot be null or empty");
            }
            this.merchantId = merchantId;
            this.secretKey = secretKey;
        }

        public Builder environment(Environment environment) {
            if (isNull(environment)) {
                throw new IllegalArgumentException("environment cannot be null");
            }
            this.environment = environment;
            return this;
        }

        public Builder connectTimeout(int ms) {
            if (ms <= 0) {
                throw new IllegalArgumentException("connectTimeout must be positive");
            }
            this.connectTimeoutMs = ms;
            return this;
        }

        public Builder readTimeout(int ms) {
            if (ms <= 0) {
                throw new IllegalArgumentException("readTimeout must be positive");
            }
            this.readTimeoutMs = ms;
            return this;
        }

        public Builder maxRetries(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("maxRetries cannot be negative");
            }
            this.maxRetries = count;
            return this;
        }

        public Builder retryDelay(int ms) {
            if (ms < 0) {
                throw new IllegalArgumentException("retryDelay cannot be negative");
            }
            this.retryDelayMs = ms;
            return this;
        }

        /**
         * Enables or disables debug mode for detailed logging.
         */
        public Builder debugMode(boolean enabled) {
            this.debugMode = enabled;
            return this;
        }

        /**
         * Sets custom API base URL (overrides environment default).
         * Useful for testing or custom deployments.
         *
         * @param url full base URL (e.g., "https://custom-api.example.com")
         */
        public Builder apiBaseUrl(String url) {
            if (nonNull(url) && !url.isEmpty()) {
                validateUrl(url);
                this.customApiBaseUrl = url.replaceAll("/+$", "");
            }
            return this;
        }

        /**
         * Sets custom checkout base URL (overrides environment default).
         *
         * @param url full base URL (e.g., "https://custom-checkout.example.com")
         */
        public Builder checkoutBaseUrl(String url) {
            if (nonNull(url) && !url.isEmpty()) {
                validateUrl(url);
                this.customCheckoutBaseUrl = url.replaceAll("/+$", "");
            }
            return this;
        }

        private void validateUrl(String url) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL: " + url, e);
            }
        }

        public SePayClientConfig build() {
            return new SePayClientConfig(this);
        }
    }
}
