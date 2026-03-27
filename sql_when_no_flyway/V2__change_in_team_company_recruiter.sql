-- ============================================================
-- IITBase: Recruiter team management + company trust model
-- Run after existing migrations
-- ============================================================

-- 1. Add new columns to companies
ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS email_domain    VARCHAR(255),
    ADD COLUMN IF NOT EXISTS status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN IF NOT EXISTS reviewed_by     BIGINT;

-- Backfill existing verified companies
UPDATE companies SET status = 'VERIFIED' WHERE is_verified = true;
UPDATE companies SET status = 'PENDING'  WHERE is_verified = false;

-- Index for domain lookups (Path A trust flow)
CREATE INDEX IF NOT EXISTS idx_companies_email_domain ON companies(email_domain);
CREATE INDEX IF NOT EXISTS idx_companies_status       ON companies(status);

-- 2. Add role column to recruiters, drop boolean is_admin
ALTER TABLE recruiters
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'MEMBER';

-- Backfill existing admins
UPDATE recruiters SET role = 'ADMIN'  WHERE is_admin = true;
UPDATE recruiters SET role = 'MEMBER' WHERE is_admin = false;

-- Keep is_admin column for now (backward compat) — remove in next sprint

-- 3. Team join requests (Path B)
CREATE TABLE IF NOT EXISTS team_join_requests (
    id                          BIGSERIAL PRIMARY KEY,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP,

    user_id                     BIGINT       NOT NULL,
    company_id                  BIGINT       NOT NULL REFERENCES companies(id),
    message                     TEXT,
    work_email                  VARCHAR(255),
    status                      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    reviewed_by_recruiter_id    BIGINT,
    reviewed_at                 TIMESTAMP,
    rejection_reason            TEXT,

    CONSTRAINT uq_join_request_user_company UNIQUE (user_id, company_id)
);

CREATE INDEX IF NOT EXISTS idx_join_requests_company_status
    ON team_join_requests(company_id, status);

CREATE INDEX IF NOT EXISTS idx_join_requests_user
    ON team_join_requests(user_id);

-- 4. Recruiter invites
CREATE TABLE IF NOT EXISTS recruiter_invites (
    id                          BIGSERIAL PRIMARY KEY,
    created_at                  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP,

    token                       VARCHAR(255) NOT NULL UNIQUE,
    email                       VARCHAR(255) NOT NULL,
    company_id                  BIGINT       NOT NULL REFERENCES companies(id),
    invited_by_recruiter_id     BIGINT       NOT NULL,
    intended_role               VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    status                      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    expires_at                  TIMESTAMP    NOT NULL,
    accepted_at                 TIMESTAMP,
    accepted_by_user_id         BIGINT
);

CREATE INDEX IF NOT EXISTS idx_recruiter_invites_token
    ON recruiter_invites(token);

CREATE INDEX IF NOT EXISTS idx_recruiter_invites_company_status
    ON recruiter_invites(company_id, status);

CREATE INDEX IF NOT EXISTS idx_recruiter_invites_email
    ON recruiter_invites(email);

   ALTER TABLE recruiters DROP COLUMN is_admin;
