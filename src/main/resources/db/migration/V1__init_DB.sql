CREATE TABLE member
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          TEXT,
    surname       TEXT,
    email         TEXT      NOT NULL UNIQUE,
    password      TEXT      NOT NULL,
    birthday      DATE,
    role          TEXT      NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE post
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id     UUID REFERENCES member,
    text          TEXT,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE img
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    img           BYTEA     NOT NULL,
    post_id       UUID REFERENCES post,
    member_id     UUID REFERENCES member,
    is_avatar      BOOLEAN,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE comment
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id     UUID REFERENCES member,
    text          TEXT      NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);