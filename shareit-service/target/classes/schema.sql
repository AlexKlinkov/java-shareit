drop table if exists users cascade;
drop table if exists items cascade;
drop table if exists bookings cascade;
drop table if exists requests cascade;
drop table if exists comments cascade;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name  VARCHAR(255)                        NOT NULL,
    email VARCHAR(512)                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name         VARCHAR(255)                        NOT NULL,
    description  VARCHAR(512)                        NOT NULL,
    is_available boolean,
    owner_id     BIGINT references users (id),
    request_id   BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT references items (id),
    booker_id  BIGINT references users (id),
    status     varchar(64)                         NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description  VARCHAR                             NOT NULL,
    requestor_id BIGINT references users (id),
    created      TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text      VARCHAR(512)                        NOT NULL,
    item_id   BIGINT references items (id),
    author_id BIGINT references users (id),
    created   TIMESTAMP WITHOUT TIME ZONE
);
