-- SePay Transactions Table
-- IMPORTANT: id is NOT auto-increment - comes from webhook payload

CREATE TABLE sepay_transactions (
    id BIGINT PRIMARY KEY,  -- Transaction ID from SePay (NOT auto-generated)
    gateway VARCHAR(100) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    sub_account VARCHAR(100),
    code VARCHAR(100),
    content VARCHAR(500) NOT NULL,
    transfer_type VARCHAR(10) NOT NULL CHECK (transfer_type IN ('IN', 'OUT')),
    description VARCHAR(1000),
    transfer_amount BIGINT NOT NULL CHECK (transfer_amount > 0),
    reference_code VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for common queries
CREATE INDEX idx_sepay_transaction_date ON sepay_transactions(transaction_date);
CREATE INDEX idx_sepay_gateway ON sepay_transactions(gateway);
CREATE INDEX idx_sepay_transfer_type ON sepay_transactions(transfer_type);
CREATE INDEX idx_sepay_content ON sepay_transactions(content);

-- Comments for documentation
COMMENT ON TABLE sepay_transactions IS 'Stores webhook transactions from SePay payment gateway';
COMMENT ON COLUMN sepay_transactions.id IS 'Transaction ID assigned by SePay (NOT auto-generated)';
