CREATE TABLE IF NOT EXISTS roles
(
    id         BIGINT PRIMARY KEY NOT NULL,
    version    BIGINT,
    name       VARCHAR(40)        NOT NULL UNIQUE,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP          NOT NULL
);

-- Insert default roles
INSERT INTO roles (id, version, name, created_at, updated_at)
VALUES (1, 0, 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 0, 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
