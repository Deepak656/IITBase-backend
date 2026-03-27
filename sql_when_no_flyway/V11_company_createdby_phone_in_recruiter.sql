-- 1. Work email + phone on recruiters
ALTER TABLE recruiters
  ADD COLUMN IF NOT EXISTS work_email VARCHAR(255),
  ADD COLUMN IF NOT EXISTS phone      VARCHAR(30);

-- 2. Created-by on companies
ALTER TABLE companies
  ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT;