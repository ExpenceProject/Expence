CREATE TABLE IF NOT EXISTS `groups`
(
    id         BIGINT PRIMARY KEY NOT NULL,
    version    BIGINT NOT NULL,
    image_key    VARCHAR(255),
    name VARCHAR(50) NOT NULL,
    settled_down BOOLEAN NOT NULL DEFAULT 0,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP          NOT NULL,
);
