-- support-service schema — database: eventual_support
-- Owns: support_tickets
-- Note: submitted_by is not foreign-keyed to user-service's database.
--       Data consistency is enforced at the application layer.

BEGIN TRANSACTION;

DROP TABLE IF EXISTS support_tickets;

CREATE TABLE support_tickets (
    id              serial        PRIMARY KEY,
    subject         varchar(255)  NOT NULL,
    content         text          NOT NULL,
    submitted_by    varchar(256)  NOT NULL,   -- references users in eventual_users (no FK across DBs)
    status          varchar(20)   NOT NULL DEFAULT 'UNRESOLVED',  -- UNRESOLVED, RESOLVED
    resolved_at     timestamp,
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_support_tickets_user ON support_tickets(submitted_by, created_at DESC);
CREATE INDEX idx_support_tickets_status ON support_tickets(status);

COMMIT TRANSACTION;
