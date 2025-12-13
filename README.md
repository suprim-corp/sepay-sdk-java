# SePay SDK for Java

Java SDK for SePay payment gateway - webhooks, checkout, and order management.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)

## Features

- **Checkout** - Generate payment forms with signature
- **Orders** - Retrieve, list, void, and cancel orders
- **Webhooks** - DTOs and utilities for webhook handling
- **Type-safe** - Enums, validation, and strong typing

## Installation

### Maven

```xml
<dependency>
    <groupId>suprim</groupId>
    <artifactId>sepay-sdk</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
implementation 'suprim:sepay-sdk:0.1.0-SNAPSHOT'
```

## Quick Start

### Initialize Client

```java
SePayClient client = SePayClient.builder("MERCHANT_ID", "SECRET_KEY")
    .environment(Environment.SANDBOX)  // or PRODUCTION
    .connectTimeout(10000)
    .build();
```

### Create Checkout

```java
// Build checkout request
CheckoutRequest checkout = client.newCheckout()
    .operation(Operation.PURCHASE)
    .amount(100000)  // VND
    .invoiceNumber("INV-2024-001")
    .description("Order payment")
    .paymentMethod(PaymentMethod.BANK_TRANSFER)
    .successUrl("https://mysite.com/success")
    .errorUrl("https://mysite.com/error")
    .build();

// Get form data for frontend
CheckoutFormData form = client.checkout().generateForm(checkout);
String actionUrl = form.getActionUrl();
Map<String, String> fields = form.getFormFields();

// Or generate HTML form directly
String htmlForm = client.checkout().buildHtmlForm(checkout, "Pay Now");
```

### Retrieve Order

```java
Order order = client.orders().retrieve("ord_123456");

System.out.println("Status: " + order.getStatus());
System.out.println("Amount: " + order.getAmount());
```

### List Orders

```java
OrderListResponse response = client.orders().list(
    OrderListRequest.builder()
        .status(OrderStatus.COMPLETED)
        .perPage(20)
        .fromDate(LocalDate.of(2024, 1, 1))
        .build()
);

for (Order order : response.getData()) {
    System.out.println(order.getInvoiceNumber() + ": " + order.getAmount());
}

// Pagination
if (response.hasNextPage()) {
    // Fetch next page
}
```

### Void Transaction

```java
Order voided = client.orders().voidTransaction("ord_123456", "Customer request");
```

### Cancel Order

```java
Order cancelled = client.orders().cancel("ord_123456");
```

## Webhook Handling

The SDK provides DTOs and utilities for processing SePay webhooks.

### Webhook Controller Example

```java
@RestController
public class WebhookController {

    private final TokenExtractor tokenExtractor = new TokenExtractor();
    private final PatternMatcher patternMatcher = new PatternMatcher();
    private final ObjectMapper objectMapper;

    @Value("${sepay.webhook.token}")
    private String expectedToken;

    @PostMapping("/api/sepay/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String body,
            HttpServletRequest request) throws Exception {

        // 1. Validate token
        String token = tokenExtractor.extractToken(request);
        if (!isValidToken(token, expectedToken)) {
            throw new SePayAuthenticationException("Invalid token");
        }

        // 2. Parse webhook data
        SePayWebhookData data = objectMapper.readValue(body, SePayWebhookData.class);

        // 3. Check for duplicate
        if (transactionExists(data.getId())) {
            throw new SePayDuplicateTransactionException(data.getId());
        }

        // 4. Extract identifier and process
        Optional<String> identifier = patternMatcher.extractIdentifier(
            data.getContent(), "SE"
        );

        if (identifier.isPresent()) {
            processPayment(identifier.get(), data);
        }

        return ResponseEntity.noContent().build();
    }
}
```

### Webhook Payload

```java
SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

Long id = data.getId();                    // Transaction ID
String gateway = data.getGateway();        // Bank name
LocalDateTime date = data.getTransactionDate();
Long amount = data.getTransferAmount();    // Amount in VND
TransferType type = data.getTransferType(); // IN or OUT
String content = data.getContent();        // Transaction content
```

## API Reference

### SePayClient

| Method | Description |
|--------|-------------|
| `builder(merchantId, secretKey)` | Create config builder |
| `create(config)` | Create client from config |
| `checkout()` | Get checkout resource |
| `orders()` | Get orders resource |
| `newCheckout()` | Create pre-configured checkout builder |

### CheckoutBuilder

| Method | Description |
|--------|-------------|
| `operation(Operation)` | PURCHASE or VERIFY |
| `amount(long)` | Amount in VND |
| `invoiceNumber(String)` | Invoice number (required for PURCHASE) |
| `description(String)` | Order description |
| `paymentMethod(PaymentMethod)` | CARD, BANK_TRANSFER, NAPAS_BANK_TRANSFER |
| `successUrl(String)` | Redirect URL on success |
| `errorUrl(String)` | Redirect URL on error |
| `cancelUrl(String)` | Redirect URL on cancel |
| `build()` | Build request with signature |

### OrderResource

| Method | Description |
|--------|-------------|
| `retrieve(orderId)` | Get single order |
| `list()` | List orders with defaults |
| `list(request)` | List orders with filters |
| `voidTransaction(orderId)` | Void completed transaction |
| `voidTransaction(orderId, reason)` | Void with reason |
| `cancel(orderId)` | Cancel pending order |

### OrderListRequest.Builder

| Method | Description |
|--------|-------------|
| `perPage(int)` | Results per page (default: 20) |
| `page(int)` | Page number (default: 1) |
| `status(OrderStatus)` | Filter by status |
| `customerId(String)` | Filter by customer |
| `fromDate(LocalDate)` | Filter from date |
| `toDate(LocalDate)` | Filter to date |
| `query(String)` | Search query |
| `sort(String)` | Sort order |

### Exceptions

| Exception | Description |
|-----------|-------------|
| `SePayException` | Base exception |
| `SePayApiException` | API error with status code |
| `SePayAuthenticationException` | 401 Unauthorized |
| `SePayRateLimitException` | 429 Too Many Requests |
| `SePayServerException` | 5xx Server Error |
| `SePayValidationException` | Validation error |
| `SePayWebhookException` | Webhook processing error |
| `SePayDuplicateTransactionException` | Duplicate transaction |

## Configuration Options

```java
SePayClientConfig config = SePayClient.builder("MERCHANT_ID", "SECRET_KEY")
    .environment(Environment.SANDBOX)  // Default: SANDBOX
    .connectTimeout(10000)             // Default: 10000ms
    .readTimeout(30000)                // Default: 30000ms
    .maxRetries(3)                     // Default: 3
    .retryDelay(1000)                  // Default: 1000ms
    .build();
```

## Security Best Practices

1. **Never hardcode credentials** - Use environment variables or secrets manager
   ```java
   String merchantId = System.getenv("SEPAY_MERCHANT_ID");
   String secretKey = System.getenv("SEPAY_SECRET_KEY");
   ```

2. **Don't log secrets** - Avoid logging client config, signatures, or auth headers

3. **Use HTTPS** - SDK only communicates with SePay over HTTPS

4. **Webhook token validation** - Use constant-time comparison to prevent timing attacks
   ```java
   MessageDigest.isEqual(provided.getBytes(), expected.getBytes());
   ```

5. **Validate webhook source** - Verify requests originate from SePay IPs in production

## Requirements

- Java 11+
- Jackson 2.18+ for JSON (included)
- Jakarta Servlet API 6.x (provided by your web framework)
- Jakarta Persistence API 3.x (optional, for entity class)
- Jakarta Validation API 3.x (optional, for DTO validation)

## License

MIT License - see [LICENSE](LICENSE) file.
