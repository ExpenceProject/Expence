CREATE TYPE invitation_status AS ENUM ('SENT', 'ACCEPTED', 'DECLINED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS invitations
(
    id         BIGINT PRIMARY KEY,
    version    BIGINT            NOT NULL,
    invitee_id BIGINT            NOT NULL,
    inviter_id BIGINT            NOT NULL,
    group_id   BIGINT            NOT NULL,
    status     invitation_status NOT NULL DEFAULT 'SENT',
    created_at TIMESTAMP         NOT NULL,
    updated_at TIMESTAMP         NOT NULL,

    FOREIGN KEY (invitee_id) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (inviter_id) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE
);
