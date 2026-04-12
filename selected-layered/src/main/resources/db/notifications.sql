-- Notifications table
-- Run this once against the `eventual` database before starting the application.

CREATE TABLE IF NOT EXISTS notifications (
    id             SERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    type           VARCHAR(50)  NOT NULL,   -- maps to NotificationType enum
    title          VARCHAR(255) NOT NULL,
    message        TEXT         NOT NULL,
    reference_id   VARCHAR(50),             -- e.g. the event_id or group_id this notification is about
    reference_type VARCHAR(50),             -- 'EVENT' | 'GROUP' | 'JOIN_REQUEST'
    is_read        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index for fast per-user lookups (the most common query pattern)
CREATE INDEX IF NOT EXISTS idx_notifications_recipient
    ON notifications (recipient_email, created_at DESC);

-- Index for unread count queries
CREATE INDEX IF NOT EXISTS idx_notifications_unread
    ON notifications (recipient_email, is_read)
    WHERE is_read = FALSE;
