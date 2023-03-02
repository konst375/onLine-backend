CREATE TABLE common_token
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token TEXT,
    member_id UUID REFERENCES member,
    expire_date DATE
);