BEGIN;

-- =========================================================
-- NAME POOLS (procedural building blocks)
-- =========================================================
WITH first_names AS (
    SELECT unnest(ARRAY[
        'Nova','Milo','Iris','Orion','Sadie','Felix','Luna','Ethan','Ava','Noah',
        'Zoe','Leo','Maya','Ezra','Nina','Kai','Jade','Theo','Ruby','Owen'
    ]) AS name
),
last_names AS (
    SELECT unnest(ARRAY[
        'Ramirez','Bennett','Nguyen','Lee','Turner','Wright','Carter','Kim','Patel','Lopez',
        'Reed','Bailey','Rivera','Cooper','Morgan','Bell','Murphy','Foster','Ward','Brooks'
    ]) AS name
),

-- =========================================================
-- USERS (N = 50)
-- =========================================================
generated_users AS (
    INSERT INTO users (
        email, name, pronoun, password_hash, role,
        location, category_types
    )
    SELECT
        lower(fn.name || '.' || ln.name || i || '@example.com'),
        fn.name || ' ' || ln.name,
        (ARRAY['he/him','she/her','they/them'])[1 + floor(random()*3)],
        'hash_' || i,
        CASE WHEN random() < 0.1 THEN 'ADMIN' ELSE 'USER' END,
        (ARRAY['Dallas, TX','Plano, TX','Richardson, TX','Allen, TX'])[1 + floor(random()*4)],
        ARRAY[
            (ARRAY['Music','Sports','Technology','Food & Drink','Gaming'])[1 + floor(random()*5)]
        ]
    FROM generate_series(1,50) i
    CROSS JOIN LATERAL (
        SELECT name FROM first_names ORDER BY random() LIMIT 1
    ) fn
    CROSS JOIN LATERAL (
        SELECT name FROM last_names ORDER BY random() LIMIT 1
    ) ln
    RETURNING email
),

-- =========================================================
-- GROUPS (N = 10)
-- =========================================================
generated_groups AS (
    INSERT INTO groups (
        name, description, creator_email, owner_email, member_emails
    )
    SELECT
        'Group ' || i || ' - ' ||
            (ARRAY['Tech','Fitness','Food','Gaming','Travel'])[1 + floor(random()*5)],
        'Auto-generated group #' || i,
        u.email,
        u.email,
        ARRAY[u.email]
    FROM generate_series(1,10) i
    JOIN LATERAL (
        SELECT email FROM users ORDER BY random() LIMIT 1
    ) u ON true
    RETURNING id, creator_email
),

-- =========================================================
-- EVENTS (N = 30)
-- =========================================================
generated_events AS (
    INSERT INTO events (
        title, description, location,
        start_datetime, end_datetime,
        organizer_email, event_type, group_id
    )
    SELECT
        (ARRAY['Meetup','Workshop','Hangout','Session','Night'])[1 + floor(random()*5)]
            || ' #' || i,
        'Generated event ' || i,
        (ARRAY['Dallas','Plano','Richardson'])[1 + floor(random()*3)],
        NOW() + (i || ' hours')::interval,
        NOW() + ((i+2) || ' hours')::interval,
        u.email,
        CASE WHEN random() < 0.5 THEN 'GROUP' ELSE 'PUBLIC' END,
        g.id
    FROM generate_series(1,30) i
    JOIN LATERAL (
        SELECT email FROM users ORDER BY random() LIMIT 1
    ) u ON true
    LEFT JOIN LATERAL (
        SELECT id FROM groups ORDER BY random() LIMIT 1
    ) g ON true
    RETURNING id
)

-- =========================================================
-- RSVP (random participation)
-- =========================================================
INSERT INTO rsvp (event_id, user_email, status)
SELECT
    e.id,
    u.email,
    (ARRAY['GOING','WAITLISTED','CANCELLED'])[1 + floor(random()*3)]
FROM events e
JOIN users u ON random() < 0.2;  -- ~20% fill rate

-- =========================================================
-- GROUP JOIN REQUESTS
-- =========================================================
INSERT INTO group_join_requests (group_id, requester_email, status)
SELECT
    g.id,
    u.email,
    (ARRAY['PENDING','APPROVED','REJECTED'])[1 + floor(random()*3)]
FROM groups g
JOIN users u ON random() < 0.15;

-- =========================================================
-- NOTIFICATIONS (derived)
-- =========================================================
INSERT INTO notifications (
    recipient_email, type, title, message, reference_id, reference_type
)
SELECT
    u.email,
    'EVENT_REMINDER',
    'Reminder: Event #' || e.id,
    'Don''t forget your upcoming event!',
    e.id::text,
    'EVENT'
FROM users u
JOIN events e ON random() < 0.1;

COMMIT;