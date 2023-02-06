CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION define_roles_after_adding_new_member()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    INSERT INTO member_role (member_id, role)
    VALUES (NEW.id, 'USER'::role);
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER on_adding_new_member
    AFTER INSERT
    ON member
    FOR EACH ROW
EXECUTE PROCEDURE define_roles_after_adding_new_member();

INSERT INTO member (alias, name, surname, login, password, birthday)
VALUES ('main-admin', 'Konstantin', 'Chirko', 'admin', crypt('1', gen_salt('bf')), '2000-11-04'),
       ('main-creator', 'Denis', 'Tyrbal', 'creator', crypt('2', gen_salt('bf')), '2000-12-02'),
       ('main-user', 'Anna', 'Belous', 'user', crypt('3', gen_salt('bf')), '2002-07-07');

UPDATE member_role
SET role = 'ADMIN'
WHERE member_id = (SELECT id
                   FROM member
                   WHERE alias = 'main-admin' AND name = 'Konstantin' AND surname = 'Chirko' AND login = 'admin');

UPDATE member_role
SET role = 'CREATOR'
WHERE member_id = (SELECT id
                   FROM member
                   WHERE alias = 'main-creator' AND name = 'Denis' AND surname = 'Tyrbal' AND login = 'creator');

UPDATE member_role
SET role = 'USER'
WHERE member_id = (SELECT id
                   FROM member
                   WHERE alias = 'main-user' AND name = 'Anna' AND surname = 'Belous' AND login = 'user');