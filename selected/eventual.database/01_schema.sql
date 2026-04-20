BEGIN TRANSACTION;

DROP TABLE IF EXISTS support_tickets;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS group_join_requests;
DROP TABLE IF EXISTS rsvp;
DROP TABLE IF EXISTS event_vendors;
DROP TABLE IF EXISTS vendor_reviews;
DROP TABLE IF EXISTS vendors;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    email           varchar(256)  PRIMARY KEY,
    name            varchar(256)  NOT NULL,
    pronoun         varchar(50),
    password_hash   varchar(256)  NOT NULL,
    role            varchar(50)   NOT NULL,   -- USER, ADMIN
    profile_picture varchar(512),
    location        varchar(256),
    about_me        varchar(1000),
    category_types  text[]        NOT NULL DEFAULT '{}',
    group_ids       integer[]     NOT NULL DEFAULT '{}',
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_email ON users(email);

CREATE TABLE categories (
    id   serial       PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE,
    type varchar(100) NOT NULL
);

CREATE TABLE groups (
    id              serial        PRIMARY KEY,
    name            varchar(100)  NOT NULL UNIQUE,
    description     varchar(500)  NOT NULL,
    creator_email   varchar(256)  NOT NULL REFERENCES users(email),
    owner_email     varchar(256)  NOT NULL REFERENCES users(email),
    is_public       boolean       NOT NULL DEFAULT true,
    member_emails   text[]        NOT NULL DEFAULT '{}',
    modified_by     varchar(256)  REFERENCES users(email),
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
    id              serial        PRIMARY KEY,
    title           varchar(256)  NOT NULL,
    description     varchar(1000) NOT NULL,
    location        varchar(256)  NOT NULL,
    start_datetime  timestamp     NOT NULL,
    end_datetime    timestamp     NOT NULL,
    organizer_email varchar(256)  NOT NULL REFERENCES users(email),
    capacity        int           NOT NULL DEFAULT 0,  -- 0 = unlimited
    event_picture   varchar(512),
    event_type      varchar(10)   NOT NULL DEFAULT 'PUBLIC',  -- PUBLIC, GROUP
    group_id        int           REFERENCES groups(id),
    category_types  text[]        NOT NULL DEFAULT '{}',
    modified_by     varchar(256)  REFERENCES users(email),
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendors (
    id              serial        PRIMARY KEY,
    name            varchar(256)  NOT NULL,
    service_type    varchar(100)  NOT NULL,   -- e.g. Catering, Photography, Music, AV & Lighting
    contact_email   varchar(256),
    contact_phone   varchar(50),
    description     varchar(1000),
    website         varchar(512),
    is_preferred    boolean       NOT NULL DEFAULT false,
    added_by        varchar(256)  REFERENCES users(email),
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendor_reviews (
    id              serial        PRIMARY KEY,
    vendor_id       int           NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    reviewer_email  varchar(256)  NOT NULL REFERENCES users(email),
    comment         varchar(2000) NOT NULL,
    rating          int           CHECK (rating BETWEEN 1 AND 5),
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE event_vendors (
    id          serial    PRIMARY KEY,
    event_id    int       NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    vendor_id   int       NOT NULL REFERENCES vendors(id),
    created_at  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_id, vendor_id)
);

CREATE TABLE rsvp (
    id          serial        PRIMARY KEY,
    event_id    int           NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_email  varchar(256)  NOT NULL REFERENCES users(email),
    status      varchar(20)   NOT NULL DEFAULT 'GOING',  -- GOING, WAITLISTED, CANCELLED
    created_at  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_id, user_email)
);

CREATE TABLE group_join_requests (
    id              serial        PRIMARY KEY,
    group_id        int           NOT NULL REFERENCES groups(id),
    requester_email varchar(256)  NOT NULL REFERENCES users(email),
    status          varchar(20)   NOT NULL DEFAULT 'PENDING',  -- PENDING, APPROVED, REJECTED
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (group_id, requester_email)
);

CREATE TABLE notifications (
    id              serial        PRIMARY KEY,
    recipient_email varchar(256)  NOT NULL REFERENCES users(email),
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

CREATE TABLE support_tickets (
    id              serial        PRIMARY KEY,
    subject         varchar(255)  NOT NULL,
    content         text          NOT NULL,
    submitted_by    varchar(256)  NOT NULL REFERENCES users(email),
    status          varchar(20)   NOT NULL DEFAULT 'UNRESOLVED',  -- UNRESOLVED, RESOLVED
    resolved_at     timestamp,
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_support_tickets_user ON support_tickets(submitted_by, created_at DESC);
CREATE INDEX idx_support_tickets_status ON support_tickets(status);

INSERT INTO categories (name, type) VALUES
    ('Music',                  'Entertainment'),
    ('Sports',                 'Active'),
    ('Technology',             'Professional'),
    ('Food & Drink',           'Lifestyle'),
    ('Arts & Culture',         'Entertainment'),
    ('Business & Networking',  'Professional'),
    ('Health & Fitness',       'Active'),
    ('Travel & Adventure',     'Lifestyle'),
    ('Gaming',                 'Entertainment'),
    ('Education & Learning',   'Professional'),
    ('Comedy',                 'Entertainment'),
    ('Film & Media',           'Entertainment'),
    ('Outdoor Activities',     'Active'),
    ('Fashion & Beauty',       'Lifestyle'),
    ('Science & Innovation',   'Professional'),
    ('Photography',            'Creative'),
    ('Cooking',                'Lifestyle'),
    ('Writing & Literature',   'Creative'),
    ('Volunteering',           'Community'),
    ('Spirituality & Wellness','Lifestyle');

COMMIT TRANSACTION;
