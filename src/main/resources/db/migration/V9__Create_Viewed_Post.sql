CREATE TABLE viewed_posts
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id     UUID REFERENCES member,
    post_id       UUID REFERENCES post
);
