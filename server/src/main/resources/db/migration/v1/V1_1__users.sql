-- https://stackoverflow.com/questions/71972926/intellij-idea-cannot-format-sql-code-properly
CREATE TABLE IF NOT EXISTS users
(
    id           BIGINT PRIMARY KEY NOT NULL,
    version      BIGINT,
    email        VARCHAR(50)        NOT NULL UNIQUE,
    password     VARCHAR(255)       NOT NULL,
    first_name   VARCHAR(50)        NOT NULL,
    last_name    VARCHAR(50)        NOT NULL,
    phone_number VARCHAR(20),
    image_key    VARCHAR(255),
    created_at   TIMESTAMP          NOT NULL,
    updated_at   TIMESTAMP          NOT NULL
);
