-- vendor-service schema — database: eventual_vendors
-- Owns: vendors, vendor_reviews, event_vendors
-- Note: added_by, reviewer_email are not foreign-keyed to user-service's database.
--       event_id is not foreign-keyed to event-service's database.
--       Data consistency is enforced at the application layer.

BEGIN TRANSACTION;

DROP TABLE IF EXISTS event_vendors;
DROP TABLE IF EXISTS vendor_reviews;
DROP TABLE IF EXISTS vendors;

CREATE TABLE vendors (
    id              serial        PRIMARY KEY,
    name            varchar(256)  NOT NULL,
    service_type    varchar(100)  NOT NULL,   -- e.g. Catering, Photography, Music, AV & Lighting
    contact_email   varchar(256),
    contact_phone   varchar(50),
    description     varchar(1000),
    website         varchar(512),
    is_preferred    boolean       NOT NULL DEFAULT false,
    added_by        varchar(256),             -- references users in eventual_users (no FK across DBs)
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendor_reviews (
    id              serial        PRIMARY KEY,
    vendor_id       int           NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    reviewer_email  varchar(256)  NOT NULL,   -- references users in eventual_users (no FK across DBs)
    comment         varchar(2000) NOT NULL,
    rating          int           CHECK (rating BETWEEN 1 AND 5),
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE event_vendors (
    id          serial    PRIMARY KEY,
    event_id    int       NOT NULL,           -- references events in eventual_events (no FK across DBs)
    vendor_id   int       NOT NULL REFERENCES vendors(id),
    created_at  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_id, vendor_id)
);

COMMIT TRANSACTION;
