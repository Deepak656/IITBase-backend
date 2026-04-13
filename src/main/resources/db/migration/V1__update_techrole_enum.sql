DO $$
BEGIN
    -- Add SOFTWARE_ENGINEER if not exists
    IF NOT EXISTS (
        SELECT 1
        FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'tech_role'
        AND e.enumlabel = 'SOFTWARE_ENGINEER'
    ) THEN
ALTER TYPE tech_role ADD VALUE 'SOFTWARE_ENGINEER';
END IF;

    -- Add ANALYST if not exists
    IF NOT EXISTS (
        SELECT 1
        FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'tech_role'
        AND e.enumlabel = 'ANALYST'
    ) THEN
ALTER TYPE tech_role ADD VALUE 'ANALYST';
END IF;

    -- Add PRODUCT_ANALYST if not exists
    IF NOT EXISTS (
        SELECT 1
        FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'tech_role'
        AND e.enumlabel = 'PRODUCT_ANALYST'
    ) THEN
ALTER TYPE tech_role ADD VALUE 'PRODUCT_ANALYST';
END IF;

END $$;