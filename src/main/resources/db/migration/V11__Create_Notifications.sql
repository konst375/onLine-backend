CREATE TABLE notification
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    member_id     UUID REFERENCES member,
    target        UUID,
    type          TEXT,
    is_viewed     BOOLEAN
);

-- trigger on update notification
CREATE OR REPLACE FUNCTION update_notification_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE notification
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;

CREATE OR REPLACE TRIGGER on_update_notification
    AFTER UPDATE
    ON notification
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_notification_modified_date();