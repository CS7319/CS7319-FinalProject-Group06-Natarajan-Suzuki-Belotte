INSERT INTO users (email, name, pronoun, password_hash, role, location, about_me, category_types, group_ids)
VALUES
('alice@example.com', 'Alice Johnson', 'she/her', 'hash1', 'USER', 'Dallas, TX', 'Loves music and tech', ARRAY['Music','Technology'], ARRAY[1]),
('bob@example.com', 'Bob Smith', 'he/him', 'hash2', 'USER', 'Plano, TX', 'Fitness enthusiast', ARRAY['Sports','Health & Fitness'], ARRAY[1,2]),
('carol@example.com', 'Carol Lee', 'they/them', 'hash3', 'ADMIN', 'Richardson, TX', 'Organizer of events', ARRAY['Business & Networking'], ARRAY[1,2]),
('dave@example.com', 'Dave Kim', 'he/him', 'hash4', 'USER', 'Allen, TX', 'Food lover', ARRAY['Food & Drink'], ARRAY[2]);

INSERT INTO groups (id, name, description, creator_email, owner_email, member_emails, modified_by)
VALUES
(1, 'Tech Enthusiasts', 'Group for tech lovers', 'carol@example.com', 'carol@example.com',
 ARRAY['alice@example.com','bob@example.com','carol@example.com'], 'carol@example.com'),
(2, 'Fitness Club', 'Stay fit together', 'bob@example.com', 'bob@example.com',
 ARRAY['bob@example.com','carol@example.com','dave@example.com'], 'bob@example.com');

INSERT INTO events (id, title, description, location, start_datetime, end_datetime, organizer_email, capacity, event_type, group_id, category_types, modified_by)
VALUES
(1, 'AI Meetup', 'Discuss latest in AI', 'Dallas, TX',
 '2026-05-01 18:00', '2026-05-01 20:00',
 'carol@example.com', 50, 'GROUP', 1, ARRAY['Technology'], 'carol@example.com'),
(2, 'Morning Yoga', 'Start your day right', 'Plano, TX',
 '2026-05-02 07:00', '2026-05-02 08:00',
 'bob@example.com', 20, 'GROUP', 2, ARRAY['Health & Fitness'], 'bob@example.com'),
(3, 'Food Festival', 'Try amazing food', 'Richardson, TX',
 '2026-05-03 12:00', '2026-05-03 16:00',
 'alice@example.com', 0, 'PUBLIC', NULL, ARRAY['Food & Drink'], 'alice@example.com');

INSERT INTO rsvp (event_id, user_email, status)
VALUES
(1, 'alice@example.com', 'GOING'),
(1, 'bob@example.com', 'GOING'),
(2, 'carol@example.com', 'GOING'),
(2, 'dave@example.com', 'WAITLISTED'),
(3, 'alice@example.com', 'GOING'),
(3, 'dave@example.com', 'GOING');

INSERT INTO group_join_requests (group_id, requester_email, status)
VALUES
(1, 'dave@example.com', 'PENDING'),
(2, 'alice@example.com', 'APPROVED');