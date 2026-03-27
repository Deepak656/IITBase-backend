ALTER TABLE email_otps
DROP CONSTRAINT email_otps_purpose_check;

ALTER TABLE email_otps
ADD CONSTRAINT email_otps_purpose_check
CHECK (purpose IN ('SIGNUP', 'RESET_PASSWORD', 'CHANGE_EMAIL', 'VERIFY_CURRENT_EMAIL'));