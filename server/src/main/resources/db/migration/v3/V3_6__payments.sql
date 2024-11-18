CREATE TABLE IF NOT EXISTS payments
(
    id         BIGINT PRIMARY KEY NOT NULL,
    version    BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(6, 2) NOT NULL CHECK (amount >= 0),
    sender_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP          NOT NULL,

    FOREIGN KEY (receiver_id) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE
);
