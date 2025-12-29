package suprim.sepay.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Order DTO representing a SePay payment order.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private String id;

    @JsonProperty("order_invoice_number")
    private String invoiceNumber;

    private OrderStatus status;

    private long amount;

    private String currency;

    @JsonProperty("customer_id")
    private String customerId;

    private String description;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("reference_code")
    private String referenceCode;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("transaction_status")
    private String transactionStatus;

    // Default constructor for Jackson
    public Order() {}

    // Getters
    public String getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public OrderStatus getStatus() { return status; }
    public long getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCustomerId() { return customerId; }
    public String getDescription() { return description; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getReferenceCode() { return referenceCode; }
    public String getTransactionId() { return transactionId; }
    public String getTransactionStatus() { return transactionStatus; }

    // Setters for Jackson
    public void setId(String id) { this.id = id; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setAmount(long amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setDescription(String description) { this.description = description; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
}
