CREATE TABLE IF NOT EXISTS expenses
(
    id         BIGINT PRIMARY KEY NOT NULL,
    version    BIGINT NOT NULL,
    borrower_id BIGINT NOT NULL,
    amount DECIMAL(6, 2) NOT NULL CHECK (amount >= 0),
    bill_id BIGINT NOT NULL,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP          NOT NULL,

    FOREIGN KEY (borrower_id) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (bill_id) REFERENCES bills (id) ON DELETE CASCADE
);
