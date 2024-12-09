CREATE TABLE IF NOT EXISTS members
(
    id            BIGINT PRIMARY KEY,
    version       BIGINT      NOT NULL,
    user_id       BIGINT      NOT NULL,
    nickname      VARCHAR(50) NOT NULL,
    group_role_id BIGINT      NOT NULL,
    group_id      BIGINT      NOT NULL,
    created_at    TIMESTAMP   NOT NULL,
    updated_at    TIMESTAMP   NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (group_role_id) REFERENCES group_roles (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE

);
