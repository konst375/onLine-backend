CREATE TABLE message
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    sender        UUID REFERENCES member,
    chat_id       UUID REFERENCES chat,
    text          TEXT,
    is_viewed     BOOLEAN
);

-- trigger on update message
CREATE OR REPLACE FUNCTION update_message_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE message
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;

CREATE OR REPLACE TRIGGER on_update_message
    AFTER UPDATE
    ON message
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_message_modified_date();