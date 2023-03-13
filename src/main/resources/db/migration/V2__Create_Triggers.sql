-- trigger on update comment
CREATE OR REPLACE FUNCTION update_comment_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE comment
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_comment
    AFTER UPDATE
    ON comment
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_comment_modified_date();

-- trigger in update img
CREATE OR REPLACE FUNCTION update_img_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE img
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_img
    AFTER UPDATE
    ON img
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_img_modified_date();

-- trigger on update member
CREATE OR REPLACE FUNCTION update_member_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE member
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_member
    AFTER UPDATE
    ON member
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_member_modified_date();

-- trigger on update post
CREATE OR REPLACE FUNCTION update_post_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE post
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_post
    AFTER UPDATE
    ON post
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_post_modified_date();