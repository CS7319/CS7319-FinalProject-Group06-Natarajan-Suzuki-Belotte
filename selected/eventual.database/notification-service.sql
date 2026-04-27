-- notification-service schema — database: eventual_notifications
-- Owns: notifications
-- Note: recipient_email is not foreign-keyed to user-service's database.
--       Data consistency is enforced at the application layer.

BEGIN TRANSACTION;

DROP TABLE IF EXISTS notifications;

CREATE TABLE notifications (
    id              serial        PRIMARY KEY,
    recipient_email varchar(256)  NOT NULL,   -- references users in eventual_users (no FK across DBs)
    type            varchar(50)   NOT NULL,   -- maps to NotificationType enum
    title           varchar(255)  NOT NULL,
    message         text          NOT NULL,
    reference_id    varchar(50),              -- e.g. the event_id or group_id this notification is about
    reference_type  varchar(50),              -- EVENT, GROUP, JOIN_REQUEST
    is_read         boolean       NOT NULL DEFAULT FALSE,
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_email, created_at DESC);
CREATE INDEX idx_notifications_unread ON notifications(recipient_email, is_read) WHERE is_read = FALSE;

COMMIT TRANSACTION;
