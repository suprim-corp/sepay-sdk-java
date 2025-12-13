package suprim.sepay.client;

import suprim.sepay.config.Environment;

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

    // Defaults
    private static final Environment DEFAULT_ENVIRONMENT = Environment.SANDBOX;
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 10000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 30000;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_RETRY_DELAY_MS = 1000;

    private SePayClientConfig(Builder builder) {
        this.merchantId = builder.merchantId;
        this.secretKey = builder.secretKey;
        this.environment = builder.environment;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.maxRetries = builder.maxRetries;
        this.retryDelayMs = builder.retryDelayMs;
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

    public static final class Builder {
        private final String merchantId;
        private final String secretKey;
        private Environment environment = DEFAULT_ENVIRONMENT;
        private int connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
        private int readTimeoutMs = DEFAULT_READ_TIMEOUT_MS;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private int retryDelayMs = DEFAULT_RETRY_DELAY_MS;

        private Builder(String merchantId, String secretKey) {
            if (merchantId == null || merchantId.trim().isEmpty()) {
                throw new IllegalArgumentException("merchantId cannot be null or empty");
            }
            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalArgumentException("secretKey cannot be null or empty");
            }
            this.merchantId = merchantId;
            this.secretKey = secretKey;
        }

        public Builder environment(Environment environment) {
            if (environment == null) {
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

        public SePayClientConfig build() {
            return new SePayClientConfig(this);
        }
    }
}
