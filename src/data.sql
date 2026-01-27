-- Seed admin user (password: admin123)
INSERT INTO users (email, password, role, created_at, updated_at)
VALUES ('admin@iitbase.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO users (email, password, role, created_at, updated_at)
VALUES ('admin@iitbase.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO users (email, password, role, created_at, updated_at)
VALUES ('deepak@iitbase.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;


UPDATE users
SET role = 'JOB_SEEKER',
    updated_at = CURRENT_TIMESTAMP
WHERE email = 'deepak01962@gmail.com';

UPDATE jobs
SET tier_one_reason = 'Only IIT / NIT / Tier 1 college'
WHERE id = 1;