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

CREATE TABLE chat
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    admin         UUID,
    name          TEXT
);

CREATE TABLE user_chat
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_id   UUID REFERENCES chat,
    member_id UUID REFERENCES member
);

CREATE TABLE img
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    img           BYTEA     NOT NULL,
    post_id       UUID REFERENCES post,
    member_id     UUID REFERENCES member,
    chat_id       UUID REFERENCES chat,
    is_avatar     BOOLEAN,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE comment
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id     UUID REFERENCES member,
    img_id        UUID REFERENCES img,
    post_id       UUID REFERENCES post,
    text          TEXT      NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);