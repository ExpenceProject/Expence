CREATE TABLE IF NOT EXISTS bills
(
    id         BIGINT PRIMARY KEY NOT NULL,
    version    BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    lender_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP          NOT NULL,

    FOREIGN KEY (lender_id) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE

);
