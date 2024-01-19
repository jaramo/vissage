CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS "user"
(
    id         uuid default uuid_generate_v4() primary key,
    nickname   varchar unique not null,
    created_at timestamp      not null
);

CREATE TABLE IF NOT EXISTS "message"
(
    id          uuid primary key,
    sender_id   uuid      not null references "user" (id),
    receiver_id uuid      not null references "user" (id),
    content     varchar   not null,
    created_at  timestamp not null,
    metadata    json
);

create index sender_receiver on message (sender_id, receiver_id, created_at);
