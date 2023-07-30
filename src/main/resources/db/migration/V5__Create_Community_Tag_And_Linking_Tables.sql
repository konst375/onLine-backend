CREATE TABLE community
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          TEXT      NOT NULL,
    subject       TEXT,
    member_id     UUID REFERENCES member,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE tag
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tag_name      TEXT      NOT NULL UNIQUE,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE friendship
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id  UUID REFERENCES member,
    sender_id     UUID REFERENCES member,
    status        TEXT      NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL
);

CREATE TABLE follower
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id    UUID REFERENCES member,
    community_id UUID REFERENCES community
);

CREATE TABLE moderator
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id    UUID REFERENCES member,
    community_id UUID REFERENCES community
);

CREATE TABLE tag_community
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    community_id UUID REFERENCES community,
    tag_id       UUID REFERENCES tag
);

CREATE TABLE tag_post
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID REFERENCES post,
    tag_id  UUID REFERENCES tag
);

ALTER TABLE img
    ADD COLUMN community_id UUID REFERENCES community,
    ADD COLUMN is_cover     BOOLEAN;

ALTER TABLE post
    ADD COLUMN community_id UUID REFERENCES community;
