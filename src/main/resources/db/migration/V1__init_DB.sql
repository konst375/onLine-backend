CREATE TYPE role AS ENUM ('ADMIN', 'CREATOR', 'USER');

CREATE TABLE member
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alias         TEXT,
    name          TEXT,
    surname       TEXT,
    email         TEXT NOT NULL UNIQUE,
    password      TEXT NOT NULL,
    birthday      DATE,
    role          ROLE NOT NULL    DEFAULT 'USER',
    create_date   DATE NOT NULL    DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL    DEFAULT CURRENT_DATE
);

CREATE TABLE post
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id     UUID REFERENCES member,
    text          TEXT,
    create_date   DATE NOT NULL    DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL    DEFAULT CURRENT_DATE
);

CREATE TABLE img
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    img           BYTEA NOT NULL,
    post_id       UUID REFERENCES post,
    create_date   DATE  NOT NULL   DEFAULT CURRENT_DATE,
    modified_date DATE  NOT NULL   DEFAULT CURRENT_DATE
);

ALTER TABLE member
    ADD COLUMN avatar UUID REFERENCES img;

CREATE TABLE comment
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id     UUID REFERENCES member,
    text          TEXT NOT NULL,
    create_date   DATE NOT NULL    DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL    DEFAULT CURRENT_DATE
);