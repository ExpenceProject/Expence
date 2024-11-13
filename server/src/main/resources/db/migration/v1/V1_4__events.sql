CREATE TABLE events
(
    id               BIGINT PRIMARY KEY NOT NULL,
    version          BIGINT,
    created_at       TIMESTAMP          NOT NULL,
    updated_at       TIMESTAMP          NOT NULL,
    serialized_event TEXT               NOT NULL,
    event_type       VARCHAR(255)       NOT NULL,
    event_status     VARCHAR(50)        NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_event_status ON events (event_status);