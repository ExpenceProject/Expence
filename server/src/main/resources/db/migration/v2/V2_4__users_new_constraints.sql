-- Adding constraints to the users table
-- Note: id as the primary key is automatically NOT NULL in Postgres, so adding it was unnecessary

ALTER TABLE users
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN email TYPE email_address USING email::email_address;
