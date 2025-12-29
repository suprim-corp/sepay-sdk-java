package suprim.sepay.entity;

import suprim.sepay.dto.SePayWebhookData;
import suprim.sepay.enums.TransferType;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for storing SePay webhook transactions.
 *
 * <p><strong>IMPORTANT:</strong> Transaction ID comes from SePay webhook payload,
 * NOT auto-generated. Do NOT use @GeneratedValue annotation.
 *
 * <p>Enable persistence via:
 * <pre>
 * spring.jpa.hibernate.ddl-auto=validate
 * </pre>
 */
@Entity
@Table(
    name = "sepay_transactions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
    },
    indexes = {
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_gateway", columnList = "gateway"),
        @Index(name = "idx_transfer_type", columnList = "transfer_type")
    }
)
public class SePayTransaction {

    /**
     * Transaction ID from SePay platform.
     * NOT auto-generated - comes from webhook payload.
     */
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "gateway", nullable = false, length = 100)
    private String gateway;

    /**
     * Transaction timestamp from bank.
     * Stored as TIMESTAMP for better querying.
     */
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "sub_account", length = 100)
    private String subAccount;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, length = 10)
    private TransferType transferType;

    /**
     * Full SMS message content from bank.
     * Larger size to accommodate full text.
     */
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "transfer_amount", nullable = false)
    private Long transferAmount;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    /**
     * Record creation timestamp (NOT from webhook).
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public SePayTransaction() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Long transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Factory method to create entity from DTO.
     */
    public static SePayTransaction fromWebhookData(SePayWebhookData data) {
        SePayTransaction entity = new SePayTransaction();
        entity.setId(data.getId());
        entity.setGateway(data.getGateway());
        entity.setTransactionDate(data.getTransactionDate());
        entity.setAccountNumber(data.getAccountNumber());
        entity.setSubAccount(data.getSubAccount());
        entity.setCode(data.getCode());
        entity.setContent(data.getContent());
        entity.setTransferType(data.getTransferType());
        entity.setDescription(data.getDescription());
        entity.setTransferAmount(data.getTransferAmount());
        entity.setReferenceCode(data.getReferenceCode());
        // Note: accumulated field NOT persisted
        return entity;
    }

    @Override
    public String toString() {
        return "SePayTransaction{" +
                "id=" + id +
                ", gateway='" + gateway + '\'' +
                ", transactionDate=" + transactionDate +
                ", transferType=" + transferType +
                ", transferAmount=" + transferAmount +
                '}';
    }
}
