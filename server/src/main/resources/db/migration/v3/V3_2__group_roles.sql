CREATE TABLE IF NOT EXISTS group_roles
(
    id         BIGINT PRIMARY KEY,
    version    BIGINT      NOT NULL,
    name       VARCHAR(40) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL
);

-- Insert default roles
INSERT INTO group_roles (id, version, name, created_at, updated_at)
VALUES (1, 0, 'ROLE_OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 0, 'ROLE_MEMBER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

