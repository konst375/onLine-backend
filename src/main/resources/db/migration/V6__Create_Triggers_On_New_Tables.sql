-- trigger on update community
CREATE OR REPLACE FUNCTION update_community_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE community
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_community
    AFTER UPDATE
    ON community
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_community_modified_date();

-- trigger on update friend
CREATE OR REPLACE FUNCTION update_friend_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE friend
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_friend
    AFTER UPDATE
    ON friend
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_friend_modified_date();

-- trigger on update tag
CREATE OR REPLACE FUNCTION update_tag_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE tag
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_tag
    AFTER UPDATE
    ON tag
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_tag_modified_date();
