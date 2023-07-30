CREATE TABLE user_activity
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    activity_date DATE      NOT NULL,
    member_id     UUID REFERENCES member
);

CREATE TABLE tag_scores
(
    id            UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    member_id     UUID REFERENCES member,
    tag_id        UUID REFERENCES tag,
    scores        INT       NOT NULL DEFAULT 0
);

-- trigger on update user_activity
CREATE OR REPLACE FUNCTION update_user_activity_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE user_activity
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;

CREATE OR REPLACE TRIGGER on_update_user_activity
    AFTER UPDATE
    ON user_activity
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_user_activity_modified_date();

-- trigger on update user_activity
CREATE OR REPLACE FUNCTION update_tag_scores_modified_date()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE tag_scores
    SET modified_date = now()::TIMESTAMP
    WHERE id = NEW.id;
END;
$$;
CREATE OR REPLACE TRIGGER on_update_tag_scores
    AFTER UPDATE
    ON tag_scores
    FOR EACH ROW
    WHEN (pg_trigger_depth() = 1)
EXECUTE PROCEDURE update_tag_scores_modified_date();