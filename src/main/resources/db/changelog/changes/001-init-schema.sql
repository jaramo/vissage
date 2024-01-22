CREATE TABLE IF NOT EXISTS "user"
(
    id         uuid      primary key,
    nickname   varchar   not null unique,
    created_at timestamp default current_timestamp
);

CREATE TABLE IF NOT EXISTS "message"
(
    id           uuid      primary key,
    sender_id    uuid      not null references "user" (id),
    receiver_id  uuid      not null references "user" (id),
    content      varchar   not null,
    sent_at      timestamp not null,
    delivered_at timestamp,
    read_at      timestamp,
    created_at   timestamp default current_timestamp
);

create index sender_receiver on message (sender_id, receiver_id, sent_at);
