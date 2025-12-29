package suprim.sepay.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import suprim.sepay.enums.TransferType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Webhook payload received from SePay platform.
 * Immutable DTO - use builder for construction.
 */
public class SePayWebhookData {

    @NotNull(message = "Transaction ID is required")
    @Positive(message = "Transaction ID must be positive")
    private Long id;

    @NotBlank(message = "Gateway is required")
    private String gateway;

    @NotNull(message = "Transaction date is required")
    @JsonProperty("transactionDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private String subAccount;

    private String code;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Transfer type is required")
    private TransferType transferType;

    private String description;

    @NotNull(message = "Transfer amount is required")
    @Positive(message = "Transfer amount must be positive")
    private Long transferAmount;

    private String referenceCode;

    private Long accumulated;

    // Default constructor for Jackson
    public SePayWebhookData() {
    }

    // Constructor for Builder
    private SePayWebhookData(Builder builder) {
        this.id = builder.id;
        this.gateway = builder.gateway;
        this.transactionDate = builder.transactionDate;
        this.accountNumber = builder.accountNumber;
        this.subAccount = builder.subAccount;
        this.code = builder.code;
        this.content = builder.content;
        this.transferType = builder.transferType;
        this.description = builder.description;
        this.transferAmount = builder.transferAmount;
        this.referenceCode = builder.referenceCode;
        this.accumulated = builder.accumulated;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getGateway() {
        return gateway;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSubAccount() {
        return subAccount;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public String getDescription() {
        return description;
    }

    public Long getTransferAmount() {
        return transferAmount;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public Long getAccumulated() {
        return accumulated;
    }

    // Setters for Jackson
    public void setId(Long id) {
        this.id = id;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTransferAmount(Long transferAmount) {
        this.transferAmount = transferAmount;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public void setAccumulated(Long accumulated) {
        this.accumulated = accumulated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String gateway;
        private LocalDateTime transactionDate;
        private String accountNumber;
        private String subAccount;
        private String code;
        private String content;
        private TransferType transferType;
        private String description;
        private Long transferAmount;
        private String referenceCode;
        private Long accumulated;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder gateway(String gateway) {
            this.gateway = gateway;
            return this;
        }

        public Builder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder subAccount(String subAccount) {
            this.subAccount = subAccount;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder transferType(TransferType transferType) {
            this.transferType = transferType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder transferAmount(Long transferAmount) {
            this.transferAmount = transferAmount;
            return this;
        }

        public Builder referenceCode(String referenceCode) {
            this.referenceCode = referenceCode;
            return this;
        }

        public Builder accumulated(Long accumulated) {
            this.accumulated = accumulated;
            return this;
        }

        public SePayWebhookData build() {
            return new SePayWebhookData(this);
        }
    }
}
