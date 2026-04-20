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
        SELECT email FROM generated_users ORDER BY random() LIMIT 1
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
        SELECT email FROM generated_users ORDER BY random() LIMIT 1
    ) u ON true
    LEFT JOIN LATERAL (
        SELECT id FROM generated_groups ORDER BY random() LIMIT 1
    ) g ON true
    RETURNING id
)
select 1;

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

-- =========================================================
-- VENDORS
-- =========================================================
INSERT INTO vendors (
    name,
    service_type,
    contact_email,
    contact_phone,
    description,
    website,
    is_preferred,
    added_by,
    created_at,
    updated_at
)
SELECT
    prefix || ' ' || suffix AS name,
    service_type,
    lower(replace(prefix || '.' || suffix || gs::text, ' ', '')) || '@vendors.example.com' AS contact_email,
    '+1-555-' || lpad((1000 + gs)::text, 4, '0') AS contact_phone,
    'Auto-generated vendor specializing in ' || service_type || '.' AS description,
    'https://www.' || lower(replace(prefix || suffix, ' ', '')) || gs::text || '.example.com' AS website,
    (random() < 0.30) AS is_preferred,
    (
        SELECT email
        FROM users
        ORDER BY random()
        LIMIT 1
    ) AS added_by,
    NOW() - ((floor(random() * 90))::text || ' days')::interval,
    NOW() - ((floor(random() * 30))::text || ' days')::interval
FROM generate_series(1, 25) AS gs
CROSS JOIN LATERAL (
    SELECT
        (ARRAY[
            'Blue','Golden','Silver','Bright','Urban','Prime','Velvet','Echo',
            'Summit','Luna','North','Redwood','Crystal','Nova','Evergreen'
        ])[1 + floor(random() * 15)::int] AS prefix,
        (ARRAY[
            'Catering','Moments','Studios','Sound','Events','Collective','Hospitality',
            'Productions','Lights','Works','Design','Services','Media','Kitchen','Crew'
        ])[1 + floor(random() * 15)::int] AS suffix,
        (ARRAY[
            'Catering','Photography','Music','AV & Lighting','Decor',
            'Security','Transportation','Floristry','Venue Support','Event Staffing'
        ])[1 + floor(random() * 10)::int] AS service_type
) v;

-- =========================================================
-- VENDOR REVIEWS
-- 0 to a few reviews per vendor, from random users
-- =========================================================
INSERT INTO vendor_reviews (
    vendor_id,
    reviewer_email,
    comment,
    rating,
    created_at
)
SELECT
    v.id,
    u.email,
    (
        ARRAY[
            'Reliable and easy to work with.',
            'Very professional throughout the event.',
            'Strong communication and on-time delivery.',
            'Guests had a great experience with this vendor.',
            'Would consider booking again for future events.',
            'Service quality was solid and consistent.',
            'Setup was smooth and well coordinated.',
            'Met expectations and handled requests well.',
            'Great value for the level of service provided.',
            'Helped the event run more smoothly.'
        ]
    )[1 + floor(random() * 10)::int] AS comment,
    1 + floor(random() * 5)::int AS rating,
    NOW() - ((floor(random() * 120))::text || ' days')::interval
FROM vendors v
JOIN LATERAL (
    SELECT email
    FROM users
    ORDER BY random()
    LIMIT (1 + floor(random() * 4)::int)
) u ON true
ON CONFLICT DO NOTHING;

-- If you want to guarantee uniqueness of reviewer/vendor pairs even without a DB constraint:
-- replace the insert above with DISTINCT ON (v.id, u.email) wrapper if needed.

-- =========================================================
-- EVENT_VENDORS
-- Assign 0-3 vendors to each event, biased by event title/type only loosely
-- =========================================================
INSERT INTO event_vendors (
    event_id,
    vendor_id,
    created_at
)
SELECT DISTINCT
    e.id,
    v.id,
    NOW() - ((floor(random() * 45))::text || ' days')::interval
FROM events e
JOIN LATERAL (
    SELECT id
    FROM vendors
    ORDER BY random()
    LIMIT floor((random() + 1) * 4)::int   -- 0,1,2,3 vendors per event
) v ON true
ON CONFLICT (event_id, vendor_id) DO NOTHING;

-- =========================================================
-- SUPPORT TICKETS
-- Randomly created by users, some resolved
-- =========================================================
INSERT INTO support_tickets (
    subject,
    content,
    submitted_by,
    status,
    resolved_at,
    created_at,
    updated_at
)
SELECT
    subject_line,
    content_body,
    submitter.email,
    ticket_status,
    CASE
        WHEN ticket_status = 'RESOLVED'
        THEN created_ts + ((1 + floor(random() * 10))::text || ' days')::interval
        ELSE NULL
    END AS resolved_at,
    created_ts,
    CASE
        WHEN ticket_status = 'RESOLVED'
        THEN created_ts + ((1 + floor(random() * 10))::text || ' days')::interval
        ELSE created_ts + ((floor(random() * 5))::text || ' days')::interval
    END AS updated_at
FROM generate_series(1, 40) gs
CROSS JOIN LATERAL (
    SELECT email
    FROM users
    ORDER BY random()
    LIMIT 1
) submitter
CROSS JOIN LATERAL (
    SELECT
        (ARRAY[
            'Issue joining a group',
            'Problem updating profile',
            'Event RSVP not saving',
            'Notification appears duplicated',
            'Vendor assignment question',
            'Unable to upload event picture',
            'Public event not visible',
            'Group ownership clarification',
            'Calendar time looks incorrect',
            'Support request about ticket status'
        ])[1 + floor(random() * 10)::int] AS subject_line,
        (ARRAY[
            'I ran into an issue while using the platform and would like help resolving it.',
            'Something does not appear to be working as expected in my account.',
            'Please investigate this behavior and let me know what steps I should take.',
            'I encountered this issue during normal usage and it seems reproducible.',
            'This is affecting my ability to manage events and groups properly.',
            'I would appreciate guidance or a fix when possible.',
            'The problem started recently and has continued across sessions.',
            'I noticed inconsistent behavior and wanted to report it.',
            'Please review this issue and confirm whether it is expected behavior.',
            'This request is related to an event, group, or account workflow problem.'
        ])[1 + floor(random() * 10)::int] AS content_body,
        (ARRAY['UNRESOLVED','RESOLVED'])[1 + floor(random() * 2)::int] AS ticket_status,
        NOW() - ((floor(random() * 75))::text || ' days')::interval AS created_ts
) t;

COMMIT;
