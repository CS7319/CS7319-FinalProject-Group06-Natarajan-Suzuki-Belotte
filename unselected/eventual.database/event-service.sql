-- event-service schema — database: eventual_events
-- Owns: events, rsvp
-- Note: organizer_email and group_id are not foreign-keyed to other services' databases.
--       Data consistency is enforced at the application layer.

BEGIN TRANSACTION;

DROP TABLE IF EXISTS rsvp;
DROP TABLE IF EXISTS events;

CREATE TABLE events (
    id              serial        PRIMARY KEY,
    title           varchar(256)  NOT NULL,
    description     varchar(1000) NOT NULL,
    location        varchar(256)  NOT NULL,
    start_datetime  timestamp     NOT NULL,
    end_datetime    timestamp     NOT NULL,
    organizer_email varchar(256)  NOT NULL,   -- references users in eventual_users (no FK across DBs)
    capacity        int           NOT NULL DEFAULT 0,  -- 0 = unlimited
    event_picture   varchar(512),
    event_type      varchar(10)   NOT NULL DEFAULT 'PUBLIC',  -- PUBLIC, GROUP
    group_id        int,                                       -- references groups in eventual_users (no FK across DBs)
    category_types  text[]        NOT NULL DEFAULT '{}',
    modified_by     varchar(256),
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rsvp (
    id          serial        PRIMARY KEY,
    event_id    int           NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_email  varchar(256)  NOT NULL,   -- references users in eventual_users (no FK across DBs)
    status      varchar(20)   NOT NULL DEFAULT 'GOING',  -- GOING, WAITLISTED, CANCELLED
    created_at  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_id, user_email)
);

COMMIT TRANSACTION;
