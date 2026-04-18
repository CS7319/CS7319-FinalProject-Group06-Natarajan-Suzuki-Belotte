-- user-service schema — database: eventual_users
-- Owns: users, groups, group_join_requests, categories

BEGIN TRANSACTION;

DROP TABLE IF EXISTS group_join_requests;
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

CREATE TABLE group_join_requests (
    id              serial        PRIMARY KEY,
    group_id        int           NOT NULL REFERENCES groups(id),
    requester_email varchar(256)  NOT NULL REFERENCES users(email),
    status          varchar(20)   NOT NULL DEFAULT 'PENDING',  -- PENDING, APPROVED, REJECTED
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (group_id, requester_email)
);

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
