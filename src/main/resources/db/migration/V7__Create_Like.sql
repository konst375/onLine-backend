CREATE TABLE likes
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    member_id     UUID REFERENCES member,
    post_id       UUID REFERENCES post,
    img_id        UUID REFERENCES img,
    comment_id    UUID REFERENCES comment
);

-- trigger on update likes
CREATE OR REPLACE FUNCTION update_likes_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE likes
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_likes
    AFTER UPDATE
    ON likes
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_likes_modified_date();