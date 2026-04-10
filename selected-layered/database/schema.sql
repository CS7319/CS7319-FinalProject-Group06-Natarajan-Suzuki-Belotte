BEGIN TRANSACTION;

DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS groups;

CREATE TABLE users (
    email           varchar(256)  PRIMARY KEY,
    name            varchar(256)  NOT NULL,
    pronoun         varchar(50),
    password_hash   varchar(256)  NOT NULL,
    role            varchar(50)   NOT NULL,
    profile_picture varchar(512),
    location        varchar(256),
    about_me        varchar(1000),
    category_types  text[]        NOT NULL DEFAULT '{}',
    group_ids       integer[]     NOT NULL DEFAULT '{}',
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- email is the primary key so it is already indexed; this named index makes intent explicit
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
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
    id              serial        PRIMARY KEY,
    title           varchar(256)  NOT NULL,
    description     varchar(1000) NOT NULL,
    location        varchar(256)  NOT NULL,
    start_datetime  timestamp     NOT NULL,
    end_datetime    timestamp     NOT NULL,
    organizer_email varchar(256)  NOT NULL REFERENCES users(email),
    capacity        int           NOT NULL DEFAULT 0,
    event_picture   varchar(512),
    event_type      varchar(10)   NOT NULL DEFAULT 'PUBLIC',
    group_id        int           REFERENCES groups(id),
    category_types  text[]        NOT NULL DEFAULT '{}'
);

-- Seed categories
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
