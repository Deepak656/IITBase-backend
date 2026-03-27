ALTER TABLE jobseekers
    ADD COLUMN IF NOT EXISTS is_verified  BOOLEAN   NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS verified_at  TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_jobseekers_is_verified
    ON jobseekers(is_verified);