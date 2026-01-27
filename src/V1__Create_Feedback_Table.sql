-- V1__Create_Feedback_Table.sql (Flyway Migration)

CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feedback_type VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_feedback_type (feedback_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add comments for clarity
ALTER TABLE feedback
COMMENT = 'Stores user feedback, feature requests, bug reports, and support requests';

-- Sample query to get feedback statistics
-- SELECT
--     feedback_type,
--     COUNT(*) as total,
--     SUM(CASE WHEN status = 'NEW' THEN 1 ELSE 0 END) as new_count,
--     SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_count,
--     SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) as resolved_count
-- FROM feedback
-- GROUP BY feedback_type;